package com.roles.usermanagement.modules.restaurant.repository;

import com.roles.usermanagement.modules.restaurant.entity.Mesero;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// el pageable En lugar de traer miles de registros a la memoria (lo que saturaría el servidor y ralentizaría la red), Pageable le indica a la base de datos exactamente qué porción de datos solicitar.
//Spring Data JPA traduce internamente la llamada a sentencias SQL utilizando cláusulas de limitación (según el motor de base de datos, típicamente LIMIT y OFFSET):
//
//Si solicitas la página 1 (la segunda página) con un tamaño de 10 registros, ordenados por nombre:

public interface MeseroRepository extends JpaRepository<Mesero,Long>{
    boolean existsByDocumentoIdentidad(String documentoIdentidad); // Verifica si ya existe un mesero registrado con ese documento.
    boolean existsByDocumentoIdentidadAndIdNot(String documentoIdentidad, Long id); //Comprueba si existe otro registro con el mismo documento cuyo identificador no sea el actual
    Page<Mesero> findByActiveTrue(Pageable pageable);
    Page<Mesero> findByEstado(EstadoMesero estado, Pageable pageable);
    List<Mesero> findByEstado(EstadoMesero estado);
}
