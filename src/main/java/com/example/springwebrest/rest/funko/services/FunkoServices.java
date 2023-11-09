package com.example.springwebrest.rest.funko.services;

import com.example.springwebrest.rest.funko.dto.FunkoCreateRequest;
import com.example.springwebrest.rest.funko.dto.FunkoResponseDto;
import com.example.springwebrest.rest.funko.dto.FunkoUpdateRequest;
import com.example.springwebrest.rest.funko.models.Funko;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FunkoServices {
    List<Funko> findAll();
    FunkoResponseDto findById(Long id);
    FunkoResponseDto save(FunkoCreateRequest funko);
    FunkoResponseDto update(Long id, FunkoUpdateRequest funko);
    void deleteById(Long id);
    void deleteAll();

    FunkoResponseDto updateImage(Long id, MultipartFile image, Boolean withUrl);
}
