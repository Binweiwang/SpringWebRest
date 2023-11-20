package com.example.springwebrest.funko.controller;

import com.example.springwebrest.rest.categoria.models.Categoria;
import com.example.springwebrest.rest.funko.dto.FunkoCreateRequest;
import com.example.springwebrest.rest.funko.dto.FunkoResponseDto;
import com.example.springwebrest.rest.funko.dto.FunkoUpdateRequest;
import com.example.springwebrest.rest.funko.exceptions.FunkoNotFound;
import com.example.springwebrest.rest.funko.mapper.FunkoMapper;
import com.example.springwebrest.rest.funko.models.Funko;
import com.example.springwebrest.rest.funko.services.FunkoServices;
import com.example.springwebrest.utils.pagination.PageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
class FunkoControllerTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
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
            .categoria(new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "TEST", LocalDateTime.now(), LocalDateTime.now(), false))
            .build();
    private final FunkoCreateRequest funkoCreateRequest = FunkoCreateRequest.builder()
            .id(1L)
            .name("Funko2")
            .price(200.0)
            .quantity(20)
            .image("image2")
            .categoria("DISNEY")
            .build();


    private final FunkoResponseDto funkoResponseDtoError = FunkoResponseDto.builder()
            .name("Funko")
            .price(-100.0)
            .quantity(10)
            .image("image")
            .categoria(new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "TEST", LocalDateTime.now(), LocalDateTime.now(), false))
            .build();
    @Autowired
    MockMvc mockMvc;
    Funko funko1 = Funko.builder()
            .id(1L)
            .name("Funko")
            .price(100.0)
            .quantity(10)
            .image("image")
            .categoria(new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7338"), "DISNEY", LocalDateTime.now(), LocalDateTime.now(), false))
            .updatedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
    Funko funko2 = Funko.builder()
            .id(2L)
            .name("Funko2")
            .price(200.0)
            .quantity(20)
            .image("image2")
            .categoria(new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "TEST", LocalDateTime.now(), LocalDateTime.now(), false))
            .updatedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
    private final FunkoUpdateRequest funkoUpdateRequest = FunkoUpdateRequest.builder()
            .name("Funko")
            .price(100.0)
            .quantity(10)
            .image("image")
            .categoria("DISNEY")
            .build();
    private final FunkoUpdateRequest funkoUpdateRequestError = FunkoUpdateRequest.builder()
            .name("Funko")
            .price(-100.0)
            .quantity(10)
            .image("image")
            .categoria("DISNEY")
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
        List<FunkoResponseDto> funkos = List.of(funkoResponseDto);
        when(funkoService.findAll(any(Optional.class), any(Optional.class), any(Optional.class), any(Optional.class), any(PageRequest.class))).thenReturn(new PageImpl<>(funkos));

        MockHttpServletResponse response = mockMvc.perform(
                get(myEndpoint)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<FunkoResponseDto> res = mapper.readValue(response.getContentAsString(), mapper.getTypeFactory().constructParametricType(PageResponse.class, FunkoResponseDto.class));

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );
    }

    @Test
    void getFunko() throws Exception {
        when(funkoService.findById(1L))
                .thenReturn(funkoResponseDto);

        when(funkoMapper.toFunko(funko1))
                .thenReturn(funkoResponseDto);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        FunkoResponseDto res = mapper.readValue(response.getContentAsString(), mapper.registerModule(new JavaTimeModule())
                .getTypeFactory().constructType(FunkoResponseDto.class));
        assertAll("Obtener funko por id",
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertTrue(response.getContentAsString().contains("\"id\":" + funkoResponseDto.getId())),
                () -> assertEquals(res.getId(), funkoResponseDto.getId())
        );
    }

    @Test
    void getFunkoNotFound() throws Exception {
        when(funkoService.findById(999L))
                .thenThrow(new FunkoNotFound("El funko con id 999 no existe"));

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/999")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        assertAll("Obtener funko por id",
                () -> assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value())
        );
    }

    @Test
    void postFunko() throws Exception {
        when(funkoService.save(funkoCreateRequest)).thenReturn(funkoResponseDto);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jacksonRequestDto.write(funkoCreateRequest).getJson()))
                .andReturn().getResponse();
        FunkoResponseDto res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructType(FunkoResponseDto.class));

        assertAll("Guardar un funko",
                () -> assertEquals(response.getStatus(), HttpStatus.CREATED.value()),
                () -> assertTrue(response.getContentAsString().contains("\"id\":" + funkoResponseDto.getId())),
                () -> assertEquals(res.getId(), funkoResponseDto.getId()),
                () -> assertEquals(res.getName(), funkoResponseDto.getName())
        );
    }

    @Test
    void putFunko() throws Exception {
        when(funkoService.update(any(Long.class), any(FunkoUpdateRequest.class)))
                .thenReturn(funkoResponseDto);


        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoUpdateRequest)))
                .andReturn().getResponse();
        FunkoResponseDto res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructType(FunkoResponseDto.class));

        assertAll("Actualizar un funko",
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertTrue(response.getContentAsString().contains("\"id\":" + funkoResponseDto.getId())),
                () -> assertEquals(res.getId(), funkoResponseDto.getId()),
                () -> assertEquals(res.getName(), funkoResponseDto.getName()));

    }

    @Test
    void patchFunko() throws Exception {
        when(funkoService.update(any(Long.class), any(FunkoUpdateRequest.class)))
                .thenReturn(funkoResponseDto);

        MockHttpServletResponse response = mockMvc.perform(
                        patch(myEndpoint + "/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoUpdateRequest)))
                .andReturn().getResponse();

        FunkoResponseDto res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructType(FunkoResponseDto.class));


        assertAll("Actualizar un funko",
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertTrue(response.getContentAsString().contains("\"id\":" + funkoResponseDto.getId())),
                () -> assertEquals(res.getId(), funkoResponseDto.getId()),
                () -> assertEquals(res.getName(), funkoResponseDto.getName()));
    }

    @Test
    void deleteFunko() throws Exception {
        when(funkoService.findById(1L))
                .thenReturn(funkoResponseDto);

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myEndpoint + "/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        var id = funko1.getId();
        assertAll("Eliminar un funko",
                () -> assertEquals(response.getStatus(), HttpStatus.NO_CONTENT.value())
        );
    }

    @Test
    void updateProductWithBadRequest() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoUpdateRequestError)))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Precio no puede ser menor a 0"))
        );
    }

}



