package com.example.springwebrest.rest.pedidos.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("pedidos")
@TypeAlias("Pedido")
@EntityListeners(AuditingEntityListener.class)
public class Pedido {
    @Id
    @Builder.Default
    private ObjectId id = new ObjectId();

    @NotNull(message = "El id del usuario no puede ser nulo")
    private Long idUsuario;

    @NotNull(message = "El id del cliente no puede ser nulo")
    @Valid
    private Cliente cliente;

    @NotNull(message = "El pedido debe tener al menos una línea de pedido")
    private List<@Valid LineaPedido> lineasPedido;

    @Builder.Default()
    private Integer totalItems = 0;

    @Builder.Default()
    private Double total = 0.0;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default()
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default()
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder.Default()
    private Boolean isDeleted = false;

    @JsonProperty("id")
    public String get_id() {
        return id.toHexString();
    }
    public void setLineasPedido(List<LineaPedido> lineasPedido) {
        this.lineasPedido = lineasPedido;
        this.totalItems = lineasPedido != null ? lineasPedido.size() : 0;
        this.total = lineasPedido != null ? lineasPedido.stream().mapToDouble(LineaPedido::getTotal).sum() : 0.0;
    }
}
