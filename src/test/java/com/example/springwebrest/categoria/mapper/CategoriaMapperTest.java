package com.example.springwebrest.categoria.mapper;

import com.example.springwebrest.rest.categoria.dto.CategoriaRequest;
import com.example.springwebrest.rest.categoria.mapper.CategoriasMapper;
import com.example.springwebrest.rest.categoria.models.Categoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoriaMapperTest {
    private final Categoria categoria = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "TEST", LocalDateTime.now(), LocalDateTime.now(), false);
    private final CategoriasMapper categoriaMapper = new CategoriasMapper();
    private final CategoriaRequest categoriaRequest = new CategoriaRequest("DISNEY", false);

    @Test
    void toCategoria() {
        Categoria mappedCategoria = categoriaMapper.toCategoria(categoriaRequest);
        assertEquals(categoriaRequest.getTipo(), mappedCategoria.getTipo());
        assertEquals(categoriaRequest.isActive(), mappedCategoria.isActive());
    }

    @Test
    void toCategoriaWithCategoria() {
        Categoria updatedCategoria = categoriaMapper.toCategoria(categoriaRequest, categoria);
        assertEquals(categoriaRequest.getTipo() , updatedCategoria.getTipo());
        assertEquals(categoriaRequest.isActive(), updatedCategoria.isActive());
    }
    @Test
    void toCategoriaTipoNull() {
        categoriaRequest.setTipo(null);
        Categoria updatedCategoria = categoriaMapper.toCategoria(categoriaRequest, categoria);

        assertEquals(categoria.getTipo(), updatedCategoria.getTipo());
    }
}
