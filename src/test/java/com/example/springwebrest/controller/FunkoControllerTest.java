package com.example.springwebrest.controller;

import com.example.springwebrest.funko.dto.FunkoCreateRequest;
import com.example.springwebrest.funko.dto.FunkoResponseDto;
import com.example.springwebrest.funko.exceptions.FunkoNotFound;
import com.example.springwebrest.funko.mapper.FunkoMapper;
import com.example.springwebrest.funko.models.Funko;
import com.example.springwebrest.funko.services.FunkoServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
class FunkoControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private final FunkoServices funkoService;
    @MockBean
    private final FunkoMapper funkoMapper;
    private final String myEndpoint = "/funkos";
    private final FunkoResponseDto funkoResponseDto = FunkoResponseDto.builder()
            .id(1L)
            .name("Funko")
            .price(100.0)
            .quantity(10)
            .image("image")
            .category("category")
            .build();
    private final FunkoCreateRequest funkoRequestDto = FunkoCreateRequest.builder()
            .id(2L)
            .name("Funko2")
            .price(200.0)
            .quantity(20)
            .image("image2")
            .category("category2")
            .build();
    @Autowired
    MockMvc mockMvc;
    Funko funko1 = Funko.builder()
            .id(1L)
            .name("Funko")
            .price(100.0)
            .quantity(10)
            .image("image")
            .category("category")
            .updatedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
    Funko funko2 = Funko.builder()
            .id(2L)
            .name("Funko2")
            .price(200.0)
            .quantity(20)
            .image("image2")
            .category("category2")
            .updatedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
    @Autowired
    private JacksonTester<FunkoCreateRequest> jacksonRequestDto;
    @Autowired
    private JacksonTester<FunkoResponseDto> jacksonResponseDto;

    @Autowired
    FunkoControllerTest(FunkoServices funkoService, FunkoMapper funkoMapper) {
        this.funkoService = funkoService;
        this.funkoMapper = funkoMapper;
    }


    @Test
    void getFunkos() throws Exception {
        when(funkoService.findAll())
                .thenReturn(List.of(funko1, funko2));

        when(funkoMapper.toResponses(List.of(funko1, funko2)))
                .thenReturn(List.of(funkoResponseDto, funkoResponseDto));

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        List<FunkoResponseDto> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, FunkoResponseDto.class));

        assertAll("Obtener todos los funkos",
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertTrue(response.getContentAsString().contains("\"id\":" + funko1.getId())),
                () -> assertTrue(res.size() > 0),
                () -> assertTrue(res.stream().anyMatch(r -> r.getId().equals(funko1.getId())))
        );
    }

    @Test
    void getFunko() throws Exception {
        when(funkoService.findById(1L))
                .thenReturn(funko1);

        when(funkoMapper.toFunko(funko1))
                .thenReturn(funkoResponseDto);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        FunkoResponseDto res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructType(FunkoResponseDto.class));
        assertAll("Obtener funko por id",
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertTrue(response.getContentAsString().contains("\"id\":" + funko1.getId())),
                () -> assertEquals(res.getId(), funko1.getId())
        );
    }

    @Test
    void getFunkoNotFound() throws Exception {
        when(funkoService.findById(1L))
                .thenThrow(new FunkoNotFound("No se encontró funko con id: " + 1L + " en la base de datos"));

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll("Obtener funko por id",
                () -> assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value())
        );
    }

    @Test
    void postFunko() throws Exception {
        when(funkoMapper.toFunko(funkoRequestDto))
                .thenReturn(funko1);
        when(funkoService.save(funko1))
                .thenReturn(funko1);
        when(funkoMapper.toFunko(funko1))
                .thenReturn(funkoResponseDto);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jacksonRequestDto.write(funkoRequestDto).getJson()))
                .andReturn().getResponse();
        FunkoResponseDto res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructType(FunkoResponseDto.class));

        assertAll("Guardar un funko",
                () -> assertEquals(response.getStatus(), HttpStatus.CREATED.value()),
                () -> assertTrue(response.getContentAsString().contains("\"id\":" + funko1.getId())),
                () -> assertEquals(res.getId(), funko1.getId()),
                () -> assertEquals(res.getName(), funko1.getName())
        );
    }

    @Test
    void putFunko() throws Exception {
        when(funkoMapper.toFunko(funkoRequestDto))
                .thenReturn(funko1);
        when(funkoService.save(any()))
                .thenReturn(funko1);
        when(funkoMapper.toFunko(funko1))
                .thenReturn(funkoResponseDto);

        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoResponseDto)))
                .andReturn().getResponse();
        FunkoResponseDto res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructType(FunkoResponseDto.class));

        assertAll("Actualizar un funko",
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertTrue(response.getContentAsString().contains("\"id\":" + funko1.getId())),
                () -> assertEquals(res.getId(), funko1.getId()),
                () -> assertEquals(res.getName(), funko1.getName()));

    }

    @Test
    void patchFunko() throws Exception {
        when(funkoMapper.toFunko(funkoRequestDto))
                .thenReturn(funko1);
        when(funkoService.update(any()))
                .thenReturn(funko1);
        when(funkoMapper.toFunko(funko1))
                .thenReturn(funkoResponseDto);

        MockHttpServletResponse response = mockMvc.perform(
                        patch(myEndpoint + "/2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoResponseDto)))
                .andReturn().getResponse();
        FunkoResponseDto res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructType(FunkoResponseDto.class));

        assertAll("Actualizar un funko",
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertTrue(response.getContentAsString().contains("\"id\":" + funko1.getId())),
                () -> assertEquals(res.getId(), funko1.getId()),
                () -> assertEquals(res.getName(), funko1.getName()));
    }

    @Test
    void deleteFunko() throws Exception {
        when(funkoService.findById(1L))
                .thenReturn(funko1);
        when(funkoService.deleteById(1L))
                .thenReturn(funko1);

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myEndpoint + "/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        var id = funko1.getId();
        assertAll("Eliminar un funko",
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertTrue(response.getContentAsString().contains("Funko con id: " + id + " eliminado correctamente"))
        );
    }

    @Test
    void updateProductWithBadRequest() throws Exception {
        var funkoDto = FunkoResponseDto.builder()
                .id(1L)
                .name("Funko")
                .price(-100.0)
                .quantity(-10)
                .image("image")
                .category("category")
                .build();
        when(funkoMapper.toFunko(any()))
                .thenReturn(funko1);
        when(funkoService.save(funko1))
                .thenReturn(funko1);
        when(funkoMapper.toFunko(funko1))
                .thenReturn(funkoDto);
        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jacksonResponseDto.write(funkoDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        System.out.println(response.getContentAsString());

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Precio no puede ser menor a 0"))
        );
    }
    }



