package com.example.springwebrest.categoria.controllers;

import com.example.springwebrest.rest.categoria.dto.CategoriaRequest;
import com.example.springwebrest.rest.categoria.exceptions.CategoriaConflict;
import com.example.springwebrest.rest.categoria.exceptions.CategoriaNotFound;
import com.example.springwebrest.rest.categoria.models.Categoria;
import com.example.springwebrest.rest.categoria.services.CategoriaServicesImp;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.ErrorResponse;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@SpringBootTest(properties = "spring.config.name=application-test")
public class CategoriaConstollersTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    CategoriaServicesImp service;

    @Autowired
    MockMvc mockMvc;

    Categoria categoria1 = Categoria.builder()
            .id(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"))
            .tipo("categoria 1")
            .build();

    Categoria categoria2 = Categoria.builder()
            .id(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"))
            .tipo("categoria 2")
            .build();

        CategoriaRequest categoriaCreateDto = CategoriaRequest.builder()
            .tipo("categoria 1")
            .build();

    String endPoint = "/categorias";


    @BeforeEach
    void setUp() {
        categoriaCreateDto = CategoriaRequest.builder()
                .tipo("categoria 1")
                .build();
    }

    @Autowired
    public CategoriaServicesImp(CategoriaServicesImp service) {
        this.service = service;
    }


    @Test
    void getCategoria() throws Exception {
        when(service.findById(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"))).thenReturn(categoria1);

        MockHttpServletResponse response = mockMvc.perform(get(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Categoria categoria = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(categoria1.getId(), categoria.getId()),
                () -> assertEquals(categoria1.getTipo(), categoria.getTipo())
        );

        verify(service, times(1)).findById(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"));
    }

    @Test
    void getCategorieNotFound() throws Exception {
        when(service.findById(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"))).thenThrow(new CategoriaNotFound(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")));

        MockHttpServletResponse response = mockMvc.perform(get(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ErrorResponse errorResponse = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
                () -> assertEquals("Categoria con id 3930e05a-7ebf-4aa1-8aa8-5d7466fa9734 no encontrada", errorResponse.getClass())
        );

        verify(service, times(1)).findById(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"));
    }

    @Test
    void addCategoria() throws Exception {
        when(service.save(categoriaCreateDto)).thenReturn(categoria1);

        MockHttpServletResponse response = mockMvc.perform(post(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoriaCreateDto)))
                .andReturn().getResponse();

        Categoria categoria = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(categoria1.getId(), categoria.getId()),
                () -> assertEquals(categoria1.getTipo(), categoria.getTipo())
        );

        verify(service, times(1)).save(categoriaCreateDto);
    }

    @Test
    void addCategoriaWithoutName() throws Exception {
        categoriaCreateDto.setTipo(null);

        MockHttpServletResponse response = mockMvc.perform(post(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoriaCreateDto)))
                .andReturn().getResponse();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> res = mapper.readValue(response.getContentAsString(), mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class) );
        LinkedHashMap<String, Object> errors = (LinkedHashMap<String, Object>) res.get("errors");


        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
                () -> assertEquals( HttpStatus.BAD_REQUEST.value(),res.get("code")),
                () -> assertEquals("El nombre no puede estar vacio", errors.get("nombre"))
        );

        verify(service, times(0)).save(categoriaCreateDto);
    }

    @Test
    void addCategoriaAlreadyExistSameName() throws Exception {
        when(service.save(categoriaCreateDto)).thenThrow(new CategoriaConflict("Ya existe una categoria con el nombre: " + categoriaCreateDto.getTipo()));

        MockHttpServletResponse response = mockMvc.perform(post(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoriaCreateDto)))
                .andReturn().getResponse();

        ErrorResponse errorResponse = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertAll(
                () -> assertEquals(HttpStatus.CONFLICT.value(), response.getStatus()),
                () -> assertEquals("Ya existe una categoria con el nombre: " + categoriaCreateDto.getTipo(), errorResponse.getClass()33)
        );

        verify(service, times(1)).save(categoriaCreateDto);
    }

    @Test
    void updateCategoria() throws Exception {
        when(service.update(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"), categoriaCreateDto)).thenReturn(categoria1);

        MockHttpServletResponse response = mockMvc.perform(put(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoriaCreateDto)))
                .andReturn().getResponse();

        Categoria categoria = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(categoria1.getId(), categoria.getId()),
                () -> assertEquals(categoria1.getTipo(), categoria.getTipo())
        );

        verify(service, times(1)).update(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"), categoriaCreateDto);
    }

    @Test
    void updateWithoutName() throws Exception{
        categoriaCreateDto.setTipo(null);

        MockHttpServletResponse response = mockMvc.perform(put(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoriaCreateDto)))
                .andReturn().getResponse();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> res = mapper.readValue(response.getContentAsString(), mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class) );
        LinkedHashMap<String, Object> errors = (LinkedHashMap<String, Object>) res.get("errors");
    }

    @Test
    void updateCategoriaAlreadyExistSameName() throws Exception {
        when(service.update(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"), categoriaCreateDto)).thenThrow(new CategoriaConflictException("Ya existe una categoria con el nombre: " + categoriaCreateDto.getNombre()));

        MockHttpServletResponse response = mockMvc.perform(put(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoriaCreateDto)))
                .andReturn().getResponse();

        ErrorResponse errorResponse = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertAll(
                () -> assertEquals(HttpStatus.CONFLICT.value(), response.getStatus()),
                () -> assertEquals("Ya existe una categoria con el nombre: " + categoriaCreateDto.getTipo(), errorResponse.msg())
        );

        verify(service, times(1)).update(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"), categoriaCreateDto);
    }

    @Test
    void updateCategoriaNotFound() throws Exception {
        when(service.update(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"), categoriaCreateDto)).thenThrow(new CategoriaNotFoundException(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")));

        MockHttpServletResponse response = mockMvc.perform(put(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoriaCreateDto)))
                .andReturn().getResponse();

        ErrorResponse errorResponse = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
                () -> assertEquals("Categoria con id 3930e05a-7ebf-4aa1-8aa8-5d7466fa9734 no encontrada", errorResponse.msg())
        );

        verify(service, times(1)).update(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"), categoriaCreateDto);
    }

    @Test
    void deleteCategoria() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(delete(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus())
        );

        verify(service, times(1)).deleteById(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"));
    }

    @Test
    void deleteCategoriaNotFound() throws Exception {
        doThrow(new CategoriaNotFound(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"))).when(service).deleteById(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"));

        MockHttpServletResponse response = mockMvc.perform(delete(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ErrorResponse errorResponse = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
                () -> assertEquals("Categoria con id 3930e05a-7ebf-4aa1-8aa8-5d7466fa9734 no encontrada", errorResponse.msg())
        );

        verify(service, times(1)).deleteById(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"));
    }

    @Test
    void deleteCategoriaBadRequest() throws Exception {
        doThrow(new CategoriaConflict("No se puede eliminar la categoria porque tiene funkos asociados")).when(service).deleteById(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"));

        MockHttpServletResponse response = mockMvc.perform(delete(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ErrorResponse errorResponse = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertAll(
                () -> assertEquals(HttpStatus.CONFLICT.value(), response.getStatus())
        );

        verify(service, times(1)).deleteById(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"));
    }



}