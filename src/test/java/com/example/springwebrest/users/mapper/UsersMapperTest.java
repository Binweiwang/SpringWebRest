package com.example.springwebrest.users.mapper;

import com.example.springwebrest.rest.pedidos.models.Cliente;
import com.example.springwebrest.rest.pedidos.models.Direccion;
import com.example.springwebrest.rest.pedidos.models.Pedido;
import com.example.springwebrest.rest.users.dto.UserInfoResponse;
import com.example.springwebrest.rest.users.dto.UserRequest;
import com.example.springwebrest.rest.users.dto.UserResponse;
import com.example.springwebrest.rest.users.mappers.UsersMapper;
import com.example.springwebrest.rest.users.models.Role;
import com.example.springwebrest.rest.users.models.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersMapperTest {
    private final UserRequest userRequest = UserRequest.builder()
            .nombre("Test User")
            .apellidos("Test User")
            .username("testuser")
            .email("test@test.com")
            .password("test")
            .roles(Collections.singleton(Role.USER))
            .isDeleted(false)
            .build();
    private final User user = User.builder()
            .id(1L)
            .nombre("Test User")
            .apellidos("Test User")
            .username("testuser")
            .email("test@test.com")
            .password("test")
            .roles(Collections.singleton(Role.USER))
            .isDeleted(false)
            .build();
    private final Pedido pedido1 = Pedido.builder()
            .id(new ObjectId("5f9d5f8d9c6d2e1d9c6d2e1d"))
            .idUsuario(1L)
            .cliente(new Cliente("Test User", "Test User", "12346789", new Direccion("Test User", "Test User", "Test User", "Test User", "Test User", "Test User")))
            .totalItems(1)
            .total(100.0)
            .build();
    private final Pedido pedido2 = Pedido.builder()
            .id(new ObjectId("5f9d5f8d9c6d2e1d9c6d2e1d"))
            .idUsuario(1L)
            .cliente(new Cliente("Test User", "Test User", "12346789", new Direccion("Test User", "Test User", "Test User", "Test User", "Test User", "Test User")))
            .totalItems(1)
            .total(100.0)
            .build();
    private final List<String> pedidos = List.of(pedido1.getId().toString(), pedido2.getId().toString());
    private UsersMapper userMapper;

    @BeforeEach
    public void setup() {
        userMapper = new UsersMapper();
    }

    @Test
    public void toUserTest() {
        User user = userMapper.toUser(userRequest);
        assertAll("User",
                () -> assertEquals(user.getNombre(), userRequest.getNombre()),
                () -> assertEquals(user.getApellidos(), userRequest.getApellidos()),
                () -> assertEquals(user.getUsername(), userRequest.getUsername()),
                () -> assertEquals(user.getEmail(), userRequest.getEmail()),
                () -> assertEquals(user.getPassword(), userRequest.getPassword()),
                () -> assertEquals(user.getRoles(), userRequest.getRoles()),
                () -> assertEquals(user.getIsDeleted(), userRequest.getIsDeleted()));
    }

    @Test
    public void toUserIdTest() {
        User user = userMapper.toUser(userRequest, 1L);
        assertAll("User",
                () -> assertEquals(user.getId(), 1L),
                () -> assertEquals(user.getNombre(), userRequest.getNombre()),
                () -> assertEquals(user.getApellidos(), userRequest.getApellidos()),
                () -> assertEquals(user.getUsername(), userRequest.getUsername()),
                () -> assertEquals(user.getEmail(), userRequest.getEmail()),
                () -> assertEquals(user.getPassword(), userRequest.getPassword()),
                () -> assertEquals(user.getRoles(), userRequest.getRoles()),
                () -> assertEquals(user.getIsDeleted(), userRequest.getIsDeleted()));

    }

    @Test
    public void toUserResponseTest() {
        UserResponse userResponse = userMapper.toUserResponse(this.user);
        assertAll("User response",
                () -> assertEquals(userResponse.getId(), this.user.getId()),
                () -> assertEquals(userResponse.getNombre(), this.user.getNombre()),
                () -> assertEquals(userResponse.getApellidos(), this.user.getApellidos()),
                () -> assertEquals(userResponse.getUsername(), this.user.getUsername()),
                () -> assertEquals(userResponse.getEmail(), this.user.getEmail()),
                () -> assertEquals(userResponse.getRoles(), this.user.getRoles()),
                () -> assertEquals(userResponse.getIsDeleted(), this.user.getIsDeleted())
        );
    }

    @Test
    public void toUserInfoResponseTest() {
        UserInfoResponse userInfoResponse = userMapper.toUserInfoResponse(user, pedidos);
        assertAll("User response",
                () -> assertEquals(userInfoResponse.getId(), this.user.getId()),
                () -> assertEquals(userInfoResponse.getNombre(), this.user.getNombre()),
                () -> assertEquals(userInfoResponse.getApellidos(), this.user.getApellidos()),
                () -> assertEquals(userInfoResponse.getUsername(), this.user.getUsername()),
                () -> assertEquals(userInfoResponse.getEmail(), this.user.getEmail()),
                () -> assertEquals(userInfoResponse.getRoles(), this.user.getRoles()),
                () -> assertEquals(userInfoResponse.getIsDeleted(), this.user.getIsDeleted())
        );
    }
}
