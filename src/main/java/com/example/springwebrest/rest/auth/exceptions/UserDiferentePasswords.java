package com.example.springwebrest.rest.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserDiferentePasswords extends AuthException {
    public UserDiferentePasswords(String message) {
        super(message);
    }
}
