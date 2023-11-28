package com.example.springwebrest.pedidos.services;

import com.example.springwebrest.rest.funko.models.Funko;
import com.example.springwebrest.rest.funko.repository.FunkoRepository;
import com.example.springwebrest.rest.pedidos.exceptions.PedidoNotFound;
import com.example.springwebrest.rest.pedidos.exceptions.PedidoNotItems;
import com.example.springwebrest.rest.pedidos.exceptions.ProductoNotFound;
import com.example.springwebrest.rest.pedidos.models.LineaPedido;
import com.example.springwebrest.rest.pedidos.models.Pedido;
import com.example.springwebrest.rest.pedidos.repositories.PedidosRepository;
import com.example.springwebrest.rest.pedidos.services.PedidosServiceImpl;
import com.example.springwebrest.rest.users.dto.UserInfoResponse;
import com.example.springwebrest.rest.users.services.UsersService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidosServiceImplTest {
    @Mock
    private PedidosRepository pedidosRepository;
    @Mock
    private FunkoRepository funkoRepository;
    @Mock
    private UsersService usersService;

    @InjectMocks
    private PedidosServiceImpl pedidosService;

    private Pedido pedido = Pedido.builder()
            .id(new ObjectId())
            .idUsuario(1L)
            .total(20.0)
            .totalItems(2)
            .idUsuario(1L)
            .lineasPedido(List.of(LineaPedido.builder()
                    .idProducto(1L)
                    .cantidad(2)
                    .precioProducto(10.0)
                    .build()))
            .build();
    @Test
    void findAll() {
        // Arrange
        List<Pedido> pedidos = List.of(new Pedido(), new Pedido());
        Page<Pedido> expectedPage = new PageImpl<>(pedidos);
        Pageable pageable = PageRequest.of(0, 10);

        when(pedidosRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<Pedido> result = pedidosService.findAll(pageable);

        // Assert
        assertAll(
                () -> assertEquals(expectedPage, result),
                () -> assertEquals(expectedPage.getContent(), result.getContent()),
                () -> assertEquals(expectedPage.getTotalElements(), result.getTotalElements())
        );
    }

    @Test
    void findById() {
        // Arrange
        ObjectId idPedido = new ObjectId();
        Pedido expectedPedido = new Pedido();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.of(expectedPedido));

        // Act
        Pedido resultPedido = pedidosService.findById(idPedido);

        // Assert
        assertEquals(expectedPedido, resultPedido);

    }

    @Test
    void findByIdNotFound() {
        // Arrange
        ObjectId idPedido = new ObjectId();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PedidoNotFound.class, () -> pedidosService.findById(idPedido));

        // Verify
        verify(pedidosRepository).findById(idPedido);
    }

    @Test
    void findByIdUsuario() {
        // Arrange
        Long idUsuario = 1L;
        Pageable pageable = mock(Pageable.class);
        @SuppressWarnings("unchecked")
        Page<Pedido> expectedPage = mock(Page.class);
        when(pedidosRepository.findByIdUsuario(idUsuario, pageable)).thenReturn(expectedPage);

        // Act
        Page<Pedido> resultPage = pedidosService.findByIdUsuario(idUsuario, pageable);

        // Assert
        assertEquals(expectedPage, resultPage);

        // Verify
        verify(pedidosRepository).findByIdUsuario(idUsuario, pageable);
    }

    @Test
    void save() {
        // Arrange
        Funko funko = Funko.builder()
                .id(1L)
                .quantity(5)
                .price(10.0)
                .build();

        LineaPedido lineaPedido = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(10.0)
                .build();
        pedido.setLineasPedido(List.of(lineaPedido));
        Pedido pedidoToSave = Pedido.builder()
                .id(new ObjectId())
                .idUsuario(1L)
                .total(20.0)
                .totalItems(2)
                .idUsuario(1L)
                .lineasPedido(List.of(lineaPedido))
                .build();
        pedidoToSave.setLineasPedido(List.of(lineaPedido));

        when(usersService.findById(anyLong())).thenReturn(UserInfoResponse.builder().id(1L).build());
        when(pedidosRepository.save(any(Pedido.class))).thenReturn(pedidoToSave); // Utiliza any(Pedido.class) para cualquier instancia de Pedido
        when(funkoRepository.findById(anyLong())).thenReturn(Optional.of(funko));

        // Act
        Pedido resultPedido = pedidosService.save(pedido);

        // Assert
        assertAll(
                () -> assertEquals(pedidoToSave, resultPedido),
                () -> assertEquals(pedidoToSave.getLineasPedido(), resultPedido.getLineasPedido()),
                () -> assertEquals(pedidoToSave.getLineasPedido().size(), resultPedido.getLineasPedido().size())
        );
    }


    @Test
    void saveNotItems() {
        // Arrang
        when(usersService.findById(anyLong())).thenReturn(UserInfoResponse.builder().id(1L).build());

        // Act & Assert
        assertThrows(ProductoNotFound.class, () -> pedidosService.save(pedido));
    }

    @Test
    void delete() {
        // Arrange
        ObjectId idPedido = new ObjectId();
        Pedido pedidoToDelete = new Pedido();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.of(pedidoToDelete));

        // Act
        pedidosService.delete(idPedido);
    }

    @Test
    void deleteNotFound() {
        // Arrange
        ObjectId idPedido = new ObjectId();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PedidoNotFound.class, () -> pedidosService.delete(idPedido));

    }

    @Test
    void update() {
        // Arrange
        Funko funko = Funko.builder()
                .id(1L)
                .quantity(5)
                .price(10.0)
                .build();


        LineaPedido lineaPedido = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(10.0)
                .build();

        ObjectId idPedido = new ObjectId();
        pedido.setLineasPedido(List.of(lineaPedido));
        Pedido pedidoToUpdate = new Pedido();
        pedidoToUpdate.setLineasPedido(List.of(lineaPedido)); // Inicializar la lista de líneas de pedido

        when(usersService.findById(anyLong())).thenReturn(UserInfoResponse.builder().id(1L).build());
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.of(pedidoToUpdate));
        when(pedidosRepository.save(any(Pedido.class))).thenReturn(pedidoToUpdate);
        when(funkoRepository.findById(anyLong())).thenReturn(Optional.of(funko));

        // Act
        Pedido resultPedido = pedidosService.update(idPedido, pedido);

        // Assert
        assertAll(
                () -> assertEquals(pedidoToUpdate, resultPedido),
                () -> assertEquals(pedidoToUpdate.getLineasPedido(), resultPedido.getLineasPedido()),
                () -> assertEquals(pedidoToUpdate.getLineasPedido().size(), resultPedido.getLineasPedido().size())
        );
    }

    @Test
    void updatePedidoNotFound() {
        // Arrange
        ObjectId idPedido = new ObjectId();
        Pedido pedido = new Pedido();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PedidoNotFound.class, () -> pedidosService.update(idPedido, pedido));

    }
}