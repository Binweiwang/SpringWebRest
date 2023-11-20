package com.example.springwebrest.rest.funko.dto;

import com.example.springwebrest.rest.categoria.models.Categoria;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
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