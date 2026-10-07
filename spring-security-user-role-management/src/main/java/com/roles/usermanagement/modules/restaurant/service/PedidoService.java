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