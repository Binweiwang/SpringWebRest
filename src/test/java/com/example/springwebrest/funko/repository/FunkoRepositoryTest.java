package com.example.springwebrest.funko.repository;

import com.example.springwebrest.rest.categoria.models.Categoria;
import com.example.springwebrest.rest.funko.models.Funko;
import com.example.springwebrest.rest.funko.repository.FunkoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class FunkoRepositoryTest {
@Autowired
    private FunkoRepository funkoRepository;
    private Funko funkoConId = Funko.builder()
            .id(4L)
            .name("Funko 4")
            .price(400.0)
            .image("Image 4")
            .categoria(new Categoria())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    private Funko funkoSinId = Funko.builder()
            .name("Funko 4")
                .price(400.0)
                .image("Image 4")
                .categoria(new Categoria())
                .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    @Test
    void findAll() {
        assertAll("Obtener todo los funkos",
                () -> assertEquals(3, funkoRepository.findAll().size())
        );
    }

    @Test
    void findById() {
        assertAll("Obtener funko por id",
                () -> assertEquals("Funko 1", funkoRepository.findById(1L).get().getName()),
                () -> assertEquals("Funko 2", funkoRepository.findById(2L).get().getName()),
                () -> assertEquals("Funko 3", funkoRepository.findById(3L).get().getName())
        );
    }

    @Test
    void deleteById() {
        funkoRepository.deleteById(1L);

        assertAll("Eliminar funko por id",
                () -> assertEquals(2, funkoRepository.findAll().size())
        );
    }

    @Test
    void deleteAll() {
        funkoRepository.deleteAll();

        assertEquals(0, funkoRepository.findAll().size());
    }
}
