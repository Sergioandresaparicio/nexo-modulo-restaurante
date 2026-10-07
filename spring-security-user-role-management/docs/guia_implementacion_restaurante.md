# Guía Paso a Paso: Implementación del Sistema de Gestión de Restaurante
**Dominio:** Restaurante (Mesero, Mesa, Pedido)  
**Arquitectura:** Paquetes segregados por responsabilidad (`entity`, `repository`, `enums`, `dto`, `service`, `controller`)  
**Estados del Mesero:** `ACTIVO` y `NO_ACTIVO` con validaciones de negocio en asignación de mesas y toma de pedidos.  
**Proyecto:** Spring Boot con Spring Security, JWT y Spring Data JPA  
**Integrantes:** Sergio Aparicio, Sebastian Ayala, Jhon Reyes  

---

## 1. Visión General y Estructura del Módulo

Esta guía detalla la implementación del módulo de **Gestión de Restaurante** organizando cada componente en su respectivo subpaquete (`entity`, `repository`, `enums`, `dto`, `service`, `controller`), incorporando el ciclo de vida del **Mesero** mediante los estados formales `ACTIVO` y `NO_ACTIVO` administrados a través de un enumerador dedicado (`EstadoMesero`), controlando las reglas de negocio en mesas y pedidos:

1. **Mesero**: Maneja estado `ACTIVO` y `NO_ACTIVO`. Un mesero en estado `NO_ACTIVO` no puede ser asignado a mesas nuevas ni puede abrir pedidos.
2. **Mesa**: Maneja estados `LIBRE`, `OCUPADA`, `SUCIA`. Solo permite asignación a meseros que se encuentren en estado `ACTIVO`.
3. **Pedido**: Maneja ciclo de vida `PENDIENTE`, `EN_COCINA`, `PAGADO`. Solo puede ser registrado por meseros en estado `ACTIVO` y en mesas aptas.

### Estructura de Paquetes:
```text
src/main/java/com/roles/usermanagement/
├── domain/service/
│   └── UserRoles.java                                    <-- [Paso 1: Authorities de seguridad]
└── modules/
    └── restaurant/                                       <-- [Módulo restaurante]
        ├── enums/
        │   ├── EstadoMesero.java                         <-- [Paso 2: ACTIVO, NO_ACTIVO]
        │   ├── EstadoMesa.java                           <-- [Paso 2: LIBRE, OCUPADA, SUCIA]
        │   └── EstadoPedido.java                         <-- [Paso 2: PENDIENTE, EN_COCINA, PAGADO]
        ├── entity/
        │   ├── Mesero.java                               <-- [Paso 3: Entidades JPA]
        │   ├── Mesa.java
        │   └── Pedido.java
        ├── repository/
        │   ├── MeseroRepository.java                     <-- [Paso 4: Repositorios Spring Data]
        │   ├── MesaRepository.java
        │   └── PedidoRepository.java
        ├── dto/
        │   ├── MeseroRequest.java                        <-- [Paso 5: DTOs de entrada y salida]
        │   ├── MeseroResponse.java
        │   ├── CambioEstadoMeseroRequest.java
        │   ├── MesaRequest.java
        │   ├── MesaResponse.java
        │   ├── PedidoRequest.java
        │   ├── PedidoResponse.java
        │   └── CambioEstadoPedidoRequest.java
        ├── service/
        │   ├── MeseroService.java                        <-- [Paso 6: Lógica transaccional]
        │   ├── MesaService.java
        │   └── PedidoService.java
        └── controller/
            ├── MeseroController.java                     <-- [Paso 7: Endpoints REST y seguridad]
            ├── MesaController.java
            └── PedidoController.java
```

---

## PASO 1: Registrar Permisos en el Catálogo de Seguridad

### 1.1 Modificar `UserRoles.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/domain/service/UserRoles.java`

Se agrega el permiso `MESERO_CHANGE_STATUS` junto a los permisos del módulo:

