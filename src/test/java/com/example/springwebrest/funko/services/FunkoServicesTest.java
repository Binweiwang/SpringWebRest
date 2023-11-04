package com.example.springwebrest.funko.services;

import com.example.springwebrest.categoria.dto.CategoriaRequest;
import com.example.springwebrest.categoria.models.Categoria;
import com.example.springwebrest.categoria.repository.CategoriaRepository;
import com.example.springwebrest.funko.dto.FunkoCreateRequest;
import com.example.springwebrest.funko.dto.FunkoResponseDto;
import com.example.springwebrest.funko.dto.FunkoUpdateRequest;
import com.example.springwebrest.funko.exceptions.FunkoNotFound;
import com.example.springwebrest.funko.mapper.FunkoMapper;
import com.example.springwebrest.funko.models.Funko;
import com.example.springwebrest.funko.repository.FunkoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.DoNotMock;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FunkoServicesTest {
    private final Categoria categoria = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "DISNEY", LocalDateTime.now(), LocalDateTime.now(), false);
    private final CategoriaRequest categoriaRequest = new CategoriaRequest("DISNEY", false);
    private final Funko funko = new Funko().builder()
            .id(1L)
            .name("Funko 1")
            .price(100.0)
            .quantity(10)
            .image("https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg")
            .categoria(categoria)
            .build();
    private final List<Funko> ListaFunkos = List.of(
            new Funko(1L, "Funko 1", 100.0, 10, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", new Categoria(), LocalDateTime.now(), LocalDateTime.now()),
            new Funko(2L, "Funko 2", 200.0, 20, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", new Categoria(), LocalDateTime.now(), LocalDateTime.now()),
            new Funko(3L, "Funko 3", 300.0, 30, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", new Categoria(), LocalDateTime.now(), LocalDateTime.now())
    );
    private final FunkoUpdateRequest funkoUpdateRequest = FunkoUpdateRequest.builder()
            .name("Funko 1")
            .price(100.0)
            .quantity(10)
            .image("https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg")
            .categoria("DISNEY")
            .build();
    private final FunkoResponseDto funkoResponseDto = FunkoResponseDto.builder()
            .id(1L)
            .name("Funko 1")
            .price(100.0)
            .quantity(10)
            .image("https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg")
            .categoria(new Categoria())
            .build();
    private final FunkoCreateRequest funkoCreateRequest = FunkoCreateRequest.builder()
            .name("Funko 1")
            .price(100.0)
            .quantity(10)
            .image("https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg")
            .categoria("DISNEY")
            .build();
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private FunkoMapper funkoMapper = new FunkoMapper();
    @Mock
    private FunkoRepository repository;

    @InjectMocks
    private FunkoServicesImp funkoService;


    @Test
    void findAll() {
        when(repository.findAll()).thenReturn(ListaFunkos);

        List<Funko> funkos = funkoService.findAll();

        assertAll("Obtener todos los funkos",
                () -> assertEquals(3, funkos.size()),
                () -> assertEquals("Funko 1", funkos.get(0).getName()),
                () -> assertEquals("Funko 2", funkos.get(1).getName()),
                () -> assertEquals("Funko 3", funkos.get(2).getName())
        );
    }

    @Test
    void findById() {
        var funko = new Funko(1L, "Funko 1", 100.0, 10, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", new Categoria(), LocalDateTime.now(), LocalDateTime.now());
        var funkoId = 99L;

        when(repository.findById(1L)).thenReturn(Optional.of(funko));
        when(funkoMapper.toFunko(funko)).thenReturn(funkoResponseDto);
        when(repository.findById(funkoId)).thenThrow(FunkoNotFound.class);

        FunkoResponseDto funkoFindById = funkoService.findById(1L);

        assertAll("Obtener funko por id",
                () -> assertEquals("Funko 1", funkoFindById.getName()),
                () -> assertEquals(100.0, funkoFindById.getPrice()),
                () -> assertThrows(FunkoNotFound.class, () -> funkoService.findById(funkoId))
        );
    }

    @Test
    void save() {
        when(categoriaRepository.findByTipoEqualsIgnoreCase("DISNEY")).thenReturn(Optional.of(categoria));
        when(funkoMapper.toFunko(funkoCreateRequest, categoria)).thenReturn(funko);
        when(repository.save(any(Funko.class))).thenReturn(funko);
        when(funkoMapper.toFunko(any(Funko.class))).thenReturn(funkoResponseDto);

        // Act
        FunkoResponseDto result = funkoService.save(funkoCreateRequest);

        // Assert
        assertEquals(funkoResponseDto, result );
    }

    @Test
    void update() {
        when(categoriaRepository.findByTipoEqualsIgnoreCase("DISNEY")).thenReturn(Optional.of(categoria));
        when(funkoMapper.toFunko(funkoCreateRequest, categoria)).thenReturn(funko);
        when(repository.save(any(Funko.class))).thenReturn(funko);
        when(funkoMapper.toFunko(any(Funko.class))).thenReturn(funkoResponseDto);

        // Act
        FunkoResponseDto result = funkoService.save(funkoCreateRequest);

        // Assert
        assertEquals(funkoResponseDto, result );

    }
    @Test
    void deleteById() {
        var funko = new Funko(1L, "Funko 1", 100.0, 10, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", new Categoria(), LocalDateTime.now(), LocalDateTime.now());
        when(repository.findById(1L)).thenReturn(Optional.of(funko));

        funkoService.deleteById(1L);

        verify(repository, times(1)).deleteById(1L);

    }

    @Test
    void deleteAll() {
        funkoService.deleteAll();
        verify(repository, times(1)).deleteAll();
    }
}