package com.example.springwebrest.categoria.repository;

import com.example.springwebrest.categoria.dto.CategoriaRequest;
import com.example.springwebrest.categoria.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
    Optional<Categoria> findByTipoEqualsIgnoreCase(String tipo);
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Funko p WHERE p.categoria.id = :id")
    boolean existsProductoBy(UUID id);
}