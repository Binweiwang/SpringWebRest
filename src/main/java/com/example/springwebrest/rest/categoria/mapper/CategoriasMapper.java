package com.example.springwebrest.rest.categoria.mapper;

import com.example.springwebrest.rest.categoria.dto.CategoriaRequest;
import com.example.springwebrest.rest.categoria.models.Categoria;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CategoriasMapper {
    public Categoria toCategoria(CategoriaRequest request) {
        return Categoria.builder()
                .tipo(request.getTipo())
                .isActive(request.isActive())
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Categoria toCategoria(CategoriaRequest request, Categoria categoria) {
        return new Categoria(
                categoria.getId(),
                request.getTipo() != null ? request.getTipo() : categoria.getTipo(),
                categoria.getCreatedAt(),
                LocalDateTime.now(),
                request.isActive()
        );
    }
}
