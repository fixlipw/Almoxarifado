package com.ufc.almoxarifado.security;

import com.ufc.almoxarifado.entity.Papel;
import com.ufc.almoxarifado.entity.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class KeycloakUsuarioRegistrationService {

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String USER_BY_ID_URL_TEMPLATE = "%s/admin/realms/%s/users/%s";
    private static final String ROLE_ADMIN = "administrador";
    private static final String ROLE_BOLSISTA = "bolsista";
    private static final String HTTP_METHOD_DELETE = "DELETE";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${keycloak.admin.base-url}")
    private String keycloakBaseUrl;

    @Value("${keycloak.admin.realm}")
    private String keycloakRealm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    @Value("${keycloak.admin.roles-client-id}")
    private String rolesClientId;

    @Value("${keycloak.admin.roles-client-uuid}")
    private String rolesClientUuid;

    @Value("${keycloak.admin.role-admin-id}")
    private String adminRoleId;

    @Value("${keycloak.admin.role-bolsista-id}")
    private String bolsistaRoleId;

    @Value("${keycloak.admin.default-password}")
    private String defaultPassword;

    @Value("${keycloak.user.client-id}")
    private String userClientId;

    @Value("${keycloak.user.client-secret:}")
    private String userClientSecret;

    public void validateUsernameNotExists(String username) {
        validateConfig();
        String token = getAdminAccessToken();
        if (tryFindUserIdByUsername(username, token).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username já cadastrado no Keycloak");
        }
    }

    public void validateEmailNotExists(String email) {
        validateConfig();
        String token = getAdminAccessToken();
        if (tryFindUserIdByEmail(email, token).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado no Keycloak");
        }
    }

    public void validateCpfNotExists(String cpf) {
        validateConfig();
        String token = getAdminAccessToken();
        if (existsUserByCpf(cpf, token)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado no Keycloak");
        }
    }

    public void register(Usuario usuario) {
        validateConfig();
        String token = getAdminAccessToken();
        String userId = createUser(usuario, token);
        try {
            syncUserRole(userId, usuario.getPapel(), token);
        } catch (Exception ex) {
            rollbackCreatedUser(userId, token);
            throw ex;
        }
    }

    public void syncUser(String previousEmail, Usuario usuario) {
        validateConfig();
        String token = getAdminAccessToken();
        String userId = resolveUserIdForUpdate(previousEmail, usuario.getUsername(), usuario.getEmail(), token);
        updateUserProfile(userId, usuario, token);
        syncUserRole(userId, usuario.getPapel(), token);
    }

    public void syncRole(Usuario usuario) {
        validateConfig();
        String token = getAdminAccessToken();
        String userId = findUserIdByEmail(usuario.getEmail(), token);
        syncUserRole(userId, usuario.getPapel(), token);
    }

    public void syncStatus(Usuario usuario) {
        validateConfig();
        String token = getAdminAccessToken();
        String userId = findUserIdByEmail(usuario.getEmail(), token);
        updateUserEnabled(userId, usuario.getStatus() != null && usuario.getStatus(), token);
    }

    public void revokeClientRoles(Usuario usuario) {
        validateConfig();
        String token = getAdminAccessToken();
        String userId = tryFindUserIdByUsername(usuario.getUsername(), token)
                .or(() -> tryFindUserIdByEmail(usuario.getEmail(), token))
                .orElse(null);
        if (userId == null) {
            return;
        }
        String targetClientUuid = resolveRolesClientUuid(token);
        removeManagedRoles(userId, targetClientUuid, token);
    }

    public void changeOwnPassword(Usuario usuario, String currentPassword, String newPassword) {
        validateConfig();
        validateCurrentPassword(usuario, currentPassword);

        String token = getAdminAccessToken();
        String userId = findUserIdByEmail(usuario.getEmail(), token);
        resetUserPassword(userId, newPassword, token);
    }

    private void validateConfig() {
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Configuração do Keycloak admin ausente (client-id/client-secret)"
            );
        }
    }

    private String getAdminAccessToken() {
        String tokenUrl = "%s/realms/%s/protocol/openid-connect/token".formatted(keycloakBaseUrl, keycloakRealm);
        String body = "grant_type=client_credentials&client_id=%s&client_secret=%s"
                .formatted(urlEncode(clientId), urlEncode(clientSecret));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_FORM_URLENCODED)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                log.error("Erro ao obter token admin do Keycloak: status={}, body={}", response.statusCode(), response.body());
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao autenticar no Keycloak");
            }

            String token = extractAccessToken(response.body());
            if (token == null || token.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Token inválido retornado pelo Keycloak");
            }
            return token;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Erro inesperado ao obter token admin do Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao autenticar no Keycloak", ex);
        } catch (Exception ex) {
            log.error("Erro inesperado ao obter token admin do Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao autenticar no Keycloak", ex);
        }
    }

    private void validateCurrentPassword(Usuario usuario, String currentPassword) {
        String tokenUrl = "%s/realms/%s/protocol/openid-connect/token".formatted(keycloakBaseUrl, keycloakRealm);
        String body = buildPasswordGrantBody(usuario.getUsername(), currentPassword);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_FORM_URLENCODED)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 400) {
                return;
            }
            if (response.statusCode() == 400 || response.statusCode() == 401) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual inválida");
            }
            log.error(
                    "Erro ao validar senha atual no Keycloak: status={}, body={}",
                    response.statusCode(),
                    response.body()
            );
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao validar senha atual");
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Erro inesperado ao validar senha atual no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao validar senha atual", ex);
        } catch (Exception ex) {
            log.error("Erro inesperado ao validar senha atual no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao validar senha atual", ex);
        }
    }

    private String buildPasswordGrantBody(String username, String password) {
        StringBuilder body = new StringBuilder()
                .append("grant_type=password")
                .append("&client_id=").append(urlEncode(userClientId))
                .append("&username=").append(urlEncode(username))
                .append("&password=").append(urlEncode(password));

        if (userClientSecret != null && !userClientSecret.isBlank()) {
            body.append("&client_secret=").append(urlEncode(userClientSecret));
        }

        return body.toString();
    }

    private String createUser(Usuario usuario, String token) {
        String usersUrl = "%s/admin/realms/%s/users".formatted(keycloakBaseUrl, keycloakRealm);
        String fullName = usuario.getNome() == null ? "" : usuario.getNome().trim();
        String firstName = extractFirstName(fullName);
        String lastName = extractLastName(fullName);
        String payload = """
                {
                  "username": "%s",
                  "email": "%s",
                  "firstName": "%s",
                  "lastName": "%s",
                  "enabled": %s,
                  "emailVerified": true,
                  "requiredActions": ["UPDATE_PASSWORD"],
                  "attributes": {
                    "cpf": ["%s"],
                    "nomeSocial": ["%s"]
                  },
                  "credentials": [
                    {
                      "type": "password",
                      "value": "%s",
                      "temporary": true
                    }
                  ]
                }
                """.formatted(
                escapeJson(usuario.getUsername()),
                escapeJson(usuario.getEmail()),
                escapeJson(firstName),
                escapeJson(lastName),
                usuario.getStatus() != null && usuario.getStatus(),
                escapeJson(usuario.getCpf()),
                escapeJson(usuario.getNomeSocial()),
                escapeJson(defaultPassword)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(usersUrl))
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_JSON)
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
                Optional<String> location = response.headers().firstValue("location");
                if (location.isPresent() && location.get().contains("/")) {
                    return location.get().substring(location.get().lastIndexOf('/') + 1);
                }
                return findUserIdByEmail(usuario.getEmail(), token);
            }
            if (response.statusCode() == 409) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já existe no Keycloak");
            }
            log.error("Erro ao criar usuário no Keycloak: status={}, body={}", response.statusCode(), response.body());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao criar usuário no Keycloak");
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Erro inesperado ao criar usuário no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao criar usuário no Keycloak", ex);
        } catch (Exception ex) {
            log.error("Erro inesperado ao criar usuário no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao criar usuário no Keycloak", ex);
        }
    }

    private String findUserIdByEmail(String email, String token) {
        return tryFindUserIdByEmail(email, token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Usuário não encontrado no Keycloak"));
    }

    private Optional<String> tryFindUserIdByEmail(String email, String token) {
        String usersUrl = "%s/admin/realms/%s/users?email=%s&exact=true"
                .formatted(keycloakBaseUrl, keycloakRealm, urlEncode(email));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(usersUrl))
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                log.error(
                        "Erro ao buscar usuário no Keycloak por email: status={}, body={}",
                        response.statusCode(),
                        response.body()
                );
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao buscar usuário no Keycloak");
            }

            String userId = extractFirstField(response.body(), "id");
            if (userId == null || userId.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(userId);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Erro inesperado ao buscar usuário no Keycloak por email", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao buscar usuário no Keycloak", ex);
        } catch (Exception ex) {
            log.error("Erro inesperado ao buscar usuário no Keycloak por email", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao buscar usuário no Keycloak", ex);
        }
    }

    private Optional<String> tryFindUserIdByUsername(String username, String token) {
        String usersUrl = "%s/admin/realms/%s/users?username=%s&exact=true"
                .formatted(keycloakBaseUrl, keycloakRealm, urlEncode(username));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(usersUrl))
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                log.error(
                        "Erro ao buscar usuário no Keycloak por username: status={}, body={}",
                        response.statusCode(),
                        response.body()
                );
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao buscar usuário no Keycloak");
            }

            String userId = extractFirstField(response.body(), "id");
            if (userId == null || userId.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(userId);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Erro inesperado ao buscar usuário no Keycloak por username", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao buscar usuário no Keycloak", ex);
        } catch (Exception ex) {
            log.error("Erro inesperado ao buscar usuário no Keycloak por username", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao buscar usuário no Keycloak", ex);
        }
    }

    private boolean existsUserByCpf(String cpf, String token) {
        String query = "cpf:" + cpf;
        String usersUrl = "%s/admin/realms/%s/users?q=%s"
                .formatted(keycloakBaseUrl, keycloakRealm, urlEncode(query));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(usersUrl))
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                log.error(
                        "Erro ao buscar usuário no Keycloak por CPF: status={}, body={}",
                        response.statusCode(),
                        response.body()
                );
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao validar CPF no Keycloak");
            }

            return extractFirstField(response.body(), "id") != null;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Erro inesperado ao buscar usuário no Keycloak por CPF", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao validar CPF no Keycloak", ex);
        } catch (Exception ex) {
            log.error("Erro inesperado ao buscar usuário no Keycloak por CPF", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao validar CPF no Keycloak", ex);
        }
    }

    private String resolveUserIdForUpdate(String previousEmail, String currentUsername, String currentEmail, String token) {
        Optional<String> byUsername = tryFindUserIdByUsername(currentUsername, token);
        if (byUsername.isPresent()) {
            return byUsername.get();
        }

        Optional<String> byCurrent = tryFindUserIdByEmail(currentEmail, token);
        if (byCurrent.isPresent()) {
            return byCurrent.get();
        }

        if (previousEmail != null && !previousEmail.isBlank() && !previousEmail.equalsIgnoreCase(currentEmail)) {
            Optional<String> byPrevious = tryFindUserIdByEmail(previousEmail, token);
            if (byPrevious.isPresent()) {
                return byPrevious.get();
            }
        }

        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Usuário não encontrado no Keycloak para atualização");
    }

    private void updateUserProfile(String userId, Usuario usuario, String token) {
        String userUrl = USER_BY_ID_URL_TEMPLATE.formatted(keycloakBaseUrl, keycloakRealm, userId);
        String fullName = usuario.getNome() == null ? "" : usuario.getNome().trim();
        String payload = """
                {
                  "username": "%s",
                  "email": "%s",
                  "firstName": "%s",
                  "lastName": "%s",
                  "enabled": %s,
                  "emailVerified": true,
                  "attributes": {
                    "cpf": ["%s"],
                    "nomeSocial": ["%s"]
                  }
                }
                """.formatted(
                escapeJson(usuario.getUsername()),
                escapeJson(usuario.getEmail()),
                escapeJson(extractFirstName(fullName)),
                escapeJson(extractLastName(fullName)),
                usuario.getStatus() != null && usuario.getStatus(),
                escapeJson(usuario.getCpf()),
                escapeJson(usuario.getNomeSocial() != null ? usuario.getNomeSocial() : "")

        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(userUrl))
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_JSON)
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                .PUT(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204) {
                return;
            }
            log.error("Erro ao atualizar usuário no Keycloak: status={}, body={}", response.statusCode(), response.body());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao atualizar usuário no Keycloak");
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Erro inesperado ao atualizar usuário no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao atualizar usuário no Keycloak", ex);
        } catch (Exception ex) {
            log.error("Erro inesperado ao atualizar usuário no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao atualizar usuário no Keycloak", ex);
        }
    }

    private void updateUserEnabled(String userId, boolean enabled, String token) {
        String userUrl = USER_BY_ID_URL_TEMPLATE.formatted(keycloakBaseUrl, keycloakRealm, userId);
        String payload = """
                {
                  "enabled": %s
                }
                """.formatted(enabled);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(userUrl))
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_JSON)
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                .PUT(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204) {
                return;
            }
            log.error("Erro ao atualizar status do usuário no Keycloak: status={}, body={}", response.statusCode(), response.body());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao atualizar status do usuário no Keycloak");
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Erro inesperado ao atualizar status do usuário no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao atualizar status do usuário no Keycloak", ex);
        } catch (Exception ex) {
            log.error("Erro inesperado ao atualizar status do usuário no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao atualizar status do usuário no Keycloak", ex);
        }
    }

    private void resetUserPassword(String userId, String newPassword, String token) {
        String userUrl = USER_BY_ID_URL_TEMPLATE.formatted(keycloakBaseUrl, keycloakRealm, userId);
        String payload = """
                {
                  "type": "password",
                  "value": "%s",
                  "temporary": false
                }
                """.formatted(escapeJson(newPassword));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(userUrl + "/reset-password"))
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_JSON)
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                .PUT(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204) {
                return;
            }
            log.error("Erro ao atualizar senha do usuário no Keycloak: status={}, body={}", response.statusCode(), response.body());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao atualizar senha do usuário");
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Erro inesperado ao atualizar senha do usuário no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao atualizar senha do usuário", ex);
        } catch (Exception ex) {
            log.error("Erro inesperado ao atualizar senha do usuário no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao atualizar senha do usuário", ex);
        }
    }

    private void syncUserRole(String userId, Papel papel, String token) {
        String targetClientUuid = resolveRolesClientUuid(token);
        removeManagedRoles(userId, targetClientUuid, token);
        String roleName = roleNameByPapel(papel);
        if (roleName == null) {
            return;
        }
        KeycloakRole role = resolveRole(targetClientUuid, roleName, token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Role não encontrada no Keycloak: " + roleName));
        addClientRolesToUser(userId, targetClientUuid, List.of(role), token);
    }

    private void removeManagedRoles(String userId, String rolesClientUuid, String token) {
        List<KeycloakRole> rolesToRemove = new ArrayList<>();
        resolveRole(rolesClientUuid, ROLE_ADMIN, token).ifPresent(rolesToRemove::add);
        resolveRole(rolesClientUuid, ROLE_BOLSISTA, token).ifPresent(rolesToRemove::add);
        if (rolesToRemove.isEmpty()) {
            return;
        }
        removeClientRolesFromUser(userId, rolesClientUuid, rolesToRemove, token);
    }

    private Optional<KeycloakRole> resolveRole(String clientUuid, String roleName, String token) {
        String configuredRoleId = roleIdByName(roleName);
        if (configuredRoleId != null && !configuredRoleId.isBlank()) {
            return Optional.of(new KeycloakRole(configuredRoleId, roleName));
        }
        return getClientRoleByName(clientUuid, roleName, token);
    }

    private String roleIdByName(String roleName) {
        if (ROLE_ADMIN.equalsIgnoreCase(roleName)) {
            return adminRoleId;
        }
        if (ROLE_BOLSISTA.equalsIgnoreCase(roleName)) {
            return bolsistaRoleId;
        }
        return null;
    }

    private String resolveRolesClientUuid(String token) {
        if (rolesClientUuid != null && !rolesClientUuid.isBlank()) {
            return rolesClientUuid;
        }
        return findClientUuidByClientId(rolesClientId, token);
    }

    private String roleNameByPapel(Papel papel) {
        if (papel == Papel.ADMIN) {
            return ROLE_ADMIN;
        }
        if (papel == Papel.BOLSISTA) {
            return ROLE_BOLSISTA;
        }
        return null;
    }

    private String findClientUuidByClientId(String targetClientId, String token) {
        String clientsUrl = "%s/admin/realms/%s/clients?clientId=%s"
                .formatted(keycloakBaseUrl, keycloakRealm, urlEncode(targetClientId));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(clientsUrl))
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                log.error("Erro ao buscar client no Keycloak: status={}, body={}", response.statusCode(), response.body());
                if (response.statusCode() == 403) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_GATEWAY,
                            "Sem permissão para consultar clients no Keycloak. Configure keycloak.admin.roles-client-uuid"
                    );
                }
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao buscar client no Keycloak");
            }

            String clientUuid = extractFirstField(response.body(), "id");
            if (clientUuid == null || clientUuid.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Client de roles não encontrado no Keycloak");
            }
            return clientUuid;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Erro inesperado ao buscar client no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao buscar client no Keycloak", ex);
        } catch (Exception ex) {
            log.error("Erro inesperado ao buscar client no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao buscar client no Keycloak", ex);
        }
    }

    private Optional<KeycloakRole> getClientRoleByName(String rolesClientUuid, String roleName, String token) {
        String roleUrl = "%s/admin/realms/%s/clients/%s/roles/%s"
                .formatted(keycloakBaseUrl, keycloakRealm, rolesClientUuid, urlEncode(roleName));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(roleUrl))
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) {
                return Optional.empty();
            }
            if (response.statusCode() >= 400) {
                log.error("Erro ao buscar role de client no Keycloak: status={}, body={}", response.statusCode(), response.body());
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao buscar role de client no Keycloak");
            }

            String id = extractFirstField(response.body(), "id");
            String name = extractFirstField(response.body(), "name");
            if (id == null || id.isBlank() || name == null || name.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Role de client inválida retornada pelo Keycloak");
            }
            return Optional.of(new KeycloakRole(id, name));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Erro inesperado ao buscar role de client no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao buscar role de client no Keycloak", ex);
        } catch (Exception ex) {
            log.error("Erro inesperado ao buscar role de client no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao buscar role de client no Keycloak", ex);
        }
    }

    private void addClientRolesToUser(String userId, String rolesClientUuid, List<KeycloakRole> roles, String token) {
        syncClientRoleMappings(userId, rolesClientUuid, roles, token, "POST");
    }

    private void removeClientRolesFromUser(String userId, String rolesClientUuid, List<KeycloakRole> roles, String token) {
        syncClientRoleMappings(userId, rolesClientUuid, roles, token, HTTP_METHOD_DELETE);
    }

    private void syncClientRoleMappings(String userId, String rolesClientUuid, List<KeycloakRole> roles, String token, String method) {
        String roleMappingsUrl = "%s/admin/realms/%s/users/%s/role-mappings/clients/%s"
                .formatted(keycloakBaseUrl, keycloakRealm, userId, rolesClientUuid);
        String payload = rolesToJson(roles);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(payload);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(roleMappingsUrl))
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_JSON)
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token);

        HttpRequest request = switch (method) {
            case "POST" -> requestBuilder.POST(body).build();
            case HTTP_METHOD_DELETE -> requestBuilder.method(HTTP_METHOD_DELETE, body).build();
            default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Método HTTP inválido");
        };

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204) {
                return;
            }
            log.error(
                    "Erro ao sincronizar role mappings de client no Keycloak: status={}, body={}",
                    response.statusCode(),
                    response.body()
            );
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao sincronizar perfis no Keycloak");
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Erro inesperado ao sincronizar role mappings de client no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao sincronizar perfis no Keycloak", ex);
        } catch (Exception ex) {
            log.error("Erro inesperado ao sincronizar role mappings de client no Keycloak", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao sincronizar perfis no Keycloak", ex);
        }
    }

    private void rollbackCreatedUser(String userId, String token) {
        String userUrl = USER_BY_ID_URL_TEMPLATE.formatted(keycloakBaseUrl, keycloakRealm, userId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(userUrl))
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204 || response.statusCode() == 404) {
                return;
            }
            log.error(
                    "Falha ao desfazer criação do usuário no Keycloak: userId={}, status={}, body={}",
                    userId,
                    response.statusCode(),
                    response.body()
            );
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Erro inesperado ao desfazer criação do usuário no Keycloak: userId={}", userId, ex);
        } catch (Exception ex) {
            log.error("Erro inesperado ao desfazer criação do usuário no Keycloak: userId={}", userId, ex);
        }
    }

    private String urlEncode(String value) {
        return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String escapeJson(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String extractAccessToken(String responseBody) {
        Matcher matcher = Pattern.compile("\"access_token\"\\s*:\\s*\"([^\"]+)\"").matcher(responseBody);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }

    private String extractFirstField(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(fieldName) + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }

    private String rolesToJson(List<KeycloakRole> roles) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < roles.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            KeycloakRole role = roles.get(i);
            builder.append("{\"id\":\"")
                    .append(escapeJson(role.id()))
                    .append("\",\"name\":\"")
                    .append(escapeJson(role.name()))
                    .append("\"}");
        }
        builder.append(']');
        return builder.toString();
    }

    private String extractFirstName(String fullName) {
        String normalized = fullName == null ? "" : fullName.trim();
        if (normalized.isEmpty()) {
            return "";
        }
        String[] parts = normalized.split("\\s+");
        return parts[0];
    }

    private String extractLastName(String fullName) {
        String normalized = fullName == null ? "" : fullName.trim();
        if (normalized.isEmpty()) {
            return "";
        }
        String[] parts = normalized.split("\\s+");
        if (parts.length <= 1) {
            return "";
        }
        return String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
    }

    private record KeycloakRole(String id, String name) {
    }
}
