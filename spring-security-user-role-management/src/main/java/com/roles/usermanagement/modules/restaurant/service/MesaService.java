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

