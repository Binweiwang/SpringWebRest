package com.example.springwebrest.rest.funko.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FunkoNotFound extends FunkoException {
    public FunkoNotFound(String message) {
        super(message);
    }
}
