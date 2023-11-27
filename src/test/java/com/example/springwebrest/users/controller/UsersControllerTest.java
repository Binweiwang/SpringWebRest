package com.example.springwebrest.users.controller;

import com.example.springwebrest.rest.users.dto.UserInfoResponse;
import com.example.springwebrest.rest.users.dto.UserRequest;
import com.example.springwebrest.rest.users.dto.UserResponse;
import com.example.springwebrest.rest.users.models.User;
import com.example.springwebrest.rest.users.services.UsersService;
import com.example.springwebrest.utils.pagination.PageResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
public class UsersControllerTest {
    private final UserRequest userRequest = UserRequest.builder()
            .nombre("test")
            .apellidos("test")
            .username("test")
            .password("admin")
            .email("test@test.com")
            .build();

    private final User userTest = User.builder()
            .nombre("test")
            .apellidos("test")
            .username("test")
            .password("admin")
            .email("test@test.com")
            .build();

    private final UserResponse userResponse = UserResponse.builder()
            .nombre("test")
            .apellidos("test")
            .username("test")
            .email("test@test.com")
            .build();

    private final UserInfoResponse userInfoResponse = UserInfoResponse.builder()
            .nombre("test")
            .apellidos("test")
            .username("test")
            .email("test@test.com")
            .build();
    private final String myEndPoint = "/v1/users";
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;
    @MockBean
    private UsersService usersService;

    @Test
    @WithAnonymousUser
    void NotAuthenticated() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                get(myEndPoint)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(403, response.getStatus());
    }
    @Test
    void findAll() throws Exception {
        // Arrange
        var list = List.of(userResponse);
        Page<UserResponse> page = new PageImpl<>(list);
        Pageable pageable = PageRequest.of(0,10, Sort.by("id").ascending());

        when(usersService.findAll(Optional.empty(),Optional.empty(),Optional.empty(),pageable)).thenReturn(page);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(myEndPoint)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<UserResponse> res = mapper.readValue(response.getContentAsString(), new TypeReference<>(){});

        // Assert
        assertAll("findAll",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );
    }

    @Test
    void findById() throws Exception {
        // Arrange
        when(usersService.findById(anyLong())).thenReturn(userInfoResponse);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(myEndPoint+"/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        UserInfoResponse res = mapper.readValue(response.getContentAsString(), new TypeReference<>(){});

        // Assert
        assertAll("findById",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("test", res.getNombre())
        );
    }
}