```java
// Dentro de public enum Authority en UserRoles.java:
MESERO_READ, MESERO_CREATE, MESERO_UPDATE, MESERO_DELETE, MESERO_CHANGE_STATUS,
MESA_READ, MESA_CREATE, MESA_UPDATE, MESA_DELETE, MESA_CHANGE_STATUS,
PEDIDO_READ, PEDIDO_CREATE, PEDIDO_UPDATE, PEDIDO_CANCEL, PEDIDO_CHANGE_STATUS,
```

---

## PASO 2: Crear Enumeradores (`enums`)

**Paquete:** `com.roles.usermanagement.modules.restaurant.enums`

### 2.1 `EstadoMesero.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/enums/EstadoMesero.java`

```java
package com.roles.usermanagement.modules.restaurant.enums;

public enum EstadoMesero {
    ACTIVO,
    NO_ACTIVO
}
```

### 2.2 `EstadoMesa.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/enums/EstadoMesa.java`

```java
package com.roles.usermanagement.modules.restaurant.enums;

public enum EstadoMesa {
    LIBRE,
    OCUPADA,
    SUCIA
}
```

### 2.3 `EstadoPedido.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/enums/EstadoPedido.java`

```java
package com.roles.usermanagement.modules.restaurant.enums;

public enum EstadoPedido {
    PENDIENTE,
    EN_COCINA,
    PAGADO
}
```

---

## PASO 3: Crear las Entidades JPA (`entity`)

**Paquete:** `com.roles.usermanagement.modules.restaurant.entity`

### 3.1 `Mesero.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/entity/Mesero.java`

```java
package com.roles.usermanagement.modules.restaurant.entity;

import com.roles.usermanagement.modules.restaurant.enums.EstadoMesero;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant_mesero")
@Getter
@Setter
public class Mesero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 30)
    private String documentoIdentidad;

    @Column(length = 30)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMesero estado = EstadoMesero.ACTIVO;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "mesero", fetch = FetchType.LAZY)
    private List<Mesa> mesas = new ArrayList<>();
}
```

### 3.2 `Mesa.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/entity/Mesa.java`

```java
package com.roles.usermanagement.modules.restaurant.entity;

import com.roles.usermanagement.modules.restaurant.enums.EstadoMesa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant_mesa")
@Getter
@Setter
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
```

### 3.3 `Pedido.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/entity/Pedido.java`

```java
package com.roles.usermanagement.modules.restaurant.entity;

import com.roles.usermanagement.modules.restaurant.enums.EstadoPedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurant_pedido")
@Getter
@Setter
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
```

---

## PASO 4: Crear los Repositorios JPA (`repository`)

**Paquete:** `com.roles.usermanagement.modules.restaurant.repository`

### 4.1 `MeseroRepository.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/repository/MeseroRepository.java`

```java
package com.roles.usermanagement.modules.restaurant.repository;

import com.roles.usermanagement.modules.restaurant.entity.Mesero;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MeseroRepository extends JpaRepository<Mesero, Long> {
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
    boolean existsByDocumentoIdentidadAndIdNot(String documentoIdentidad, Long id);
    Page<Mesero> findByActiveTrue(Pageable pageable);
    Page<Mesero> findByEstado(EstadoMesero estado, Pageable pageable);
    List<Mesero> findByEstado(EstadoMesero estado);
}
```

### 4.2 `MesaRepository.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/repository/MesaRepository.java`

```java
package com.roles.usermanagement.modules.restaurant.repository;

import com.roles.usermanagement.modules.restaurant.entity.Mesa;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
    boolean existsByNumeroMesa(Integer numeroMesa);
    boolean existsByNumeroMesaAndIdNot(Integer numeroMesa, Long id);
    List<Mesa> findByEstado(EstadoMesa estado);
    Page<Mesa> findByActiveTrue(Pageable pageable);
    boolean existsByMeseroIdAndEstado(Long meseroId, EstadoMesa estado);
}
```

### 4.3 `PedidoRepository.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/repository/PedidoRepository.java`

