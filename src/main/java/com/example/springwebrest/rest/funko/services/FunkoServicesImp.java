package com.example.springwebrest.rest.funko.services;

import com.example.springwebrest.config.websockets.WebSocketConfig;
import com.example.springwebrest.config.websockets.WebSocketHandler;
import com.example.springwebrest.rest.categoria.models.Categoria;
import com.example.springwebrest.rest.categoria.repository.CategoriaRepository;
import com.example.springwebrest.rest.funko.dto.FunkoCreateRequest;
import com.example.springwebrest.rest.funko.dto.FunkoResponseDto;
import com.example.springwebrest.rest.funko.dto.FunkoUpdateRequest;
import com.example.springwebrest.rest.funko.exceptions.FunkoBadRequest;
import com.example.springwebrest.rest.funko.exceptions.FunkoNotFound;
import com.example.springwebrest.rest.funko.mapper.FunkoMapper;
import com.example.springwebrest.rest.funko.models.Funko;
import com.example.springwebrest.rest.funko.repository.FunkoRepository;
import com.example.springwebrest.rest.storage.services.StorageService;
import com.example.springwebrest.websockets.notifications.dto.FunkoNotificationResponse;
import com.example.springwebrest.websockets.notifications.mapper.FunkoNotificationMapper;
import com.example.springwebrest.websockets.notifications.models.Notificacion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@CacheConfig(cacheNames = "funkos")
public class FunkoServicesImp implements FunkoServices {
    private final FunkoRepository funkoRepository;
    private final CategoriaRepository categoriaRepository;
    private final StorageService storageService;
    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper mapper;
    private final FunkoMapper funkoMapper;
    private final FunkoNotificationMapper funkoNotificationMapper;
    private WebSocketHandler webSocketService;

    @Autowired
    public FunkoServicesImp(FunkoRepository funkoRepository, CategoriaRepository categoriaRepository, WebSocketHandler webSocketHandler, StorageService storageService, WebSocketConfig webSocketConfig, ObjectMapper mapper, FunkoMapper funkoMapper, FunkoNotificationMapper funkoNotificationMapper) {
        this.funkoRepository = funkoRepository;
        this.categoriaRepository = categoriaRepository;
        this.storageService = storageService;
        this.webSocketConfig = webSocketConfig;
        webSocketService = webSocketConfig.webSocketRaquetasHandler();
        this.funkoNotificationMapper = funkoNotificationMapper;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.funkoMapper = funkoMapper;
    }


