package com.example.springwebrest.rest.categoria.services;

import com.example.springwebrest.rest.categoria.dto.CategoriaRequest;
import com.example.springwebrest.rest.categoria.exceptions.CategoriaConflict;
import com.example.springwebrest.rest.categoria.exceptions.CategoriaNotFound;
import com.example.springwebrest.rest.categoria.mapper.CategoriasMapper;
import com.example.springwebrest.rest.categoria.models.Categoria;
import com.example.springwebrest.rest.categoria.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public class CategoriaServicesImp implements CategoriaServices {
    private final CategoriaRepository categoriaRepository;
    private final CategoriasMapper categoriasMapper;

    @Autowired
    public CategoriaServicesImp(CategoriaRepository categoriaRepository, CategoriasMapper categoriasMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriasMapper = categoriasMapper;
    }

    @Override
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    @Override
    public Categoria findById(UUID id) {
        return categoriaRepository.findById(id).orElseThrow(() -> new CategoriaNotFound(id));
    }

    @Override
    public Categoria findByNombre(String tipo) {
        return categoriaRepository.findByTipoEqualsIgnoreCase(tipo).orElseThrow(() -> new CategoriaNotFound(tipo));
    }

    @Override
    public Categoria save(CategoriaRequest categoria) {
        return categoriaRepository.save(categoriasMapper.toCategoria(categoria));
    }

    @Override
    public Categoria update(UUID id, CategoriaRequest categoriaRequest) {
        Categoria categoriaToUpdate = categoriaRepository.findById(id).orElseThrow(() -> new CategoriaNotFound(id));
        return categoriaRepository.save(categoriasMapper.toCategoria(categoriaRequest,categoriaToUpdate));
    }
    @Override
    public void deleteById(UUID id) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new CategoriaNotFound(id));
        if (categoriaRepository.existsProductoBy(id)){
            throw new CategoriaConflict("No se puede borrar la categoría con id " + id + " porque tiene productos asociados");
        }else {
            categoriaRepository.deleteById(id);
        }
    }
}