```java
package com.roles.usermanagement.modules.restaurant.repository;

import com.roles.usermanagement.modules.restaurant.entity.Pedido;
import com.roles.usermanagement.modules.restaurant.enums.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Page<Pedido> findByMesaId(Long mesaId, Pageable pageable);
    Page<Pedido> findByMeseroId(Long meseroId, Pageable pageable);
    List<Pedido> findByEstado(EstadoPedido estado);
    boolean existsByMeseroIdAndEstadoNot(Long meseroId, EstadoPedido estado);
    boolean existsByMesaIdAndEstadoNot(Long mesaId, EstadoPedido estado);
}
```

---

## PASO 5: Crear los DTOs (`dto`)

**Paquete:** `com.roles.usermanagement.modules.restaurant.dto`

### 5.1 DTOs de Mesero
- **`MeseroRequest.java`**
  **Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/dto/MeseroRequest.java`
```java
package com.roles.usermanagement.modules.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MeseroRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120)
    String nombre,

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 30)
    String documentoIdentidad,

    @Size(max = 30)
    String telefono
) {}
```

- **`CambioEstadoMeseroRequest.java`**
  **Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/dto/CambioEstadoMeseroRequest.java`
```java
package com.roles.usermanagement.modules.restaurant.dto;

import com.roles.usermanagement.modules.restaurant.enums.EstadoMesero;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoMeseroRequest(
    @NotNull(message = "El nuevo estado del mesero es obligatorio")
    EstadoMesero nuevoEstado
) {}
```

- **`MeseroResponse.java`**
  **Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/dto/MeseroResponse.java`
```java
package com.roles.usermanagement.modules.restaurant.dto;

import com.roles.usermanagement.modules.restaurant.enums.EstadoMesero;

public record MeseroResponse(
    Long id,
    String nombre,
    String documentoIdentidad,
    String telefono,
    EstadoMesero estado,
    boolean active
) {}
```

### 5.2 DTOs de Mesa
- **`MesaRequest.java`**
  **Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/dto/MesaRequest.java`
```java
package com.roles.usermanagement.modules.restaurant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MesaRequest(
    @NotNull(message = "El número de mesa es obligatorio")
    @Min(value = 1, message = "El número de mesa debe ser positivo")
    Integer numeroMesa,

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad mínima es de 1 persona")
    Integer capacidad,

    Long meseroId
) {}
```

- **`MesaResponse.java`**
  **Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/dto/MesaResponse.java`
```java
package com.roles.usermanagement.modules.restaurant.dto;

import com.roles.usermanagement.modules.restaurant.enums.EstadoMesa;

public record MesaResponse(
    Long id,
    Integer numeroMesa,
    Integer capacidad,
    EstadoMesa estado,
    boolean active,
    Long meseroId,
    String meseroNombre
) {}
```

### 5.3 DTOs de Pedido
- **`PedidoRequest.java`**
  **Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/dto/PedidoRequest.java`
```java
package com.roles.usermanagement.modules.restaurant.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PedidoRequest(
    @NotNull(message = "El ID de la mesa es obligatorio")
    Long mesaId,

    @NotNull(message = "El ID del mesero es obligatorio")
    Long meseroId,

    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto total debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2)
    BigDecimal total,

    @Size(max = 255)
    String observaciones
) {}
```

- **`CambioEstadoPedidoRequest.java`**
  **Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/dto/CambioEstadoPedidoRequest.java`
```java
package com.roles.usermanagement.modules.restaurant.dto;

import com.roles.usermanagement.modules.restaurant.enums.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoPedidoRequest(
    @NotNull(message = "El nuevo estado es obligatorio")
    EstadoPedido nuevoEstado
) {}
```

- **`PedidoResponse.java`**
  **Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/dto/PedidoResponse.java`
```java
package com.roles.usermanagement.modules.restaurant.dto;

import com.roles.usermanagement.modules.restaurant.enums.EstadoPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoResponse(
    Long id,
    Long mesaId,
    Integer numeroMesa,
    Long meseroId,
    String meseroNombre,
    LocalDateTime fechaCreacion,
    EstadoPedido estado,
    BigDecimal total,
    String observaciones,
    String createdBy
) {}
```

---

## PASO 6: Implementar Servicios de Negocio (`service`)

**Paquete:** `com.roles.usermanagement.modules.restaurant.service`

### 6.1 `MeseroService.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/service/MeseroService.java`

