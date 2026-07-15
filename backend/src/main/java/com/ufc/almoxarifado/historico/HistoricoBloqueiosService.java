package com.ufc.almoxarifado.historico;

import com.ufc.almoxarifado.exception.ResourceNotFoundException;
import com.ufc.almoxarifado.historico.internal.HistoricoBloqueios;
import com.ufc.almoxarifado.historico.internal.HistoricoBloqueiosRepository;
import com.ufc.almoxarifado.usuario.Usuario;
import com.ufc.almoxarifado.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoricoBloqueiosService {

    private final HistoricoBloqueiosRepository historicoBloqueiosRepository;
    private final UsuarioService usuarioService;

    public HistoricoBloqueiosResponse create(HistoricoBloqueiosRequest request, Long adminId) {
        HistoricoBloqueios entity = new HistoricoBloqueios();
        applyCreationRequest(entity, request, adminId);
        return toResponse(historicoBloqueiosRepository.save(entity));
    }

    public List<HistoricoBloqueiosResponse> findAll() {
        return historicoBloqueiosRepository.findAll().stream().map(this::toResponse).toList();
    }

    public HistoricoBloqueiosResponse findById(UUID id) {
        return toResponse(getEntity(id));
    }

    public HistoricoBloqueiosResponse update(UUID id, HistoricoBloqueiosRequest request, Long adminId) {
        HistoricoBloqueios entity = getEntity(id);
        applyUpdateRequest(entity, request, adminId);
        return toResponse(historicoBloqueiosRepository.save(entity));
    }

    public void delete(UUID id) {
        if (!historicoBloqueiosRepository.existsById(id)) {
            throw new ResourceNotFoundException("HistoricoBloqueios não encontrado para id: " + id);
        }
        historicoBloqueiosRepository.deleteById(id);
    }

    private HistoricoBloqueios getEntity(UUID id) {
        return historicoBloqueiosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Histórico de bloqueios não encontrado para id: " + id));
    }

    private void applyCreationRequest(HistoricoBloqueios entity, HistoricoBloqueiosRequest request, Long adminId) {
        Usuario usuario = usuarioService.getEntity(request.usuarioId());

        Usuario administradorBloqueio = usuarioService.getEntity(adminId);

        entity.setUsuario(usuario);
        entity.setAdministradorBloqueio(administradorBloqueio);
        entity.setMotivoBloqueio(request.motivoBloqueio());
        entity.setDataBloqueio(request.dataBloqueio() != null ? request.dataBloqueio() : LocalDateTime.now());

        if (request.motivoDesbloqueio() != null && !request.motivoDesbloqueio().isBlank()) {
            entity.setMotivoDesbloqueio(request.motivoDesbloqueio());
            entity.setAdministradorDesbloqueio(administradorBloqueio);
            entity.setDataDesbloqueio(request.dataDesbloqueio() != null ? request.dataDesbloqueio() : LocalDateTime.now());
        }
    }

    private void applyUpdateRequest(HistoricoBloqueios entity, HistoricoBloqueiosRequest request, Long adminId) {
        Usuario usuario = usuarioService.getEntity(request.usuarioId());

        entity.setUsuario(usuario);
        entity.setMotivoBloqueio(request.motivoBloqueio());
        if (request.dataBloqueio() != null) {
            entity.setDataBloqueio(request.dataBloqueio());
        }

        if (request.motivoDesbloqueio() != null && !request.motivoDesbloqueio().isBlank()) {
            entity.setMotivoDesbloqueio(request.motivoDesbloqueio());
            if (entity.getAdministradorDesbloqueio() == null) {
                Usuario administradorDesbloqueio = usuarioService.getEntity(adminId);
                entity.setAdministradorDesbloqueio(administradorDesbloqueio);
            }
            if (entity.getDataDesbloqueio() == null) {
                entity.setDataDesbloqueio(request.dataDesbloqueio() != null ? request.dataDesbloqueio() : LocalDateTime.now());
            } else if (request.dataDesbloqueio() != null) {
                entity.setDataDesbloqueio(request.dataDesbloqueio());
            }
        }
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
