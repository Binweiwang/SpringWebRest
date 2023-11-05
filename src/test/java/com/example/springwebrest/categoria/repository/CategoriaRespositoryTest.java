package com.example.springwebrest.categoria.repository;

import com.example.springwebrest.categoria.models.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CategoriaRespositoryTest {
    private final Categoria categoria = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "TEST", LocalDateTime.now(), LocalDateTime.now(), true);
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.merge(categoria);
        entityManager.flush();
    }
    @Test
    void findAll(){
        // Act
        var categorias = categoriaRepository.findAll();
        // Assert
        assertAll("findAll",
                () -> assertNotNull(categorias),
                () -> assertFalse(categorias.isEmpty()),
                () -> assertFalse(categorias.isEmpty())
        );
    }

    @Test
    void findByNombre() {
        // Act
        var categorias = categoriaRepository.findByTipoEqualsIgnoreCase("TEST");
        // Assert
        assertAll("findAllByNombre",
                () -> assertNotNull(categorias),
                () -> assertFalse(categorias.isEmpty()),
                () -> assertEquals("TEST", categorias.get().getTipo())
        );
    }

    @Test
    void findById(){
        // Act
        var categoriaFound = categoriaRepository.findById(categoria.getId());
        // Assert
        assertAll("findAllByNombre",
                () -> assertNotNull(categoriaFound),
                () -> assertTrue(categoriaFound.isPresent()),
                () -> assertEquals("TEST", categoriaFound.get().getTipo())
        );
    }

    @Test
    void findByIdNotFound(){
        // Act
        var categorias = categoriaRepository.findById(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7338"));
        // Assert
        assertAll("findAllByNombre",
                () -> assertNotNull(categorias),
                () -> assertFalse(categorias.isPresent())
        );
    }

    @Test
    void save(){
        // Act
        var categoria = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7338"), "TEST2", LocalDateTime.now(), LocalDateTime.now(), true);
        var categoriaRes = categoriaRepository.save(categoria);
        // Assert
        assertAll("save",
                () -> assertNotNull(categoriaRes),
                () -> assertEquals("TEST2", categoriaRes.getTipo())
        );
    }

    @Test
    void update(){
        // Act
        var categoria = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "TEST2", LocalDateTime.now(), LocalDateTime.now(), true);
        var categoriaRes = categoriaRepository.save(categoria);
        // Assert
        assertAll("save",
                () -> assertNotNull(categoriaRes),
                () -> assertEquals("TEST2", categoriaRes.getTipo())
        );
    }


    @Test
    void delete(){
        // Act
        var categoriaFound = categoriaRepository.findById(categoria.getId()).orElse(null);
        categoriaRepository.delete(categoriaFound);
        var categoriaRes = categoriaRepository.findById(categoria.getId()).orElse(null);
        // Assert
        assertNull(categoriaRes);
    }

}
