package com.example.springwebrest.funko.dto;

import com.example.springwebrest.categoria.models.Categoria;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FunkoUpdateRequest {
    private final String name;
    @Min(value = 0, message = "Precio no puede ser menor a 0")
    private final Double price;
    @Min(value = 0, message = "Cantidad no puede ser menor a 0")
    private final Integer quantity;
    private final String image;
    @NotBlank(message = "Categoria no puede estar vacio")
    private final String categoria;
}