    @Override
    public Page<FunkoResponseDto> findAll(Optional<String> name, Optional<Double> price, Optional<Integer> quantity, Optional<String> categoria, Pageable pageable) {
        Specification<Funko> specCategoria = ((root, criteriaQuery, criteriaBuilder) -> categoria.map(c -> criteriaBuilder.equal(
                root.get("categoria").get("tipo"),
                c)).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))));
        Specification<Funko> specName = ((root, criteriaQuery, criteriaBuilder) -> name.map(n -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + n.toLowerCase() + "%")).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))));
        Specification<Funko> specPrice = ((root, criteriaQuery, criteriaBuilder) -> price.map(p -> criteriaBuilder.equal(
                root.get("price"),
                p)).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))));
        Specification<Funko> specQuantity = ((root, criteriaQuery, criteriaBuilder) -> quantity.map(q -> criteriaBuilder.equal(
                root.get("quantity"),
                q)).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))));
        Specification<Funko> spec = Specification.where(specCategoria).and(specName).and(specPrice).and(specQuantity);
        return funkoRepository.findAll(spec, pageable).map(funkoMapper::toFunko);

    }

    @Override
    @Cacheable(key = "#id")
    public FunkoResponseDto findById(Long id) {
        return funkoMapper.toFunko(funkoRepository.findById(id).orElseThrow(() -> new FunkoNotFound("El funko con id " + id + " no existe")));
    }

    @Override
    @CachePut(key = "#result.id")
    public FunkoResponseDto save(FunkoCreateRequest funko) {
        var categoria = checkCategoria(funko.getCategoria());
        var funkoToSave = funkoRepository.save(funkoMapper.toFunko(funko, categoria));
        onChange(Notificacion.Tipo.CREATE, funkoToSave);
        return funkoMapper.toFunko(funkoToSave);
    }

    @Override
    @CachePut(key = "#result.id")
    public FunkoResponseDto update(Long id, FunkoUpdateRequest funko) {
        var funkoActual = funkoRepository.findById(id).orElseThrow(() -> new FunkoNotFound("El funko con id " + id + " no existe"));
        Categoria categoria = null;

        if (funko.getCategoria() != null && !funko.getCategoria().isEmpty()) {
            categoria = checkCategoria(funko.getCategoria());
        } else {
            categoria = funkoActual.getCategoria();
        }

        var funkoToUpdate = funkoRepository.save(funkoMapper.toFunko(funko, funkoActual, categoria));
        onChange(Notificacion.Tipo.UPDATE, funkoToUpdate);
        return funkoMapper.toFunko(funkoToUpdate);
    }


    @Override
    @CachePut(key = "#id")
    public void deleteById(Long id) {
        var funko = funkoRepository.findById(id).orElseThrow(() -> new FunkoNotFound("El funko con id " + id + " no existe"));
        funkoRepository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE, funko);
    }

    @Override
    public void deleteAll() {
        funkoRepository.deleteAll();
    }

    public Categoria checkCategoria(String nombreCategoria) {
        var categoria = categoriaRepository.findByTipoEqualsIgnoreCase(nombreCategoria);
        // Buscamos la categoría por su nombre, debe existir y no estar borrada
        if (categoria.isEmpty() || !categoria.get().isActive()) {
            throw new FunkoBadRequest("La categoría " + nombreCategoria + " no existe o está borrada");
        }
        return categoria.get();
    }

    @Override
    public FunkoResponseDto updateImage(Long id, MultipartFile image, Boolean withUrl) {
        log.info("Actualizando imagen del funko con id: " + id);
        var funkoActual = funkoRepository.findById(id).orElseThrow(() -> new FunkoNotFound("El funko con id " + id + " no existe"));

        if(funkoActual.getImage() != null && !funkoActual.getImage().equals(Funko.IMAGE_DEFAULT)){
            storageService.delete(funkoActual.getImage());
        }
        String imageStored = storageService.store(image);

        String imgUrl = !withUrl ? imageStored : storageService.getUrl(imageStored);

        var funkoActualizado = Funko.builder()
                .id(funkoActual.getId())
                .name(funkoActual.getName())
                .price(funkoActual.getPrice())
                .quantity(funkoActual.getQuantity())
                .image(imgUrl)
                .categoria(funkoActual.getCategoria())
                .createdAt(funkoActual.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        var funkoToUpdate = funkoRepository.save(funkoActualizado);
        onChange(Notificacion.Tipo.UPDATE, funkoToUpdate);
        return funkoMapper.toFunko(funkoToUpdate);
    }

    public void onChange(Notificacion.Tipo tipo, Funko funko) {
        log.debug("Servicio de productos onChange con tipo: " + tipo + " y datos: " + funko);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketRaquetasHandler();
        }

        try {
            Notificacion<FunkoNotificationResponse> notificacion = new Notificacion<>(
                    "FUNKOS",
                    tipo,
                    funkoNotificationMapper.toFunkoNotificationDto(funko),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString((notificacion));

            log.info("Enviando mensaje a los clientes ws");
            // Enviamos el mensaje a los clientes ws con un hilo, si hay muchos clientes, puede tardar
            // no bloqueamos el hilo principal que atiende las peticiones http
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

    public void setWebSocketService(WebSocketHandler webSocketHandlerMock) {
        this.webSocketService = webSocketHandlerMock;
    }
}