- Valida unicidad de documento.
- Al crear el mesero, asigna `EstadoMesero.ACTIVO` por defecto.
- Bloquea pasar a `NO_ACTIVO` si el mesero tiene mesas `OCUPADAS` o pedidos abiertos (distintos de `PAGADO`).
- Ofrece método explícito `changeStatus` para conmutar entre `ACTIVO` y `NO_ACTIVO`.

```java
package com.roles.usermanagement.modules.restaurant.service;

import com.roles.usermanagement.modules.restaurant.dto.CambioEstadoMeseroRequest;
import com.roles.usermanagement.modules.restaurant.dto.MeseroRequest;
import com.roles.usermanagement.modules.restaurant.dto.MeseroResponse;
import com.roles.usermanagement.modules.restaurant.entity.Mesero;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesa;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesero;
import com.roles.usermanagement.modules.restaurant.enums.EstadoPedido;
import com.roles.usermanagement.modules.restaurant.repository.MesaRepository;
import com.roles.usermanagement.modules.restaurant.repository.MeseroRepository;
import com.roles.usermanagement.modules.restaurant.repository.PedidoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class MeseroService {

    private final MeseroRepository repository;
    private final MesaRepository mesaRepository;
    private final PedidoRepository pedidoRepository;

    public MeseroService(MeseroRepository repository, MesaRepository mesaRepository, PedidoRepository pedidoRepository) {
        this.repository = repository;
        this.mesaRepository = mesaRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public Page<MeseroResponse> all(int page, int size) {
        return repository.findAll(PageRequest.of(page, Math.min(size, 100)))
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<MeseroResponse> byEstado(EstadoMesero estado, int page, int size) {
        return repository.findByEstado(estado, PageRequest.of(page, Math.min(size, 100)))
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public MeseroResponse get(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));
    }

    public MeseroResponse create(MeseroRequest dto) {
        if (repository.existsByDocumentoIdentidad(dto.documentoIdentidad())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un mesero con ese documento");
        }
        Mesero m = new Mesero();
        m.setNombre(dto.nombre());
        m.setDocumentoIdentidad(dto.documentoIdentidad());
        m.setTelefono(dto.telefono());
        m.setEstado(EstadoMesero.ACTIVO);
        return toResponse(repository.save(m));
    }

    public MeseroResponse update(Long id, MeseroRequest dto) {
        Mesero m = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));
        if (!m.isActive() || m.getEstado() == EstadoMesero.NO_ACTIVO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede editar un mesero inactivo o no activo");
        }
        if (repository.existsByDocumentoIdentidadAndIdNot(dto.documentoIdentidad(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El documento ya está en uso por otro mesero");
        }
        m.setNombre(dto.nombre());
        m.setDocumentoIdentidad(dto.documentoIdentidad());
        m.setTelefono(dto.telefono());
        return toResponse(repository.save(m));
    }

    public MeseroResponse changeStatus(Long id, CambioEstadoMeseroRequest request) {
        Mesero m = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));

        EstadoMesero nuevoEstado = request.nuevoEstado();

        if (nuevoEstado == EstadoMesero.NO_ACTIVO) {
            if (mesaRepository.existsByMeseroIdAndEstado(id, EstadoMesa.OCUPADA)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El mesero tiene mesas ocupadas actualmente; reasigne o libere las mesas antes de desactivarlo");
            }
            if (pedidoRepository.existsByMeseroIdAndEstadoNot(id, EstadoPedido.PAGADO)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El mesero tiene pedidos pendientes o en cocina; finalícelos antes de cambiar su estado");
            }
        }

        m.setEstado(nuevoEstado);
        return toResponse(repository.save(m));
    }

    public void deactivate(Long id) {
        Mesero m = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));

        if (mesaRepository.existsByMeseroIdAndEstado(id, EstadoMesa.OCUPADA)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede desactivar un mesero con mesas ocupadas");
        }

        m.setActive(false);
        m.setEstado(EstadoMesero.NO_ACTIVO);
        repository.save(m);
    }

    public MeseroResponse toResponse(Mesero m) {
        return new MeseroResponse(m.getId(), m.getNombre(), m.getDocumentoIdentidad(), m.getTelefono(), m.getEstado(), m.isActive());
    }
}
```

