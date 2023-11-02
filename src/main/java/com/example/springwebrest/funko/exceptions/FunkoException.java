package com.example.springwebrest.funko.exceptions;

public abstract class FunkoException extends RuntimeException{
    public FunkoException(String message) {
        super(message);
    }
}
