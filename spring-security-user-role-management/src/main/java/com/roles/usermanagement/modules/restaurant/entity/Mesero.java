package com.roles.usermanagement.modules.restaurant.entity;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesero;
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
@Table(name ="restaurant_mesero")

public class Mesero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    @NonNull
    private String nombre;

    @Column(nullable = false, length = 10)
    private String documentoIdentidad;

    @Column(length = 10)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMesero estado =EstadoMesero.ACTIVO;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "mesero", fetch = FetchType.LAZY)
    private List<Mesa> mesas = new ArrayList<>();
}