### 6.2 `MesaService.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/service/MesaService.java`

- Verifica que si se asigna un mesero a la mesa, su estado sea estrictamente `EstadoMesero.ACTIVO`.

```java
package com.roles.usermanagement.modules.restaurant.service;

import com.roles.usermanagement.modules.restaurant.dto.MesaRequest;
import com.roles.usermanagement.modules.restaurant.dto.MesaResponse;
import com.roles.usermanagement.modules.restaurant.entity.Mesa;
import com.roles.usermanagement.modules.restaurant.entity.Mesero;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesa;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesero;
import com.roles.usermanagement.modules.restaurant.repository.MesaRepository;
import com.roles.usermanagement.modules.restaurant.repository.MeseroRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class MesaService {

    private final MesaRepository mesaRepository;
    private final MeseroRepository meseroRepository;

    public MesaService(MesaRepository mesaRepository, MeseroRepository meseroRepository) {
        this.mesaRepository = mesaRepository;
        this.meseroRepository = meseroRepository;
    }

    @Transactional(readOnly = true)
    public Page<MesaResponse> all(int page, int size) {
        return mesaRepository.findAll(PageRequest.of(page, Math.min(size, 100)))
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public MesaResponse get(Long id) {
        return mesaRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
    }

    public MesaResponse create(MesaRequest dto) {
        if (mesaRepository.existsByNumeroMesa(dto.numeroMesa())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El número de mesa ya existe");
        }
        Mesa mesa = new Mesa();
        mesa.setNumeroMesa(dto.numeroMesa());
        mesa.setCapacidad(dto.capacidad());
        mesa.setEstado(EstadoMesa.LIBRE);

        if (dto.meseroId() != null) {
            Mesero mesero = meseroRepository.findById(dto.meseroId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));
            if (!mesero.isActive() || mesero.getEstado() != EstadoMesero.ACTIVO) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El mesero asignado no está activo");
            }
            mesa.setMesero(mesero);
        }

        return toResponse(mesaRepository.save(mesa));
    }

    public MesaResponse update(Long id, MesaRequest dto) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
        if (!mesa.isActive()) throw new ResponseStatusException(HttpStatus.CONFLICT, "La mesa está inactiva");

        if (mesaRepository.existsByNumeroMesaAndIdNot(dto.numeroMesa(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El número de mesa ya pertenece a otra mesa");
        }

        mesa.setNumeroMesa(dto.numeroMesa());
        mesa.setCapacidad(dto.capacidad());

        if (dto.meseroId() != null) {
            Mesero mesero = meseroRepository.findById(dto.meseroId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));
            if (!mesero.isActive() || mesero.getEstado() != EstadoMesero.ACTIVO) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El mesero asignado no está activo");
            }
            mesa.setMesero(mesero);
        } else {
            mesa.setMesero(null);
        }

        return toResponse(mesaRepository.save(mesa));
    }

    public MesaResponse changeStatus(Long id, EstadoMesa nuevoEstado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
        mesa.setEstado(nuevoEstado);
        return toResponse(mesaRepository.save(mesa));
    }

    public void deactivate(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
        mesa.setActive(false);
        mesaRepository.save(mesa);
    }

    public MesaResponse toResponse(Mesa m) {
        return new MesaResponse(
                m.getId(),
                m.getNumeroMesa(),
                m.getCapacidad(),
                m.getEstado(),
                m.isActive(),
                m.getMesero() != null ? m.getMesero().getId() : null,
                m.getMesero() != null ? m.getMesero().getNombre() : null
        );
    }
}
```

### 6.3 `PedidoService.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/service/PedidoService.java`

- Valida que el mesero asignado al pedido esté en `EstadoMesero.ACTIVO`.
- Maneja la transición de la mesa a `OCUPADA` al crearse y a `SUCIA` al marcar `PAGADO`.

