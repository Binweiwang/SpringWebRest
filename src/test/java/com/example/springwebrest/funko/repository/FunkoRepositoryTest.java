package com.example.springwebrest.funko.repository;

import com.example.springwebrest.categoria.models.Categoria;
import com.example.springwebrest.funko.models.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FunkoRepositoryTest {

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
    void save() {
        Funko save = funkoRepository.save(funkoConId);
        Funko save2 = funkoRepository.save(funkoSinId);

        assertAll("Guardar funko",
                () -> assertEquals(4, funkoRepository.findAll().size()),
                () -> assertEquals("Funko 4", save.getName()),
                () -> assertEquals(400, save.getPrice()),
                ()-> assertNotNull(save2.getId(),"El id no debe ser nulo"),
                ()-> assertEquals(funkoSinId,save2)

        );
    }

    @Test
    void update() {


        Funko update = funkoRepository.save(funkoConId);

        assertAll("Actualizar funko",
                () -> assertEquals(3, funkoRepository.findAll().size()),
                () -> assertEquals("Funko updated", update.getName()),
                () -> assertEquals(1, update.getId())
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
