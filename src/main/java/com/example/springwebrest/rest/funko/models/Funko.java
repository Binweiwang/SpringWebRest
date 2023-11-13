package com.example.springwebrest.rest.funko.models;


import com.example.springwebrest.rest.categoria.models.Categoria;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "funkos")
public class Funko {
    public static final String IMAGE_DEFAULT ="https://via.placeholder.com/150";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    @NotEmpty(message = "El nombre no puede estar vacio")
    private String name;
    @Column
    @Builder.Default
    private Double price = 0.0;
    @Builder.Default
    @Column
    private Integer quantity = 0;
    @Column
    @Builder.Default
    private String image = IMAGE_DEFAULT;
    @NotNull(message = "La categoria no puede estar vacia")
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    @Column
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