```java
package com.roles.usermanagement.modules.restaurant.service;

import com.roles.usermanagement.modules.restaurant.dto.CambioEstadoPedidoRequest;
import com.roles.usermanagement.modules.restaurant.dto.PedidoRequest;
import com.roles.usermanagement.modules.restaurant.dto.PedidoResponse;
import com.roles.usermanagement.modules.restaurant.entity.Mesa;
import com.roles.usermanagement.modules.restaurant.entity.Mesero;
import com.roles.usermanagement.modules.restaurant.entity.Pedido;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesa;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesero;
import com.roles.usermanagement.modules.restaurant.enums.EstadoPedido;
import com.roles.usermanagement.modules.restaurant.repository.MesaRepository;
import com.roles.usermanagement.modules.restaurant.repository.MeseroRepository;
import com.roles.usermanagement.modules.restaurant.repository.PedidoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final MeseroRepository meseroRepository;

    public PedidoService(PedidoRepository pedidoRepository, MesaRepository mesaRepository, MeseroRepository meseroRepository) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.meseroRepository = meseroRepository;
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> all(int page, int size) {
        return pedidoRepository.findAll(PageRequest.of(page, Math.min(size, 100)))
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PedidoResponse get(Long id) {
        return pedidoRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    public PedidoResponse create(PedidoRequest dto) {
        Mesa mesa = mesaRepository.findById(dto.mesaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
        if (!mesa.isActive()) throw new ResponseStatusException(HttpStatus.CONFLICT, "La mesa está inactiva");
        if (mesa.getEstado() == EstadoMesa.SUCIA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La mesa está sucia; debe limpiarse antes de usarla");
        }

        Mesero mesero = meseroRepository.findById(dto.meseroId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));
        
        // Validación del estado de mesero
        if (!mesero.isActive() || mesero.getEstado() != EstadoMesero.ACTIVO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El mesero seleccionado se encuentra NO ACTIVO");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = (auth != null && auth.getName() != null) ? auth.getName() : "system";

        Pedido pedido = new Pedido();
        pedido.setMesa(mesa);
        pedido.setMesero(mesero);
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setTotal(dto.total());
        pedido.setObservaciones(dto.observaciones());
        pedido.setCreatedBy(currentUsername);

        mesa.setEstado(EstadoMesa.OCUPADA);
        mesaRepository.save(mesa);

        return toResponse(pedidoRepository.save(pedido));
    }

    public PedidoResponse changeStatus(Long id, CambioEstadoPedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));

        EstadoPedido nuevo = request.nuevoEstado();

        if (pedido.getEstado() == EstadoPedido.PAGADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede cambiar el estado de un pedido ya PAGADO");
        }

        pedido.setEstado(nuevo);

        if (nuevo == EstadoPedido.PAGADO) {
            Mesa mesa = pedido.getMesa();
            mesa.setEstado(EstadoMesa.SUCIA);
            mesaRepository.save(mesa);
        }

        return toResponse(pedidoRepository.save(pedido));
    }

    public PedidoResponse toResponse(Pedido p) {
        return new PedidoResponse(
                p.getId(),
                p.getMesa().getId(),
                p.getMesa().getNumeroMesa(),
                p.getMesero().getId(),
                p.getMesero().getNombre(),
                p.getFechaCreacion(),
                p.getEstado(),
                p.getTotal(),
                p.getObservaciones(),
                p.getCreatedBy()
        );
    }
}
```

---

## PASO 7: Implementar los Controladores REST (`controller`)

**Paquete:** `com.roles.usermanagement.modules.restaurant.controller`

### 7.1 `MeseroController.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/controller/MeseroController.java`

- Expone `PATCH /api/meseros/{id}/estado` para alternar entre `ACTIVO` y `NO_ACTIVO`.
- Permite listar por estado específico con `GET /api/meseros?estado=ACTIVO`.

