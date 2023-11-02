package com.example.springwebrest.funko.dto;

import com.example.springwebrest.categoria.models.Categoria;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class FunkoCreateRequest {
    @Min(value = 0, message = "Id no puede ser menor a 0")
    private final Long id;
    @NotBlank(message = "Nombre no puede estar vacio")
    private final String name;
    @NotNull(message = "Precio no puede estar vacio")
    @Min(value = 0, message = "Precio no puede ser menor a 0")
    @Max(value = 100000000, message = "Precio no puede ser mayor a 100000000")
    private final Double price;
    @NotNull(message = "Cantidad no puede estar vacio")
    @Min(value = 0, message = "Cantidad no puede ser menor a 0")
    @Max(value = 100000000, message = "Cantidad no puede ser mayor a 100000000")
    private final Integer quantity;
    @NotBlank(message = "Imagen no puede estar vacio")
    private final String image;
    @NotBlank(message = "Categoria no puede estar vacio")
    private final String categoria;
}
