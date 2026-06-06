package com.ufc.almoxarifado.service;

import com.ufc.almoxarifado.dto.UsuarioRequest;
import com.ufc.almoxarifado.dto.UsuarioResponse;
import com.ufc.almoxarifado.entity.Papel;
import com.ufc.almoxarifado.entity.Usuario;
import com.ufc.almoxarifado.exception.ResourceNotFoundException;
import com.ufc.almoxarifado.repository.UsuarioRepository;
import com.ufc.almoxarifado.security.KeycloakUsuarioRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AuthSecurityService authSecurityService;
    private final KeycloakUsuarioRegistrationService usuarioManagementService;

    @Transactional
    public UsuarioResponse create(UsuarioRequest request) {
        validateUniqueFields(request.username(), request.email(), request.cpf(), null);
        usuarioManagementService.validateUsernameNotExists(normalizeUsername(request.username()));
        usuarioManagementService.validateEmailNotExists(normalizeEmail(request.email()));
        usuarioManagementService.validateCpfNotExists(normalizeCpf(request.cpf()));

        Usuario entity = new Usuario();
        applyRequest(entity, request);
        Usuario saved = usuarioRepository.save(entity);
        usuarioManagementService.register(saved);
        return toResponse(saved);
    }

    @Transactional
    public UsuarioResponse update(Long id, UsuarioRequest request) {
        Usuario entity = getEntity(id);
        String previousEmail = entity.getEmail();
        validateUniqueFields(request.username(), request.email(), request.cpf(), id);
        validateKeycloakChanges(entity, request);
        applyRequest(entity, request);
        Usuario updated = usuarioRepository.save(entity);
        usuarioManagementService.syncUser(previousEmail, updated);
        return toResponse(updated);
    }

    @Transactional
    public UsuarioResponse updateStatus(Long id, Boolean ativo) {
        if (ativo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status é obrigatório");
        }
        return updateStatus(id, ativo.booleanValue());
    }

    private UsuarioResponse updateStatus(Long id, boolean ativo) {
        Usuario entity = getEntity(id);
        Usuario usuarioLogado = authSecurityService.getUsuarioLogado();
        if (!ativo && isAuthenticatedUser(usuarioLogado, entity.getEmail(), entity.getCpf())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é permitido desativar o usuário logado");
        }

        entity.setStatus(ativo);
        Usuario updated = usuarioRepository.save(entity);
        usuarioManagementService.syncStatus(updated);
        return toResponse(updated);
    }

    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
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

        return usuarioRepository.findAll(spec, effectivePageable).map(this::toResponse);
    }

    public UsuarioResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    public UsuarioResponse findByMatricula(String matricula) {
        Usuario entity = usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para matricula: " + matricula));
        return toResponse(entity);
    }

    public UsuarioResponse findByEmail(String email) {
        Usuario entity = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para email: " + email));
        return toResponse(entity);
    }

    public void delete(Long id) {
        Usuario entity = getEntity(id);
        Usuario usuarioLogado = authSecurityService.getUsuarioLogado();
        if (isAuthenticatedUser(usuarioLogado, entity.getEmail(), entity.getCpf())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é permitido excluir o usuário logado");
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    private boolean isAuthenticatedUser(Usuario usuario, String authenticatedEmail, String authenticatedUsername) {
        return (authenticatedEmail != null && authenticatedEmail.equalsIgnoreCase(usuario.getEmail()))
                || (authenticatedUsername != null && authenticatedUsername.equalsIgnoreCase(usuario.getUsername()));
    }

    private void validateUniqueFields(String username, String email, String cpf, Long id) {
        String normalizedUsername = normalizeUsername(username);
        boolean usernameExists = id == null
                ? usuarioRepository.existsByUsernameIgnoreCase(normalizedUsername)
                : usuarioRepository.existsByUsernameIgnoreCaseAndIdNot(normalizedUsername, id);
        if (usernameExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username já cadastrado");
        }

        String normalizedEmail = normalizeEmail(email);
        boolean emailExists = id == null
                ? usuarioRepository.existsByEmailIgnoreCase(normalizedEmail)
                : usuarioRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, id);
        if (emailExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        String normalizedCpf = normalizeCpf(cpf);
        boolean cpfExists = id == null
                ? usuarioRepository.existsByCpf(normalizedCpf)
                : usuarioRepository.existsByCpfAndIdNot(normalizedCpf, id);
        if (cpfExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
    }

    private void validateKeycloakChanges(Usuario current, UsuarioRequest request) {
        String normalizedUsername = normalizeUsername(request.username());
        if (normalizedUsername != null && !normalizedUsername.equalsIgnoreCase(current.getUsername())) {
            usuarioManagementService.validateUsernameNotExists(normalizedUsername);
        }

        String normalizedEmail = normalizeEmail(request.email());
        if (normalizedEmail != null && !normalizedEmail.equalsIgnoreCase(current.getEmail())) {
            usuarioManagementService.validateEmailNotExists(normalizedEmail);
        }

        if (request.cpf() != null) {
            String normalizedCpf = normalizeCpf(request.cpf());
            if (!Objects.equals(normalizedCpf, current.getCpf())) {
                usuarioManagementService.validateCpfNotExists(normalizedCpf);
            }
        }
    }

    private void applyRequest(Usuario entity, UsuarioRequest request) {
        if (request.matricula() != null) {
            entity.setMatricula(request.matricula().trim());
        }
        if (request.username() != null) {
            entity.setUsername(normalizeUsername(request.username()));
        }
        if (request.email() != null) {
            entity.setEmail(normalizeEmail(request.email()));
        }
        if (request.nome() != null) {
            entity.setNome(request.nome().trim());
        }
        if (request.nomeSocial() != null) {
            entity.setNomeSocial(request.nomeSocial().trim());
        }
        if (request.cpf() != null) {
            entity.setCpf(normalizeCpf(request.cpf()));
        }
        if (request.curso() != null) {
            entity.setCurso(request.curso());
        }
        if (request.papel() != null) {
            entity.setPapel(request.papel());
        }
        if (request.fotoPerfil() != null) {
            entity.setFotoPerfil(request.fotoPerfil());
        }
        if (request.status() != null) {
            entity.setStatus(request.status());
        }
        if (request.bloqueado() != null) {
            entity.setBloqueado(request.bloqueado());
        }
    }

    private String normalizeUsername(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    private String normalizeEmail(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    private String normalizeCpf(String value) {
        return value == null ? null : value.replaceAll("\\D", "");
    }

    private UsuarioResponse toResponse(Usuario entity) {
        return new UsuarioResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getNome(),
                entity.getNomeSocial(),
                entity.getCpf(),
                entity.getCurso(),
                entity.getPapel(),
                entity.getFotoPerfil(),
                entity.getStatus(),
                entity.getBloqueado(),
                entity.getMatricula()
        );
    }
}
