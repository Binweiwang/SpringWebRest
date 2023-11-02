package com.example.springwebrest.services;

import com.example.springwebrest.funko.exceptions.FunkoNotFound;
import com.example.springwebrest.funko.models.Funko;
import com.example.springwebrest.funko.services.FunkoServicesImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FunkoServicesTest {
    @Mock
    private FunkoRepositoryImp repository;

    @InjectMocks
    private FunkoServicesImp funkoService;

    @Test
    void findAll() {
        var ListaFunkos = List.of(
                new Funko(1L, "Funko 1", 100.0, 10, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", "Anime", LocalDateTime.now(), LocalDateTime.now()),
                new Funko(2L, "Funko 2", 200.0, 20, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", "Anime", LocalDateTime.now(), LocalDateTime.now()),
                new Funko(3L, "Funko 3", 300.0, 30, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", "Anime", LocalDateTime.now(), LocalDateTime.now())
        );

        when(repository.findAll()).thenReturn(ListaFunkos);

        List<Funko> funkos = funkoService.findAll();

        assertAll("Obtener todos los funkos",
                () -> assertEquals(3,funkos.size()),
                () -> assertEquals("Funko 1", funkos.get(0).getName()),
                () -> assertEquals("Funko 2", funkos.get(1).getName()),
                () -> assertEquals("Funko 3", funkos.get(2).getName())
        );
    }

    @Test
    void findById() {
        var funko = new Funko(1L, "Funko 1", 100.0, 10, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", "Anime", LocalDateTime.now(), LocalDateTime.now());
        var funkoId = 99L;

        when(repository.findById(funkoId)).thenReturn(Optional.empty());
        when(repository.findById(1L)).thenReturn(Optional.of(funko));

        Funko funkoFound2 = funkoService.findById(1L);

        assertAll("Obtener funko por id",
                () -> assertEquals("Funko 1", funkoFound2.getName()),
                () -> assertEquals(100.0, funkoFound2.getPrice()),
                () -> assertThrows(FunkoNotFound.class, () -> funkoService.findById(funkoId))
            );
    }

    @Test
    void save() {
        var funko = new Funko(1L, "Funko 1", 100.0, 10, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", "Anime", LocalDateTime.now(), LocalDateTime.now());

        when(repository.save(funko)).thenReturn(funko);

        Funko funkoSaved = funkoService.save(funko);

        assertAll("Guardar funko",
                () -> assertEquals("Funko 1", funkoSaved.getName()),
                () -> assertEquals(100.0, funkoSaved.getPrice())
        );
    }
    @Test
    void update() {
        var funko = new Funko(1L, "Funko 1", 100.0, 10, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", "Anime", LocalDateTime.now(), LocalDateTime.now());

        when(repository.findById(1L)).thenReturn(Optional.of(funko));
        when(repository.update(funko)).thenReturn(funko);

        Funko funkoUpdated = funkoService.update(funko);

        assertAll("Actualizar funko",
                () -> assertEquals("Funko 1", funkoUpdated.getName()),
                () -> assertEquals(100.0, funkoUpdated.getPrice())
        );
    }
    @Test
    void deleteById() {
        var funko = new Funko(1L, "Funko 1", 100.0, 10, "https://images-na.ssl-images-amazon.com/images/I/61%2B%2Bq%2B0%2B%2BZL._AC_SL1500_.jpg", "Anime", LocalDateTime.now(), LocalDateTime.now());
        when(repository.findById(1L)).thenReturn(Optional.of(funko));
        when(repository.deleteById(1L)).thenReturn(funko);

        Funko funkoDeleted = funkoService.deleteById(1L);

        assertAll("Eliminar funko por id",
                () -> assertEquals("Funko 1", funkoDeleted.getName()),
                () -> assertEquals(100.0, funkoDeleted.getPrice())
        );
    }

    @Test
    void deleteAll() {
        funkoService.deleteAll();
        verify(repository, times(1)).deleteAll();
    }
}
