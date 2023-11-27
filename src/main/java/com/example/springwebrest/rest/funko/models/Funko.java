package com.example.springwebrest.rest.funko.models;


import com.example.springwebrest.rest.categoria.models.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "FUNKOS")
@Schema(name = "Funkos")
public class Funko {
    public static final String IMAGE_DEFAULT ="https://via.placeholder.com/150";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador del funko", example = "1")
    private Long id;
    @NotEmpty(message = "El nombre no puede estar vacio")
    @Schema(description = "Nombre del funko", example = "Funko 1")
    private String name;
    @Builder.Default
    @Schema(description = "Precio del funko", example = "10.0")
    private Double price = 0.0;
    @Builder.Default
    @Schema(description = "Cantidad del funko", example = "10")
    private Integer quantity = 0;
    @Builder.Default
    @Schema(description = "Imagen del funko", example = "https://via.placeholder.com/150")
    private String image = IMAGE_DEFAULT;
    @NotNull(message = "La categoria no puede estar vacia")
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @Schema(description = "Categoria del funko", example = "DISNEY")
    private Categoria categoria;
    @Builder.Default
    @Schema(description = "Fecha de creación del funko", example = "2021-10-01T00:00:00.000Z")
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    @Schema(description = "Fecha de actualización del funko", example = "2021-10-01T00:00:00.000Z")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
