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
    public Page all(int page, int size) {
        return repository.findAll(PageRequest.of(page, Math.min(size, 100)))
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page byEstado(EstadoMesero estado, int page, int size) {
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
