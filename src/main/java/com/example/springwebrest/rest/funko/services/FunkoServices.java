package com.example.springwebrest.rest.funko.services;

import com.example.springwebrest.rest.funko.dto.FunkoCreateRequest;
import com.example.springwebrest.rest.funko.dto.FunkoResponseDto;
import com.example.springwebrest.rest.funko.dto.FunkoUpdateRequest;
import com.example.springwebrest.rest.funko.models.Funko;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface FunkoServices {
    Page<FunkoResponseDto> findAll(Optional<String> name, Optional<Double> price, Optional<Integer> quantity, Optional<String> categoria, Pageable pageable);

    FunkoResponseDto findById(Long id);
    FunkoResponseDto save(FunkoCreateRequest funko);
    FunkoResponseDto update(Long id, FunkoUpdateRequest funko);
    void deleteById(Long id);
    void deleteAll();

    FunkoResponseDto updateImage(Long id, MultipartFile image, Boolean withUrl);
}
