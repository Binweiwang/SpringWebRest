package com.example.springwebrest.funko.services;

import com.example.springwebrest.config.websockets.WebSocketConfig;
import com.example.springwebrest.config.websockets.WebSocketHandler;
import com.example.springwebrest.rest.categoria.dto.CategoriaRequest;
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
import com.example.springwebrest.rest.funko.services.FunkoServicesImp;
import com.example.springwebrest.rest.storage.services.StorageService;
import com.example.springwebrest.websockets.notifications.mapper.FunkoNotificationMapper;
import com.example.springwebrest.websockets.notifications.models.Notificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FunkoServicesTest {
    private final Categoria categoria = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "DISNEY", LocalDateTime.now(), LocalDateTime.now(), false);
    private final CategoriaRequest categoriaRequest = new CategoriaRequest("DISNEY", false);
    private final Funko funko = new Funko().builder()
            .id(1L)
            .name("Funko 1")
            .price(100.0)
            .quantity(10)
            .image("https://wallhaven.cc/w/85852j")
            .categoria(categoria)
            .build();
    private final List<Funko> ListaFunkos = List.of(
            new Funko(1L, "Funko 1", 100.0, 10, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", new Categoria(), LocalDateTime.now(), LocalDateTime.now()),
            new Funko(2L, "Funko 2", 200.0, 20, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", new Categoria(), LocalDateTime.now(), LocalDateTime.now()),
            new Funko(3L, "Funko 3", 300.0, 30, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", new Categoria(), LocalDateTime.now(), LocalDateTime.now())
    );
    private final FunkoUpdateRequest funkoUpdateRequest = FunkoUpdateRequest.builder()
            .name("Funko 1")
            .price(100.0)
            .quantity(10)
            .image("https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg")
            .categoria("DISNEY")
            .build();

    private final FunkoUpdateRequest funkoUpdateRequestCategoriaNull = FunkoUpdateRequest.builder()
            .name("Funko 1")
            .price(100.0)
            .quantity(10)
            .image("https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg")
            .categoria(null)
            .build();
    private final FunkoResponseDto funkoResponseDto = FunkoResponseDto.builder()
            .id(1L)
            .name("Funko 1")
            .price(100.0)
            .quantity(10)
            .image("https://wallhaven.cc/w/85852j")
            .categoria(categoria)
            .build();
    private final FunkoCreateRequest funkoCreateRequest = FunkoCreateRequest.builder()
            .name("Funko 1")
            .price(100.0)
            .quantity(10)
            .image("https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg")
            .categoria("DISNEY")
            .build();
    WebSocketHandler webSocketHandler = mock(WebSocketHandler.class);
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private StorageService storageService;
    @Mock
    private WebSocketConfig webSocketConfig;
    @Mock
    private FunkoNotificationMapper funkoNotificationMapper;
    @Mock
    private FunkoMapper funkoMapper;
    @Mock
    private FunkoRepository repository;

    @InjectMocks
    private FunkoServicesImp funkoService;


    @Test
    void findAll() {
        when(repository.findAll()).thenReturn(ListaFunkos);

        List<Funko> funkos = funkoService.findAll();

        assertAll("Obtener todos los funkos",
                () -> assertEquals(3, funkos.size()),
                () -> assertEquals("Funko 1", funkos.get(0).getName()),
                () -> assertEquals("Funko 2", funkos.get(1).getName()),
                () -> assertEquals("Funko 3", funkos.get(2).getName())
        );
    }

    @BeforeEach
    void setUp(){
        funkoService.setWebSocketService(webSocketHandler);
    }

    @Test
    void findById() {
        var funko = new Funko(1L, "Funko 1", 100.0, 10, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", new Categoria(), LocalDateTime.now(), LocalDateTime.now());

        when(repository.findById(1L)).thenReturn(Optional.of(funko));
        when(funkoMapper.toFunko(funko)).thenReturn(funkoResponseDto);
        when(repository.findById(99L)).thenThrow(FunkoNotFound.class);

        FunkoResponseDto funkoFindById = funkoService.findById(1L);

        assertAll("Obtener funko por id",
                () -> assertEquals("Funko 1", funkoFindById.getName()),
                () -> assertEquals(100.0, funkoFindById.getPrice()),
                () -> assertThrows(FunkoNotFound.class, () -> funkoService.findById(99L))
        );
    }

    @Test
    void findByIdNoExiste() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(FunkoNotFound.class, () -> funkoService.findById(1L));
    }
    @Test
    void save() {
        when(categoriaRepository.findByTipoEqualsIgnoreCase("DISNEY")).thenReturn(Optional.of(categoria));
        when(funkoMapper.toFunko(funkoCreateRequest, categoria)).thenReturn(funko);
        when(repository.save(any(Funko.class))).thenReturn(funko);
        when(funkoMapper.toFunko(any(Funko.class))).thenReturn(funkoResponseDto);

        // Act
        FunkoResponseDto result = funkoService.save(funkoCreateRequest);

        // Assert
        assertEquals(funkoResponseDto, result);
    }

    @Test
    void update() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(funko));
        when(categoriaRepository.findByTipoEqualsIgnoreCase(anyString())).thenReturn(Optional.of(categoria));
        when(funkoMapper.toFunko(funkoUpdateRequest, funko, categoria)).thenReturn(funko);
        when(repository.save(any(Funko.class))).thenReturn(funko);
        when(funkoMapper.toFunko(any(Funko.class))).thenReturn(funkoResponseDto);


        FunkoResponseDto funkoUpdated = funkoService.update(1L, funkoUpdateRequest);

        assertAll("Comprobar funko actualizado",
                () -> assertEquals("Funko 1", funkoUpdated.getName()),
                () -> assertEquals(100.0, funkoUpdated.getPrice()),
                () -> assertEquals(10, funkoUpdated.getQuantity())
                );

    }
    @Test
    void updateNoExiste(){
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(FunkoNotFound.class, () -> funkoService.update(1L, funkoUpdateRequest));
    }
    @Test
    void updateFunkoNull() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(funko));
        when(repository.save(any(Funko.class))).thenReturn(funko);
        when(funkoMapper.toFunko(any(FunkoUpdateRequest.class),any(Funko.class),any(Categoria.class))).thenReturn(funko);
        when(funkoMapper.toFunko(any(Funko.class))).thenReturn(funkoResponseDto);

        FunkoResponseDto funkoUpdated = funkoService.update(1L, funkoUpdateRequestCategoriaNull);

        assertAll("Comprobar funko actualizado",
                () -> assertEquals("Funko 1", funkoUpdated.getName()),
                () -> assertEquals(100.0, funkoUpdated.getPrice()),
                () -> assertEquals(10, funkoUpdated.getQuantity()),
                () -> assertEquals("DISNEY", funkoUpdated.getCategoria().getTipo())
        );

    }
    @Test
    void deleteById() {
        var funko = new Funko(1L, "Funko 1", 100.0, 10, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", new Categoria(), LocalDateTime.now(), LocalDateTime.now());
        when(repository.findById(1L)).thenReturn(Optional.of(funko));

        funkoService.deleteById(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteByIdNoExiste() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(FunkoNotFound.class, () -> funkoService.deleteById(1L));
    }

    @Test
    void deleteAll() {
        funkoService.deleteAll();
        verify(repository, times(1)).deleteAll();
    }

    @Test
    void checkCategoria() {
        when(categoriaRepository.findByTipoEqualsIgnoreCase(anyString())).thenReturn(Optional.of(categoria));

        Categoria categoriaChecked = funkoService.checkCategoria("DISNEY");

        assertAll("Comprobar categoría",
                () -> assertEquals("DISNEY", categoriaChecked.getTipo()),
                () -> assertFalse(categoriaChecked.isActive()));
    }

    @Test
    void categoriaNoExiste() {
        when(categoriaRepository.findByTipoEqualsIgnoreCase("INEXISTENTE")).thenReturn(Optional.empty());

        assertThrows(FunkoBadRequest.class, () -> funkoService.checkCategoria("INEXISTENTE"));
    }

    @Test
    void categoriaBorrada() {
        categoria.setActive(true);

        when(categoriaRepository.findByTipoEqualsIgnoreCase("BORRADA")).thenReturn(Optional.of(categoria));
        assertThrows(FunkoBadRequest.class, () -> funkoService.checkCategoria("BORRADA"));
    }
    @Test
    void onChange() throws IOException {
        doNothing().when(webSocketHandler).sendMessage(any(String.class));

        funkoService.onChange(Notificacion.Tipo.CREATE,any(Funko.class));
    }

    @Test
    void updateImage() throws IOException {
        String imageUrl = "https://wallhaven.cc/w/85852j";

        MultipartFile multipartFile = mock(MultipartFile.class);


        when(repository.findById(anyLong())).thenReturn(Optional.of(funko));
        when(storageService.store(multipartFile)).thenReturn(imageUrl);
        when(repository.save(any(Funko.class))).thenReturn(funko);
        doNothing().when(webSocketHandler).sendMessage(anyString());
        when(funkoMapper.toFunko(any(Funko.class))).thenReturn(funkoResponseDto);

        FunkoResponseDto funkoResponseDto1 = funkoService.updateImage(funko.getId(), multipartFile, false);

         assertEquals(funkoResponseDto1.getImage(), imageUrl);
         verify(repository, times(1)).save(any(Funko.class));
         verify(storageService,times((1))).delete(anyString());
         verify(storageService, times(1)).store(any(MultipartFile.class));
    }
    

}
