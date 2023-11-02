package com.example.springwebrest.funko.models;


import com.example.springwebrest.categoria.models.Categoria;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.Builder.Default;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "funkos")
public class Funko {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    @NotEmpty(message = "El nombre no puede estar vacio")
    private String name;
    @Column
    @Default
    private Double price = 0.0;
    @Default
    @Column
    private int quantity = 0;
    @Column
    @Default
    private String image = "no image";
    @NotNull(message = "La categoria no puede estar vacia")
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    @Column
    @Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column
    @Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
