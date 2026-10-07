package com.roles.usermanagement.modules.restaurant.repository;

import com.roles.usermanagement.modules.restaurant.entity.Mesa;
import com.roles.usermanagement.modules.restaurant.entity.Mesero;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesa;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MesaRepository extends JpaRepository<Mesa, Long>{

    boolean existsByNumeroMesa(Integer numeroMesa);
    boolean existsByNumeroMesaAndIdNot(Integer numeroMesa, Long id);
    List<Mesa> findByEstado(EstadoMesa estado);
    Page<Mesa> findByActiveTrue(Pageable pageable);
    boolean existsByMeseroIdAndEstado(Long meseroId, EstadoMesa estado);

}
