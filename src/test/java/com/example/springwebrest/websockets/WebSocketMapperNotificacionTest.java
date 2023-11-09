package com.example.springwebrest.websockets;

import com.example.springwebrest.rest.categoria.models.Categoria;
import com.example.springwebrest.rest.funko.models.Funko;
import com.example.springwebrest.websockets.notifications.dto.FunkoNotificationResponse;
import com.example.springwebrest.websockets.notifications.mapper.FunkoNotificationMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
class WebSocketMapperNotificacionTest {

    Categoria categoria = Categoria.builder()
            .id(UUID.fromString("870bddce-4862-4d70-a8d9-dc4741da8295"))
            .tipo("DISNEY")
            .build();

    Funko funko = Funko.builder()
            .id(1L)
            .name("Funko Test")
            .price(10.0)
            .image("imagen Test")
            .categoria(categoria)
            .quantity(10)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();


    @Test
    void toFunkoNotificationDtoTest(){
        FunkoNotificationMapper funkoNotificationMapper = new FunkoNotificationMapper();
        FunkoNotificationResponse funkoNotificationResponse = funkoNotificationMapper.toFunkoNotificationDto(funko);

        assertAll(
                () -> assertEquals(funko.getId(), funkoNotificationResponse.id()),
                () -> assertEquals(funko.getName(), funkoNotificationResponse.name()),
                () -> assertEquals(funko.getPrice(), funkoNotificationResponse.price()),
                () -> assertEquals(funko.getImage(), funkoNotificationResponse.image()),
                () -> assertEquals(funko.getCategoria().getTipo(), funkoNotificationResponse.categoria().getTipo()),
                () -> assertEquals(funko.getQuantity(), funkoNotificationResponse.quantity()));
}}