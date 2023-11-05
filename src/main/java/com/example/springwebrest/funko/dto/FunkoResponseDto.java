package com.example.springwebrest.funko.dto;

import com.example.springwebrest.categoria.models.Categoria;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class FunkoResponseDto {
    private Long id;
    private String name;
    @Min(value = 0, message = "Precio no puede ser menor a 0")
    private Double price;
    @Min(value = 0, message = "Cantidad no puede ser menor a 0")
    private Integer quantity;
    private String image;
    @NotEmpty(message = "Categoria no puede estar vacio")
    private Categoria categoria;
}