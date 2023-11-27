package com.example.springwebrest.rest.funko.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Funko a crear")
public class FunkoCreateRequest {
    private static final String IMAGE_DEFAULT ="https://via.placeholder.com/150";
    @Min(value = 0, message = "Id no puede ser menor a 0")
    @Schema(description = "Identificador del funko", example = "1")
    private final Long id;
    @NotBlank(message = "Nombre no puede estar vacio")
    @Schema(description = "Nombre del funko", example = "Funko 1")
    private final String name;
    @NotNull(message = "Precio no puede estar vacio")
    @Min(value = 0, message = "Precio no puede ser menor a 0")
    @Schema(description = "Precio del funko", example = "10.0")
    private final Double price;
    @NotNull(message = "Cantidad no puede estar vacio")
    @Min(value = 0, message = "Cantidad no puede ser menor a 0")
    @Schema(description = "Cantidad del funko", example = "10")
    private final Integer quantity;
    @Builder.Default
    @Schema(description = "Imagen del funko", example = "https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif")
    private final String image = IMAGE_DEFAULT;
    @NotBlank(message = "Categoria no puede estar vacio")
    @Schema(description = "Categoria del funko", example = "DISNEY")
    private final String categoria;
}
