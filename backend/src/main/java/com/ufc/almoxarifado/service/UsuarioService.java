package com.ufc.almoxarifado.service;

import com.ufc.almoxarifado.dto.request.UsuarioRequest;
import com.ufc.almoxarifado.dto.response.UsuarioResponse;
import com.ufc.almoxarifado.mapper.UsuarioMapper;
import com.ufc.almoxarifado.model.Papel;
import com.ufc.almoxarifado.model.Usuario;

import com.ufc.almoxarifado.exception.DuplicateFieldException;
import com.ufc.almoxarifado.exception.ResourceNotFoundException;
import com.ufc.almoxarifado.exception.SelfActionException;
import com.ufc.almoxarifado.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AuthSecurityService authSecurityService;
    private final KeycloakUsuarioRegistrationService usuarioManagementService;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public UsuarioResponse create(UsuarioRequest request) {
        validateUniqueFields(request.username(), request.email(), request.cpf(), null);
        usuarioManagementService.validateUsernameNotExists(usuarioMapper.normalizeUsername(request.username()));
        usuarioManagementService.validateEmailNotExists(usuarioMapper.normalizeEmail(request.email()));
        usuarioManagementService.validateCpfNotExists(usuarioMapper.normalizeCpf(request.cpf()));

        Usuario entity = new Usuario();
        usuarioMapper.applyRequest(entity, request);
        Usuario saved = usuarioRepository.save(entity);
        usuarioManagementService.register(saved);
        return usuarioMapper.toResponse(saved);
    }

    @Transactional
    public UsuarioResponse update(Long id, UsuarioRequest request) {
        Usuario entity = getEntity(id);
        String previousEmail = entity.getEmail();
        validateUniqueFields(request.username(), request.email(), request.cpf(), id);
        validateKeycloakChanges(entity, request);
        usuarioMapper.applyRequest(entity, request);
        Usuario updated = usuarioRepository.save(entity);
        usuarioManagementService.syncUser(previousEmail, updated);
        return usuarioMapper.toResponse(updated);
    }

    @Transactional
    public UsuarioResponse updateStatus(Long id, Boolean ativo) {
        if (ativo == null) {
            throw new IllegalArgumentException("Status é obrigatório");
        }
        return updateStatus(id, ativo.booleanValue());
    }

    private UsuarioResponse updateStatus(Long id, boolean ativo) {
        Usuario entity = getEntity(id);
        Usuario usuarioLogado = authSecurityService.getUsuarioLogado();
        if (!ativo && isAuthenticatedUser(usuarioLogado, entity)) {
            throw new SelfActionException("Não é permitido desativar o usuário logado");
        }

        entity.setStatus(ativo);
        Usuario updated = usuarioRepository.save(entity);
        usuarioManagementService.syncStatus(updated);
        return usuarioMapper.toResponse(updated);
    }

    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream().map(usuarioMapper::toResponse).toList();
    }

    public Page<UsuarioResponse> list(String search, String papel, Boolean ativo, Pageable pageable) {
        Papel papelFilter = parsePapel(papel);
        Specification<Usuario> spec = (_, _, cb) -> cb.conjunction();

        if (search != null && !search.isBlank()) {
            String value = "%" + search.trim().toLowerCase() + "%";
            spec = spec.and((root, _, cb) -> cb.or(
                    cb.like(cb.lower(root.get("nome")), value),
                    cb.like(cb.lower(root.get("username")), value),
                    cb.like(cb.lower(root.get("email")), value),
                    cb.like(cb.lower(root.get("cpf")), value)));
        }

        if (papelFilter != null) {
            spec = spec.and((root, _, cb) -> cb.equal(root.get("papel"), papelFilter));
        }

        if (ativo != null) {
            spec = spec.and((root, _, cb) -> cb.equal(root.get("status"), ativo));
        }

        Pageable effectivePageable = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "nome"));

        return usuarioRepository.findAll(spec, effectivePageable).map(usuarioMapper::toResponse);
    }

    public UsuarioResponse findById(Long id) {
        return usuarioMapper.toResponse(getEntity(id));
    }

    public UsuarioResponse findByMatricula(String matricula) {
        Usuario entity = usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para matricula: " + matricula));
        return usuarioMapper.toResponse(entity);
    }

    public UsuarioResponse findByEmail(String email) {
        Usuario entity = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para email: " + email));
        return usuarioMapper.toResponse(entity);
    }

    public void delete(Long id) {
        Usuario entity = getEntity(id);
        Usuario usuarioLogado = authSecurityService.getUsuarioLogado();
        if (isAuthenticatedUser(usuarioLogado, entity)) {
            throw new SelfActionException("Não é permitido excluir o usuário logado");
        }

        usuarioManagementService.revokeClientRoles(entity);
        usuarioRepository.delete(entity);
    }

    public Usuario getEntity(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para id: " + id));
    }

    private Papel parsePapel(String papel) {
        try {
            return Papel.fromValue(papel);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    private boolean isAuthenticatedUser(Usuario usuarioLogado, Usuario target) {
        return usuarioLogado != null && target != null && Objects.equals(usuarioLogado.getId(), target.getId());
    }

    private void validateUniqueFields(String username, String email, String cpf, Long id) {
        String normalizedUsername = usuarioMapper.normalizeUsername(username);
        boolean usernameExists = id == null
                ? usuarioRepository.existsByUsernameIgnoreCase(normalizedUsername)
                : usuarioRepository.existsByUsernameIgnoreCaseAndIdNot(normalizedUsername, id);
        if (usernameExists) {
            throw new DuplicateFieldException("Username já cadastrado");
        }

        String normalizedEmail = usuarioMapper.normalizeEmail(email);
        boolean emailExists = id == null
                ? usuarioRepository.existsByEmailIgnoreCase(normalizedEmail)
                : usuarioRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, id);
        if (emailExists) {
            throw new DuplicateFieldException("Email já cadastrado");
        }

        String normalizedCpf = usuarioMapper.normalizeCpf(cpf);
        boolean cpfExists = id == null
                ? usuarioRepository.existsByCpf(normalizedCpf)
                : usuarioRepository.existsByCpfAndIdNot(normalizedCpf, id);
        if (cpfExists) {
            throw new DuplicateFieldException("CPF já cadastrado");
        }
    }

    private void validateKeycloakChanges(Usuario current, UsuarioRequest request) {
        String normalizedUsername = usuarioMapper.normalizeUsername(request.username());
        if (normalizedUsername != null && !normalizedUsername.equalsIgnoreCase(current.getUsername())) {
            usuarioManagementService.validateUsernameNotExists(normalizedUsername);
        }

        String normalizedEmail = usuarioMapper.normalizeEmail(request.email());
        if (normalizedEmail != null && !normalizedEmail.equalsIgnoreCase(current.getEmail())) {
            usuarioManagementService.validateEmailNotExists(normalizedEmail);
        }

        if (request.cpf() != null) {
            String normalizedCpf = usuarioMapper.normalizeCpf(request.cpf());
            if (!Objects.equals(normalizedCpf, current.getCpf())) {
                usuarioManagementService.validateCpfNotExists(normalizedCpf);
            }
        }
    }
}
