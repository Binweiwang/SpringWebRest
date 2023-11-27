package com.example.springwebrest.categoria.controllers;

import com.example.springwebrest.rest.categoria.dto.CategoriaRequest;
import com.example.springwebrest.rest.categoria.exceptions.CategoriaConflict;
import com.example.springwebrest.rest.categoria.exceptions.CategoriaNotFound;
import com.example.springwebrest.rest.categoria.models.Categoria;
import com.example.springwebrest.rest.categoria.services.CategoriaServices;
import com.example.springwebrest.rest.categoria.services.CategoriaServicesImp;
import com.example.springwebrest.utils.pagination.PageResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.ErrorResponse;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
public class CategoriaConstollersTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    Categoria categoria1 = Categoria.builder()
            .id(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"))
            .tipo("DISNEY")
            .build();
    Categoria categoria2 = Categoria.builder()
            .id(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"))
            .tipo("DISNEY")
            .build();
    CategoriaRequest categoriaCreateDto = CategoriaRequest.builder()
            .tipo("DISNEY")
            .build();
    String endPoint = "/v1/categorias";
    @MockBean
    private CategoriaServicesImp service;


    @Autowired
    public CategoriaConstollersTest(CategoriaServicesImp service) {
        this.service = service;
        mapper.registerModule(new JavaTimeModule());
    }


    @Test
    void getCategorias() throws Exception {
        // Arrange
        var categoriasList = List.of(categoria1, categoria2);
        Page<Categoria> page = new PageImpl<>(categoriasList);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        // Act
        when(service.findAll(Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(endPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

PageResponse<Categoria> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        // Assert
        assertAll("findallCategorias",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.pageSize())
        );

    }

    @Test
    void getCategorieNotFound() throws Exception {
        when(service.findById(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"))).thenThrow(new CategoriaNotFound(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")));

        MockHttpServletResponse response = mockMvc.perform(get(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    void addCategoria() throws Exception {
        when(service.save(categoriaCreateDto)).thenReturn(categoria1);

        MockHttpServletResponse response = mockMvc.perform(post(endPoint)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoriaCreateDto)))
                .andReturn().getResponse();

        Categoria categoria = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(categoria1.getId(), categoria.getId()),
                () -> assertEquals(categoria1.getTipo(), categoria.getTipo())
        );
    }

    @Test
    void addCategoriaAlreadyExistSameName() throws Exception {
        when(service.save(categoriaCreateDto)).thenThrow(new CategoriaConflict("Ya existe una categoria con el nombre: " + categoriaCreateDto.getTipo()));

        MockHttpServletResponse response = mockMvc.perform(post(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoriaCreateDto)))
                .andReturn().getResponse();


        assertAll(
                () -> assertEquals(HttpStatus.CONFLICT.value(), response.getStatus())

        );
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
    void updateWithoutName() throws Exception {
        categoriaCreateDto.setTipo(null);

        MockHttpServletResponse response = mockMvc.perform(put(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoriaCreateDto)))
                .andReturn().getResponse();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> res = mapper.readValue(response.getContentAsString(), mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
        LinkedHashMap<String, Object> errors = (LinkedHashMap<String, Object>) res.get("errors");
    }

    @Test
    void updateCategoriaAlreadyExistSameName() throws Exception {
        when(service.update(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"), categoriaCreateDto)).thenThrow(new CategoriaConflict("Ya existe una categoria con el nombre: " + categoriaCreateDto.getTipo()));

        MockHttpServletResponse response = mockMvc.perform(put(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoriaCreateDto)))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.CONFLICT.value(), response.getStatus())
        );

        verify(service, times(1)).update(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"), categoriaCreateDto);
    }

    @Test
    void updateCategoriaNotFound() throws Exception {
        when(service.update(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"), categoriaCreateDto)).thenThrow(new CategoriaNotFound(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")));

        MockHttpServletResponse response = mockMvc.perform(put(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoriaCreateDto)))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );

        verify(service, times(1)).update(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"), categoriaCreateDto);
    }

    @Test
    void deleteCategoria() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(delete(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus())
        );

    }

    @Test
    void deleteCategoriaNotFound() throws Exception {
        doThrow(new CategoriaNotFound(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"))).when(service).deleteById(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"));

        MockHttpServletResponse response = mockMvc.perform(delete(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    void deleteCategoriaBadRequest() throws Exception {
        doThrow(new CategoriaConflict("No se puede eliminar la categoria porque tiene funkos asociados")).when(service).deleteById(UUID.fromString("3930e05a-7ebf-4aa1-8aa8-5d7466fa9734"));

        MockHttpServletResponse response = mockMvc.perform(delete(endPoint + "/3930e05a-7ebf-4aa1-8aa8-5d7466fa9734")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.CONFLICT.value(), response.getStatus())
        );
    }
}