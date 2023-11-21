package com.example.springwebrest.rest.categoria.services;

import com.example.springwebrest.rest.categoria.dto.CategoriaRequest;
import com.example.springwebrest.rest.categoria.exceptions.CategoriaConflict;
import com.example.springwebrest.rest.categoria.exceptions.CategoriaNotFound;
import com.example.springwebrest.rest.categoria.mapper.CategoriasMapper;
import com.example.springwebrest.rest.categoria.models.Categoria;
import com.example.springwebrest.rest.categoria.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
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
    public Page<Categoria> findAll(Optional<String> tipo, Optional<Boolean> isDeleted, Pageable pageable) {
        Specification<Categoria> specTipoCategoria = (root, query, criteriaBuilder) ->
                tipo.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por borrado
        Specification<Categoria> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(m -> criteriaBuilder.equal(root.get("isActive"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Categoria> criterio = Specification.where(specTipoCategoria)
                .and(specIsDeleted);
        return categoriaRepository.findAll(criterio, pageable);
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
