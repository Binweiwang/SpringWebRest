package com.example.springwebrest.mapper;

import com.example.springwebrest.funko.dto.FunkoCreateRequest;
import com.example.springwebrest.funko.dto.FunkoResponseDto;
import com.example.springwebrest.funko.mapper.FunkoMapper;
import com.example.springwebrest.funko.models.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapperTest {

    private FunkoMapper funkoMapper;

    @BeforeEach
    public void setup() {
        funkoMapper = new FunkoMapper();
    }

    @Test
     void GetFunko() {
        var funko = Funko.builder()
                .id(1L)
                .name("Test Funko")
                .price(10.0)
                .quantity(5)
                .image("test-image.jpg")
                .category("Test Category")
                .build();

        FunkoResponseDto funkoResponseDto = funkoMapper.toFunko(funko);

        assertEquals(funko.getId(), funkoResponseDto.getId());
        assertEquals(funko.getName(), funkoResponseDto.getName());
        assertEquals(funko.getPrice(), funkoResponseDto.getPrice());
        assertEquals(funko.getQuantity(), funkoResponseDto.getQuantity());
        assertEquals(funko.getImage(), funkoResponseDto.getImage());
        assertEquals(funko.getCategory(), funkoResponseDto.getCategory());
    }

    @Test
     void GetFunkos() {
        var funko1 = Funko.builder()
                .id(1L)
                .name("Funko 1")
                .build();

        var funko2 = Funko.builder()
                .id(2L)
                .name("Funko 2")
                .build();

        List<Funko> funkos = Arrays.asList(funko1, funko2);

        List<FunkoResponseDto> responseDtos = funkoMapper.toResponses(funkos);

        assertEquals(funkos.size(), responseDtos.size());
        assertEquals(funkos.get(0).getId(), responseDtos.get(0).getId());
        assertEquals(funkos.get(1).getName(), responseDtos.get(1).getName());

    }
    @Test
   void modelToFunko(){
        var funko = FunkoCreateRequest.builder()
                .id(1L)
                .name("Test Funko")
                .price(10.0)
                .quantity(5)
                .image("test-image.jpg")
                .category("Test Category")
                .build();

        Funko funkoResponseDto = funkoMapper.toFunko(funko);

        assertEquals(funko.getId(), funkoResponseDto.getId());
        assertEquals(funko.getName(), funkoResponseDto.getName());
        assertEquals(funko.getPrice(), funkoResponseDto.getPrice());
        assertEquals(funko.getQuantity(), funkoResponseDto.getQuantity());
        assertEquals(funko.getImage(), funkoResponseDto.getImage());
        assertEquals(funko.getCategory(), funkoResponseDto.getCategory());
    }
}
