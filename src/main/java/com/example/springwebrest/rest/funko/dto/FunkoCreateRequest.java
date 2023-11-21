package com.example.springwebrest.rest.funko.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
    private final Double price;
    @NotNull(message = "Cantidad no puede estar vacio")
    @Min(value = 0, message = "Cantidad no puede ser menor a 0")
    private final Integer quantity;
    @Builder.Default
    private final String image = "https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif";
    @NotBlank(message = "Categoria no puede estar vacio")
    private final String categoria;
}
