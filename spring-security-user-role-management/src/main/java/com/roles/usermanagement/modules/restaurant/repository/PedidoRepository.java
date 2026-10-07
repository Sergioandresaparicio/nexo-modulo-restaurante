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
