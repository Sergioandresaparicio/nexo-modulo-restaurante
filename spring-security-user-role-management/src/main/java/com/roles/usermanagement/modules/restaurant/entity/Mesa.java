package com.roles.usermanagement.modules.restaurant.entity;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesa;
import jakarta.persistence.*;
import lombok.*;
// util se debe llamar solo lo que se necesita porque ralentiza el program al ser tan grande
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name ="restaurant_mesa")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer numeroMesa;

    @Column(nullable = false)
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMesa estado = EstadoMesa.LIBRE;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesero_id")
    private Mesero mesero;

    @OneToMany(mappedBy = "mesa", fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();

}
