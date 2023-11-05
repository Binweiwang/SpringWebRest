package com.example.springwebrest.categoria.controllers;

import com.example.springwebrest.categoria.dto.CategoriaRequest;
import com.example.springwebrest.categoria.mapper.CategoriasMapper;
import com.example.springwebrest.categoria.models.Categoria;
import com.example.springwebrest.categoria.services.CategoriaServices;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class CategoriaConstollersTest {

    private final String myEndpoint = "/categorias";

    private final Categoria categoria1 = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "TEST", LocalDateTime.now(), LocalDateTime.now(), false);
    private final Categoria categoria2 = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "TEST", LocalDateTime.now(), LocalDateTime.now(), false);
    private final Categoria categoriaBadReques = new Categoria(null, null, LocalDateTime.now(), LocalDateTime.now(), false);
    private final CategoriaRequest categoriaRequest = new CategoriaRequest("DISNEY", true);
    private final ObjectMapper mapper = new ObjectMapper();
    private final CategoriasMapper categoriasMapper = new CategoriasMapper();

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private CategoriaServices categoriaServices;

    @Autowired
    public CategoriaConstollersTest(CategoriaServices categoriaServices) {
        this.categoriaServices = categoriaServices;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllCategorias() throws Exception {
    when(categoriaServices.findAll()).thenReturn(List.of(categoria1, categoria2));

        MockHttpServletResponse response = mockMvc.perform(
                get(myEndpoint)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Categoria> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll("find all categorias",
                ()-> assertEquals(200,response.getStatus()),
                ()-> assertEquals(2,res.size())
        );
    }

    @Test
    void getCategoriaById() throws Exception {
    when(categoriaServices.findById(categoria1.getId())).thenReturn(categoria1);

    MockHttpServletResponse response = mockMvc.perform(
                get(myEndpoint + "/" + categoria1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


    Categoria res = mapper.readValue(response.getContentAsString(), Categoria.class);

    assertAll("find categoria by id",
                ()-> assertEquals(200,response.getStatus()),
                ()-> assertEquals(categoria1.getId(),res.getId())
        );
    }

    @Test
    void createCategoria() throws Exception {
        when(categoriaServices.save(categoriaRequest)).thenReturn(categoria1);

        MockHttpServletResponse response = mockMvc.perform(
                post(myEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoriaRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Categoria res = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll("create categoria",
                ()-> assertEquals(201,response.getStatus()),
                ()-> assertEquals(categoria1.getId(),res.getId())
        );
    }

    @Test
    void updateCategoria() throws Exception {
        when(categoriaServices.update(categoria1.getId(),categoriaRequest)).thenReturn(categoria1);

        MockHttpServletResponse response = mockMvc.perform(
                put(myEndpoint + "/" + categoria1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoriaRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Categoria res = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll("update categoria",
                ()-> assertEquals(200,response.getStatus()),
                ()-> assertEquals(categoria1.getId(),res.getId())
        );
    }


    @Test
    void deleteCategoria() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                delete(myEndpoint + "/" + categoria1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll("delete categoria",
                ()-> assertEquals(204,response.getStatus())
        );
    }

    @Test
    void badRequest() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                put(myEndpoint + "/" + categoria1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoriaBadReques))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll("bad request",
                ()-> assertEquals(400,response.getStatus())
        );
    }
}
