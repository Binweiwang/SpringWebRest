package com.example.springwebrest.funko.mapper;

import com.example.springwebrest.rest.categoria.models.Categoria;
import com.example.springwebrest.rest.funko.dto.FunkoCreateRequest;
import com.example.springwebrest.rest.funko.dto.FunkoResponseDto;
import com.example.springwebrest.rest.funko.dto.FunkoUpdateRequest;
import com.example.springwebrest.rest.funko.mapper.FunkoMapper;
import com.example.springwebrest.rest.funko.models.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapperTest {

    private FunkoMapper funkoMapper;
    private FunkoUpdateRequest funkoUpdateRequest = FunkoUpdateRequest.builder()
            .name("Test Funko")
            .price(10.0)
            .quantity(5)
            .image("test-image.jpg")
            .build();
    private FunkoUpdateRequest funkoUpdateRequestNull = FunkoUpdateRequest.builder()
            .build();
    private Funko funko = Funko.builder()
            .id(1L)
            .name("Test Funko")
            .price(10.0)
            .quantity(5)
            .image("test-image.jpg")
            .categoria(new Categoria())
            .build();
    private Categoria categoria = new Categoria(UUID.fromString("018b9c89-9096-7c96-9450-8cbbaa6fc8d9"), "DISNEY", LocalDateTime.now(), LocalDateTime.now(), false);
    @BeforeEach
    public void setup() {
        funkoMapper = new FunkoMapper();
    }

    @Test
    void toFunko(){
        FunkoResponseDto funkoResponseDto = funkoMapper.toFunko(funko);
        assertEquals(funkoResponseDto.getId(), funko.getId());
        assertEquals(funkoResponseDto.getName(), funko.getName());
        assertEquals(funkoResponseDto.getPrice(), funko.getPrice());
        assertEquals(funkoResponseDto.getQuantity(), funko.getQuantity());
        assertEquals(funkoResponseDto.getImage(), funko.getImage());
        assertEquals(funkoResponseDto.getCategoria(), funko.getCategoria());
    }

    @Test
    void toFunkoCreateRequest(){
        FunkoCreateRequest funkoCreateRequest = FunkoCreateRequest.builder()
                .name("Test Funko")
                .price(10.0)
                .quantity(5)
                .image("test-image.jpg")
                .build();
        Funko funko = funkoMapper.toFunko(funkoCreateRequest, categoria);
        assertEquals(funkoCreateRequest.getName(), funko.getName());
        assertEquals(funkoCreateRequest.getPrice(), funko.getPrice());
        assertEquals(funkoCreateRequest.getQuantity(), funko.getQuantity());
        assertEquals(funkoCreateRequest.getImage(), funko.getImage());
    }

    @Test
    void toFunkoUpdateRequest(){
        Funko funkoMapped = funkoMapper.toFunko(funkoUpdateRequest, funko, categoria);
        assertEquals(funko.getName(), funkoMapped.getName());
        assertEquals(funko.getPrice(), funkoMapped.getPrice());
        assertEquals(funko.getQuantity(), funkoMapped.getQuantity());
        assertEquals(funko.getImage(), funkoMapped.getImage());
    }
    @Test
    void toFunkoUpdateRequestNull(){
        Funko funkoMapped = funkoMapper.toFunko(funkoUpdateRequestNull, funko, categoria);

        assertEquals(funko.getName(), funkoMapped.getName());
        assertEquals(funko.getPrice(), funkoMapped.getPrice());
        assertEquals(funko.getQuantity(), funkoMapped.getQuantity());
        assertEquals(funko.getImage(), funkoMapped.getImage());

    }

   @Test
    void toFunkoList(){
        List<Funko> funkos = Arrays.asList(funko);
        List<FunkoResponseDto> funkoResponseDtos = funkoMapper.toResponses(funkos);
        assertEquals(funkoResponseDtos.get(0).getId(), funko.getId());
        assertEquals(funkoResponseDtos.get(0).getName(), funko.getName());
        assertEquals(funkoResponseDtos.get(0).getPrice(), funko.getPrice());
        assertEquals(funkoResponseDtos.get(0).getQuantity(), funko.getQuantity());
        assertEquals(funkoResponseDtos.get(0).getImage(), funko.getImage());
        assertEquals(funkoResponseDtos.get(0).getCategoria(), funko.getCategoria());
    }
}
