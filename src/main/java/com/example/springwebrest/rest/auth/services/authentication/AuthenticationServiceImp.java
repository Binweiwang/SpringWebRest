package com.example.springwebrest.rest.auth.services.authentication;

import com.example.springwebrest.rest.auth.dto.JwtAuthResponse;
import com.example.springwebrest.rest.auth.dto.UserSignInRequest;
import com.example.springwebrest.rest.auth.dto.UserSignUpRequest;
import com.example.springwebrest.rest.auth.exceptions.AuthSingInInvalid;
import com.example.springwebrest.rest.auth.exceptions.UserAuthNameOrEmailExisten;
import com.example.springwebrest.rest.auth.exceptions.UserDiferentePasswords;
import com.example.springwebrest.rest.auth.repositories.AuthUsersRepository;
import com.example.springwebrest.rest.auth.services.jwt.JwtService;
import com.example.springwebrest.rest.users.models.Role;
import com.example.springwebrest.rest.users.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class AuthenticationServiceImp implements AuthenticationService {
    private final AuthUsersRepository authUsersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationServiceImp(AuthUsersRepository authUsersRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.authUsersRepository = authUsersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public JwtAuthResponse signUp(UserSignUpRequest request) {
        log.info("Creando usuario: {}", request);
        if (request.getPassword().contentEquals(request.getPasswordComprobacion())) {
            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .email(request.getEmail())
                    .nombre(request.getNombre())
                    .apellidos(request.getApellidos())
                    .roles(Stream.of(Role.USER).collect(Collectors.toSet()))
                    .build();

            try {
                var userStored = authUsersRepository.save(user);
                return JwtAuthResponse.builder()
                        .token(jwtService.generateToken(userStored))
                        .build();
            } catch (DataIntegrityViolationException e) {
                throw new UserAuthNameOrEmailExisten("El usuario con username " + request.getUsername() + " o email " + request.getEmail() + " ya existe");
            }
        } else {
            throw new UserDiferentePasswords("Las contraseñas no coinciden");
        }
    }

    @Override
    public JwtAuthResponse signIn(UserSignInRequest request) {
        log.info("Autenticando usuario: {}", request);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = authUsersRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthSingInInvalid("Usuario o contraseña incorrectos"));
        var jwt = jwtService.generateToken(user);
        return JwtAuthResponse.builder()
                .token(jwt)
                .build();
    }
}
