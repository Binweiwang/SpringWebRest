package com.example.springwebrest.rest.auth.services.users;

import com.example.springwebrest.rest.auth.repositories.AuthUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class AuthUsersServiceImp implements AuthUsersService{
    private final AuthUsersRepository authUsersRepository;
    @Autowired
    public AuthUsersServiceImp(AuthUsersRepository authUsersRepository) {
        this.authUsersRepository = authUsersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return authUsersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario con username " + username + " no encontrado"));
    }
}
