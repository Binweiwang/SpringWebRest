package com.example.springwebrest.categoria.services;

import com.example.springwebrest.categoria.dto.CategoriaRequest;
import com.example.springwebrest.categoria.exceptions.CategoriaConflict;
import com.example.springwebrest.categoria.exceptions.CategoriaNotFound;
import com.example.springwebrest.categoria.mapper.CategoriasMapper;
import com.example.springwebrest.categoria.models.Categoria;
import com.example.springwebrest.categoria.repository.CategoriaRepository;
import com.example.springwebrest.funko.exceptions.FunkoNotFound;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoriaServicesTest {
    private final Categoria categoria = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "TEST", LocalDateTime.now(), LocalDateTime.now(), false);
    private final CategoriaRequest categoriaRequest = new CategoriaRequest("TEST", false);
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private CategoriasMapper categoriasMapper;

    @InjectMocks
    private CategoriaServicesImp categoriaServices;

    @Test
    void findAll(){
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<Categoria> categorias = categoriaServices.findAll();

        assertAll("findAll",
                () -> assertNotNull(categorias),
                () -> assertFalse(categorias.isEmpty())
        );
    }

    @Test
    void findById(){
        when(categoriaRepository.findById(categoria.getId())).thenReturn(java.util.Optional.of(categoria));

        Categoria categoriaFound = categoriaServices.findById(categoria.getId());

        assertAll("findById",
                () -> assertNotNull(categoriaFound),
                () -> assertEquals(categoriaFound.getId(), categoria.getId())
        );
    }
    @Test
    void findByIdNotFound(){
        when(categoriaRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(CategoriaNotFound.class, () -> categoriaServices.findById(categoria.getId()));
    }

    @Test
    void findByNombre(){
        when(categoriaRepository.findByTipoEqualsIgnoreCase(categoria.getTipo())).thenReturn(Optional.of(categoria));

        Categoria categoriaFound = categoriaServices.findByNombre(categoria.getTipo());

        assertAll("findByNombre",
                () -> assertNotNull(categoriaFound),
                () -> assertEquals(categoriaFound.getTipo(), categoria.getTipo())
        );
    }
    @Test
    void findByNombreNotFound(){
        when(categoriaRepository.findByTipoEqualsIgnoreCase(any(String.class))).thenReturn(Optional.empty());

        assertThrows(CategoriaNotFound.class, () -> categoriaServices.findByNombre(categoria.getTipo()));
    }
    @Test
    void save(){
        when(categoriasMapper.toCategoria(categoriaRequest)).thenReturn(categoria);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        Categoria categoriaSaved = categoriaServices.save(categoriaRequest);

        assertAll("save",
                () -> assertNotNull(categoriaSaved),
                () -> assertEquals(categoriaSaved.getTipo(), categoria.getTipo())
        );
    }


    @Test
    void update(){
        when(categoriaRepository.findById(any(UUID.class))).thenReturn(Optional.of(categoria));
        when(categoriasMapper.toCategoria(any(CategoriaRequest.class), any(Categoria.class))).thenReturn(categoria);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        Categoria categoriaUpdated = categoriaServices.update(categoria.getId(), categoriaRequest);

        assertAll("update",
                () -> assertNotNull(categoriaUpdated),
                () -> assertEquals(categoriaUpdated.getTipo(), categoria.getTipo())
        );
    }

    @Test
    void updateNotFound(){
        when(categoriaRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(CategoriaNotFound.class, () -> categoriaServices.update(categoria.getId(), categoriaRequest));
    }
    @Test
    void deleteById(){
        when(categoriaRepository.findById(categoria.getId())).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsProductoBy(categoria.getId())).thenReturn(false);

        categoriaServices.deleteById(categoria.getId());

        verify(categoriaRepository).deleteById(categoria.getId());
    }
    @Test
    void deleteByIdNotFound(){
        when(categoriaRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(CategoriaNotFound.class, () -> categoriaServices.deleteById(categoria.getId()));
    }
    @Test
    void deleteConflict(){
        when(categoriaRepository.findById(categoria.getId())).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsProductoBy(categoria.getId())).thenReturn(true);

       assertThrows(CategoriaConflict.class, () -> categoriaServices.deleteById(categoria.getId()));
    }
}

