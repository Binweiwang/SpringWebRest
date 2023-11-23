package com.example.springwebrest.rest.auth.services.jwt;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUserName(String token);

    String generateToken(UserDetails username);

    boolean isTokenValid(String token,  UserDetails userDetails);
}
