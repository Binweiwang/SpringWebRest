package com.example.springwebrest.funko.dto;

import com.example.springwebrest.categoria.models.Categoria;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
