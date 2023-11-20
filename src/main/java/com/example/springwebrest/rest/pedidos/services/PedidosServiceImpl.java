package com.example.springwebrest.rest.pedidos.services;

import com.example.springwebrest.rest.funko.repository.FunkoRepository;
import com.example.springwebrest.rest.pedidos.exceptions.*;
import com.example.springwebrest.rest.pedidos.models.LineaPedido;
import com.example.springwebrest.rest.pedidos.models.Pedido;
import com.example.springwebrest.rest.pedidos.repositories.PedidosRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@CacheConfig(cacheNames = {"pedidos"})
public class PedidosServiceImpl implements PedidosService {
    private final PedidosRepository pedidosRepository;
    private final FunkoRepository funkoRepository;

    public PedidosServiceImpl(PedidosRepository pedidosRepository, FunkoRepository funkoRepository) {
        this.pedidosRepository = pedidosRepository;
        this.funkoRepository = funkoRepository;
    }

    @Override
    public Page<Pedido> findAll(Pageable pageable) {
        // Podemos paginar y hacer otras cosas
        log.info("Obteniendo todos los pedidos paginados y ordenados con {}", pageable);
        return pedidosRepository.findAll(pageable);
    }


    @Override
    @Cacheable(key = "#idPedido")
    public Pedido findById(ObjectId idPedido) {
        log.info("Obteniendo pedido con id: " + idPedido);
        return pedidosRepository.findById(idPedido).orElseThrow(() -> new PedidoNotFound(idPedido.toHexString()));
    }

    @Override
    public Page<Pedido> findByIdUsuario(Long idUsuario, Pageable pageable) {
        log.info("Obteniendo pedidos del usuario con id: " + idUsuario);
        return pedidosRepository.findByIdUsuario(idUsuario, pageable);
    }

    @Override
    @Transactional
    @CachePut(key = "#result.id")
    public Pedido save(Pedido pedido) {
        log.info("Guardando pedido: {}", pedido);

        checkPedido(pedido);

        var pedidoToSave = reserveStockPedidos(pedido);

        pedidoToSave.setCreatedAt(LocalDateTime.now());
        pedidoToSave.setUpdatedAt(LocalDateTime.now());


        return pedidosRepository.save(pedidoToSave);
    }

    Pedido reserveStockPedidos(Pedido pedido) {
        log.info("Reservando stock del pedido: {}", pedido);

        if (pedido.getLineasPedido() == null || pedido.getLineasPedido().isEmpty()) {
            throw new PedidoNotItems(pedido.getId().toHexString());
        }

        pedido.getLineasPedido().forEach(lineaPedido -> {
            var funko = funkoRepository.findById(lineaPedido.getIdProducto()).get(); // Siempre existe porque ha pasado el check
            funko.setQuantity(funko.getQuantity() - lineaPedido.getCantidad());
            funkoRepository.save(funko);
            lineaPedido.setTotal(lineaPedido.getCantidad() * lineaPedido.getPrecioProducto());
        });

        var total = pedido.getLineasPedido().stream()
                .map(lineaPedido -> lineaPedido.getCantidad() * lineaPedido.getPrecioProducto())
                .reduce(0.0, Double::sum);

        var totalItems = pedido.getLineasPedido().stream()
                .map(LineaPedido::getCantidad)
                .reduce(0, Integer::sum);

        pedido.setTotal(total);
        pedido.setTotalItems(totalItems);

        return pedido;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#idPedido")
    public void delete(ObjectId idPedido) {
        log.info("Borrando pedido: " + idPedido);

        var pedidoToDelete = this.findById(idPedido);

        returnStockPedidos(pedidoToDelete);

        pedidosRepository.deleteById(idPedido);
    }

    Pedido returnStockPedidos(Pedido pedido) {
        log.info("Retornando stock del pedido: {}", pedido);
        if (pedido.getLineasPedido() != null) {
            pedido.getLineasPedido().forEach(lineaPedido -> {
                var producto = funkoRepository.findById(lineaPedido.getIdProducto()).get(); // Siempre existe porque ha pasado el check
                producto.setQuantity(producto.getQuantity() + lineaPedido.getCantidad());
                funkoRepository.save(producto);
            });
        }
        return pedido;
    }


    @Override
    @Transactional
    @CachePut(key = "#idPedido")
    public Pedido update(ObjectId idPedido, Pedido pedido) {
        log.info("Actualizando pedido con id: " + idPedido);

        var pedidoToUpdate = this.findById(idPedido);

        returnStockPedidos(pedido);

        checkPedido(pedido);

        var pedidoToSave = reserveStockPedidos(pedido);

        pedidoToSave.setUpdatedAt(LocalDateTime.now());

        return pedidosRepository.save(pedidoToSave);

    }

    void checkPedido(Pedido pedido) {
        log.info("Comprobando pedido: {}", pedido);

        if (pedido.getLineasPedido() == null || pedido.getLineasPedido().isEmpty()) {
            throw new PedidoNotItems(pedido.getId().toHexString());
        }
        pedido.getLineasPedido().forEach(lineaPedido -> {
            var producto = funkoRepository.findById(lineaPedido.getIdProducto())
                    .orElseThrow(() -> new ProductoNotFound(lineaPedido.getIdProducto()));
            if (producto.getQuantity() < lineaPedido.getCantidad()) {
                throw new ProductoNotStock(lineaPedido.getIdProducto());
            }
            if (!producto.getPrice().equals(lineaPedido.getPrecioProducto())) {
                throw new ProductoBadPrice(lineaPedido.getIdProducto());
            }
        });
    }
}