```java
package com.roles.usermanagement.modules.restaurant.controller;

import com.roles.usermanagement.modules.restaurant.dto.CambioEstadoMeseroRequest;
import com.roles.usermanagement.modules.restaurant.dto.MeseroRequest;
import com.roles.usermanagement.modules.restaurant.dto.MeseroResponse;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesero;
import com.roles.usermanagement.modules.restaurant.service.MeseroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meseros")
@Tag(name = "Meseros", description = "Gestión de personal de atención de restaurante y estados de actividad")
@SecurityRequirement(name = "bearerAuth")
public class MeseroController {

    private final MeseroService service;

    public MeseroController(MeseroService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MESERO_READ')")
    @Operation(summary = "Listar meseros paginados (con filtro opcional por estado)")
    public Page<MeseroResponse> all(
            @RequestParam(required = false) EstadoMesero estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (estado != null) {
            return service.byEstado(estado, page, size);
        }
        return service.all(page, size);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MESERO_READ')")
    @Operation(summary = "Consultar mesero por ID")
    public MeseroResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MESERO_CREATE')")
    @Operation(summary = "Registrar nuevo mesero (inicia en estado ACTIVO)")
    public ResponseEntity<MeseroResponse> create(@Valid @RequestBody MeseroRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MESERO_UPDATE')")
    @Operation(summary = "Actualizar datos de mesero")
    public MeseroResponse update(@PathVariable Long id, @Valid @RequestBody MeseroRequest dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('MESERO_CHANGE_STATUS')")
    @Operation(summary = "Cambiar estado del mesero (ACTIVO, NO_ACTIVO)")
    public MeseroResponse changeStatus(@PathVariable Long id, @Valid @RequestBody CambioEstadoMeseroRequest request) {
        return service.changeStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MESERO_DELETE')")
    @Operation(summary = "Desactivar mesero (borrado lógico)")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 7.2 `MesaController.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/controller/MesaController.java`

```java
package com.roles.usermanagement.modules.restaurant.controller;

import com.roles.usermanagement.modules.restaurant.dto.MesaRequest;
import com.roles.usermanagement.modules.restaurant.dto.MesaResponse;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesa;
import com.roles.usermanagement.modules.restaurant.service.MesaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mesas")
@Tag(name = "Mesas", description = "Gestión de mesas y asignación de meseros")
@SecurityRequirement(name = "bearerAuth")
public class MesaController {

    private final MesaService service;

