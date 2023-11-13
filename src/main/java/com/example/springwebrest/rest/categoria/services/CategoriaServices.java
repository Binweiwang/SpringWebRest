package com.example.springwebrest.rest.categoria.services;

import com.example.springwebrest.rest.categoria.dto.CategoriaRequest;
import com.example.springwebrest.rest.categoria.models.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaServices {
    Page<Categoria> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable);
    Categoria findById(UUID id);
    Categoria findByNombre(String nombre);
    Categoria save(CategoriaRequest categoria);
    Categoria update(UUID id, CategoriaRequest categoriaRequest);
    void deleteById(UUID id);
}
