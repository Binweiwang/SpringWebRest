package com.example.springwebrest.websockets.notifications.dto;

import com.example.springwebrest.rest.categoria.models.Categoria;

import java.time.LocalDateTime;

public record FunkoNotificationResponse(
         Long id,
         String name,
         Double price,
         Integer quantity,
         String image,
         Categoria categoria,
         LocalDateTime createdAt,
         LocalDateTime updatedAt
) {
}