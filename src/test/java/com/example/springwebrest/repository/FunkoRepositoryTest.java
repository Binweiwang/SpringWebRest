package com.example.springwebrest.repository;

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

    private FunkoRepositoryImp funkoRepositoryImp;

    @BeforeEach
    void setUp() {
        funkoRepositoryImp = new FunkoRepositoryImp();
    }


    @Test
    void findAll() {
        assertAll("Obtener todo los funkos",
                () -> assertEquals(3, funkoRepositoryImp.findAll().size())
        );
    }

    @Test
    void findById() {
        assertAll("Obtener funko por id",
                () -> assertEquals("Funko 1", funkoRepositoryImp.findById(1L).get().getName()),
                () -> assertEquals("Funko 2", funkoRepositoryImp.findById(2L).get().getName()),
                () -> assertEquals("Funko 3", funkoRepositoryImp.findById(3L).get().getName())
        );
    }
    @Test
    void save() {
        var funkoConId = Funko.builder()
                .id(4L)
                .name("Funko 4")
                .price(400.0)
                .image("Image 4")
                .category("Category 4")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        var funkoSinId= Funko.builder()
                .name("Funko 4")
                .price(400.0)
                .image("Image 4")
                .category("Category 4")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Funko save = funkoRepositoryImp.save(funkoConId);
        Funko save2 = funkoRepositoryImp.save(funkoSinId);

        assertAll("Guardar funko",
                () -> assertEquals(4, funkoRepositoryImp.findAll().size()),
                () -> assertEquals("Funko 4", save.getName()),
                () -> assertEquals(400, save.getPrice()),
                ()-> assertNotNull(save2.getId(),"El id no debe ser nulo"),
                ()-> assertEquals(funkoSinId,save2)

        );
    }

    @Test
    void update() {
        var funko = Funko.builder()
                .id(1L)
                .name("Funko updated")
                .price(100.0)
                .image("Image 1")
                .category("Category 1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Funko update = funkoRepositoryImp.update(funko);

        assertAll("Actualizar funko",
                () -> assertEquals(3, funkoRepositoryImp.findAll().size()),
                () -> assertEquals("Funko updated", update.getName()),
                () -> assertEquals(1, update.getId())
        );
    }

    @Test
    void deleteById() {
        Funko funko = funkoRepositoryImp.deleteById(1L);

        assertAll("Eliminar funko por id",
                () -> assertEquals(2, funkoRepositoryImp.findAll().size()),
                () -> assertEquals("Funko 1", funko.getName()),
                () -> assertEquals(1, funko.getId())
        );
    }

    @Test
    void deleteAll() {
        funkoRepositoryImp.deleteAll();

        assertEquals(0, funkoRepositoryImp.findAll().size());
    }
}
