package com.ufc.almoxarifado.service;

import com.ufc.almoxarifado.dto.HistoricoBloqueiosRequest;
import com.ufc.almoxarifado.dto.HistoricoBloqueiosResponse;
import com.ufc.almoxarifado.entity.HistoricoBloqueios;
import com.ufc.almoxarifado.entity.Usuario;
import com.ufc.almoxarifado.exception.ResourceNotFoundException;
import com.ufc.almoxarifado.repository.HistoricoBloqueiosRepository;
import com.ufc.almoxarifado.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoricoBloqueiosService {

    private final HistoricoBloqueiosRepository historicoBloqueiosRepository;
    private final UsuarioRepository usuarioRepository;

    public HistoricoBloqueiosResponse create(HistoricoBloqueiosRequest request) {
        HistoricoBloqueios entity = new HistoricoBloqueios();
        applyRequest(entity, request);
        return toResponse(historicoBloqueiosRepository.save(entity));
    }

    public List<HistoricoBloqueiosResponse> findAll() {
        return historicoBloqueiosRepository.findAll().stream().map(this::toResponse).toList();
    }

    public HistoricoBloqueiosResponse findById(UUID id) {
        return toResponse(getEntity(id));
    }

    public HistoricoBloqueiosResponse update(UUID id, HistoricoBloqueiosRequest request) {
        HistoricoBloqueios entity = getEntity(id);
        applyRequest(entity, request);
        return toResponse(historicoBloqueiosRepository.save(entity));
    }

    public void delete(UUID id) {
        if (!historicoBloqueiosRepository.existsById(id)) {
            throw new ResourceNotFoundException("HistoricoBloqueios nao encontrado para id: " + id);
        }
        historicoBloqueiosRepository.deleteById(id);
    }

    private HistoricoBloqueios getEntity(UUID id) {
        return historicoBloqueiosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Histórico de bloqueios nao encontrado para id: " + id));
    }

    private void applyRequest(HistoricoBloqueios entity, HistoricoBloqueiosRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado para id: " + request.usuarioId()));

        Usuario administradorBloqueio = usuarioRepository.findById(request.administradorBloqueioId())
                .orElseThrow(() -> new ResourceNotFoundException("Administrador de bloqueio nao encontrado para id: " + request.administradorBloqueioId()));

        Usuario administradorDesbloqueio = null;
        if (request.administradorDesbloqueioId() != null) {
            administradorDesbloqueio = usuarioRepository.findById(request.administradorDesbloqueioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Administrador de desbloqueio nao encontrado para id: " + request.administradorDesbloqueioId()));
        }

        entity.setUsuario(usuario);
        entity.setAdministradorBloqueio(administradorBloqueio);
        entity.setAdministradorDesbloqueio(administradorDesbloqueio);
        entity.setMotivoDesbloqueio(request.motivoDesbloqueio());
        entity.setMotivoBloqueio(request.motivoBloqueio());
        if (entity.getDataBloqueio() == null) {
            entity.setDataBloqueio(request.dataBloqueio() != null ? request.dataBloqueio() : LocalDateTime.now());
        }
        entity.setDataDesbloqueio(request.dataDesbloqueio());
    }

    private HistoricoBloqueiosResponse toResponse(HistoricoBloqueios entity) {
        return new HistoricoBloqueiosResponse(
                entity.getId(),
                entity.getUsuario() != null ? entity.getUsuario().getId() : null,
                entity.getAdministradorBloqueio() != null ? entity.getAdministradorBloqueio().getId() : null,
                entity.getAdministradorDesbloqueio() != null ? entity.getAdministradorDesbloqueio().getId() : null,
                entity.getMotivoDesbloqueio(),
                entity.getMotivoBloqueio(),
                entity.getDataBloqueio(),
                entity.getDataDesbloqueio()
        );
    }
}

