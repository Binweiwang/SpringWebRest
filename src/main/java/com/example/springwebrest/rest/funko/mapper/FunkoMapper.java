package com.example.springwebrest.rest.funko.mapper;

import com.example.springwebrest.rest.funko.dto.FunkoCreateRequest;
import com.example.springwebrest.rest.funko.dto.FunkoResponseDto;
import com.example.springwebrest.rest.categoria.models.Categoria;
import com.example.springwebrest.rest.funko.dto.FunkoUpdateRequest;
import com.example.springwebrest.rest.funko.models.Funko;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class FunkoMapper {
    public FunkoResponseDto toFunko(Funko funko){
        return  FunkoResponseDto.builder()
                .id(funko.getId())
                .name(funko.getName())
                .price(funko.getPrice())
                .quantity(funko.getQuantity())
                .image(funko.getImage())
                .categoria(funko.getCategoria().getTipo())
                .build();
    }
    public Funko toFunko(FunkoUpdateRequest request, Funko Funko, Categoria categoria) {
        // Creamos el producto actualizado con los campos que nos llegan actualizando el updateAt y si son null no se actualizan y se quedan los anteriores
        return Funko.builder()
                .id(Funko.getId())
                .name(request.getName() != null ? request.getName() : Funko.getName())
                .price(request.getPrice() != null ? request.getPrice() : Funko.getPrice())
                .quantity(request.getQuantity() != null ? request.getQuantity() : Funko.getQuantity())
                .image(request.getImage() != null ? request.getImage() : Funko.getImage())
                .createdAt(Funko.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .categoria(categoria)
                .build();
    }
    public Funko toFunko(FunkoCreateRequest funko, Categoria categoria){
        return Funko.builder()
                .id(funko.getId())
                .name(funko.getName())
                .price(funko.getPrice())
                .quantity(funko.getQuantity())
                .image(funko.getImage())
                .categoria(categoria)
                .build();
    }
    public List<FunkoResponseDto> toResponses(List<Funko> funkos){
        return funkos.stream().map(this::toFunko).collect(Collectors.toList());
    }

}
