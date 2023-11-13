package com.example.springwebrest.rest.funko.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FunkoUpdateRequest {
    private  String name;
    @Min(value = 0, message = "Precio no puede ser menor a 0")
    private  Double price;
    @Min(value = 0, message = "Cantidad no puede ser menor a 0")
    private  Integer quantity;
    private  String image;
    @NotBlank(message = "Categoria no puede estar vacio")
    private  String categoria;
}
