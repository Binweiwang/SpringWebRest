package com.example.springwebrest.rest.users.exceptions;

public class UserNotFound extends UserException {
    public UserNotFound(String message) {
        super(message);
    }
    public UserNotFound(Long id){
        super("Usuario con id " + id + " no encontrado");
    }
}