    public MesaController(MesaService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MESA_READ')")
    @Operation(summary = "Listar mesas paginadas")
    public Page<MesaResponse> all(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.all(page, size);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MESA_READ')")
    @Operation(summary = "Consultar mesa por ID")
    public MesaResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MESA_CREATE')")
    @Operation(summary = "Crear nueva mesa (valida que el mesero esté ACTIVO)")
    public ResponseEntity<MesaResponse> create(@Valid @RequestBody MesaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MESA_UPDATE')")
    @Operation(summary = "Actualizar mesa o reasignar mesero")
    public MesaResponse update(@PathVariable Long id, @Valid @RequestBody MesaRequest dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('MESA_CHANGE_STATUS')")
    @Operation(summary = "Cambiar estado de la mesa (LIBRE, OCUPADA, SUCIA)")
    public MesaResponse changeStatus(@PathVariable Long id, @RequestParam EstadoMesa estado) {
        return service.changeStatus(id, estado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MESA_DELETE')")
    @Operation(summary = "Desactivar mesa")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 7.3 `PedidoController.java`
**Ubicación:** `src/main/java/com/roles/usermanagement/modules/restaurant/controller/PedidoController.java`

```java
package com.roles.usermanagement.modules.restaurant.controller;

import com.roles.usermanagement.modules.restaurant.dto.CambioEstadoPedidoRequest;
import com.roles.usermanagement.modules.restaurant.dto.PedidoRequest;
import com.roles.usermanagement.modules.restaurant.dto.PedidoResponse;
import com.roles.usermanagement.modules.restaurant.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Gestión y ciclo de vida de pedidos en restaurante")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PEDIDO_READ')")
    @Operation(summary = "Listar pedidos paginados")
    public Page<PedidoResponse> all(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.all(page, size);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PEDIDO_READ')")
    @Operation(summary = "Consultar pedido por ID")
    public PedidoResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PEDIDO_CREATE')")
    @Operation(summary = "Crear nuevo pedido (valida que el mesero esté ACTIVO y mesa disponible)")
    public ResponseEntity<PedidoResponse> create(@Valid @RequestBody PedidoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('PEDIDO_CHANGE_STATUS')")
    @Operation(summary = "Actualizar estado del pedido (PENDIENTE, EN_COCINA, PAGADO)")
    public PedidoResponse changeStatus(@PathVariable Long id, @Valid @RequestBody CambioEstadoPedidoRequest request) {
        return service.changeStatus(id, request);
    }
}
```

---

## PASO 8: Ejecución y Pruebas del Flujo Completo

### 8.1 Compilar y Ejecutar
En la terminal PowerShell en la raíz del proyecto:
```powershell
.\gradlew.bat bootRun
```
Hibernate actualizará la base de datos con las columnas y tablas correspondientes.

### 8.2 Autenticación y Swagger UI
1. Entrar a [http://localhost:8050/swagger-ui.html](http://localhost:8050/swagger-ui.html).
2. Autenticarse en `POST /api/auth/login` con credenciales de administrador para obtener el JWT token.
3. Hacer clic en **Authorize** e ingresar `Bearer <tu_token>`.

### 8.3 Flujo de Pruebas de Negocio con Estados de Mesero

1. **Crear Mesero (Nace en estado `ACTIVO`):**
   - `POST /api/meseros`
   ```json
   {
     "nombre": "Carlos Mendoza",
     "documentoIdentidad": "1098765432",
     "telefono": "3109876543"
   }
   ```
   *(Respuesta: `201 CREATED`, `id: 1`, `estado: "ACTIVO"`)*

2. **Crear Mesa asignada al Mesero ACTIVO:**
   - `POST /api/mesas`
   ```json
   {
     "numeroMesa": 5,
     "capacidad": 4,
     "meseroId": 1
   }
   ```
   *(Respuesta: `201 CREATED`, estado de la mesa: `LIBRE`)*

3. **Crear Pedido con Mesero ACTIVO:**
   - `POST /api/pedidos`
   ```json
   {
     "mesaId": 1,
     "meseroId": 1,
     "total": 85000.00,
     "observaciones": "Sin picante"
   }
   ```
   *(Respuesta: `201 CREATED`, estado del pedido: `PENDIENTE`. La mesa pasa a `OCUPADA`)*

4. **Validación de Bloqueo al intentar desactivar Mesero con pedidos o mesas ocupadas:**
   - `PATCH /api/meseros/1/estado`
   ```json
   {
     "nuevoEstado": "NO_ACTIVO"
   }
   ```
   *(Respuesta: `409 CONFLICT` indicando que el mesero tiene mesas ocupadas o pedidos pendientes)*

5. **Completar Pedido y Limpiar Mesa:**
   - Avanzar a cocina: `PATCH /api/pedidos/1/estado` con `{"nuevoEstado": "EN_COCINA"}`
   - Pagar: `PATCH /api/pedidos/1/estado` con `{"nuevoEstado": "PAGADO"}` (mesa pasa a `SUCIA`)
   - Limpiar mesa: `PATCH /api/mesas/1/estado?estado=LIBRE`

6. **Desactivar Mesero con éxito:**
   - `PATCH /api/meseros/1/estado`
   ```json
   {
     "nuevoEstado": "NO_ACTIVO"
   }
   ```
   *(Respuesta: `200 OK`, con `estado: "NO_ACTIVO"`)*

7. **Validar que un Mesero `NO_ACTIVO` no puede tomar nuevos pedidos:**
   - Intentar `POST /api/pedidos` usando `meseroId: 1`:
   *(Respuesta: `409 CONFLICT: "El mesero seleccionado se encuentra NO ACTIVO"`)*

---

## 9. Extensibilidad Futura

- **Reasignación Automática de Mesas:** Posibilidad de crear un servicio para reasignar mesas de un mesero que pasa a `NO_ACTIVO` hacia otro que esté `ACTIVO`.
- **Módulo de Turnos y Horarios:** Vinculación del estado `ACTIVO`/`NO_ACTIVO` con la marcación de entrada y salida de turnos de meseros.
- **Detalle de Ítems y Platos:** Agregar entidades `Plato` y `DetallePedido` manteniendo las capas desacopladas.
