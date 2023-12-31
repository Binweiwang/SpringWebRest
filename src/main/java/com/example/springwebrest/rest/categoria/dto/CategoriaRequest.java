package com.example.springwebrest.rest.categoria.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaRequest {
    @NotEmpty(message = "El nombre no puede estar vacio")
    @Pattern(regexp= "SERIES|DISNEY|SUPERHEROES|PELICULA|OTROS", message = "El tipo de categoria no es valido")
    private String tipo;
    private boolean Active;
}
