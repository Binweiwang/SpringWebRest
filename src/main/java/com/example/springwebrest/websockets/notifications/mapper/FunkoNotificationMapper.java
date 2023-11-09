package com.example.springwebrest.websockets.notifications.mapper;

import com.example.springwebrest.rest.funko.models.Funko;
import com.example.springwebrest.websockets.notifications.dto.FunkoNotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class FunkoNotificationMapper {
    public FunkoNotificationResponse toFunkoNotificationDto(Funko funko) {
        return new FunkoNotificationResponse(
                funko.getId(),
                funko.getName(),
                funko.getPrice(),
                funko.getQuantity(),
                funko.getImage(),
                funko.getCategoria(),
                funko.getCreatedAt(),
                funko.getUpdatedAt());
    }
}
