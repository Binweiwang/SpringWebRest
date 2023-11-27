package com.example.springwebrest.rest.funko.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Funko a actualizar")
public class FunkoUpdateRequest {
    @Schema(description = "Identificador del funko", example = "funko")
    private  String name;
    @Min(value = 0, message = "Precio no puede ser menor a 0")
    @Schema(description = "Precio del funko", example = "100.0")
    private  Double price;
    @Min(value = 0, message = "Cantidad no puede ser menor a 0")
    @Schema(description = "Cantidad del funko", example = "10")
    private  Integer quantity;
    @Schema(description = "Imagen del funko", example = "https://via.placeholder.com/150")
    private  String image;
    @NotBlank(message = "Categoria no puede estar vacio")
    @Schema(description = "Categoria del funko", example = "DISNEY")
    private  String categoria;
}
