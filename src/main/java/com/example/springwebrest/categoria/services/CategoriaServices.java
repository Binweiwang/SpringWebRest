package com.example.springwebrest.categoria.services;

import com.example.springwebrest.categoria.dto.CategoriaRequest;
import com.example.springwebrest.categoria.models.Categoria;

import java.util.List;
import java.util.UUID;

public interface CategoriaServices {
    List<Categoria> findAll();
    Categoria findById(UUID id);
    Categoria findByNombre(String nombre);
    Categoria save(CategoriaRequest categoria);
    Categoria update(UUID id, CategoriaRequest categoriaRequest);
    void deleteById(UUID id);
}
