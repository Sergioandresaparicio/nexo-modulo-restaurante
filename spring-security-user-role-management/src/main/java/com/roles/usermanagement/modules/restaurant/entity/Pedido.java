package com.roles.usermanagement.modules.restaurant.entity;
import com.roles.usermanagement.modules.restaurant.enums.EstadoPedido;
import jakarta.persistence.*;
import lombok.*;
// util se debe llamar solo lo que se necesita porque ralentiza el program al ser tan grande
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name ="restaurant_pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)

    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)

    @JoinColumn(name = "mesero_id", nullable = false)
    private Mesero mesero;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(length = 255)
    private String observaciones;

    @Column(nullable = false, length = 50)
    private String createdBy;

}
