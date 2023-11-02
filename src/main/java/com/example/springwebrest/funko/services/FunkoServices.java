package com.example.springwebrest.funko.services;

import com.example.springwebrest.funko.dto.FunkoCreateRequest;
import com.example.springwebrest.funko.dto.FunkoResponseDto;
import com.example.springwebrest.funko.dto.FunkoUpdateRequest;
import com.example.springwebrest.funko.models.Funko;

import java.util.List;
import java.util.UUID;

public interface FunkoServices {
    List<Funko> findAll();
    FunkoResponseDto findById(Long id);
    FunkoResponseDto save(FunkoCreateRequest funko);
    FunkoResponseDto update(Long id, FunkoUpdateRequest funko);
    void deleteById(Long id);
    void deleteAll();
}
