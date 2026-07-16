package com.ufc.almoxarifado.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.List;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull AuthenticationException authException)
            throws IOException {
        writeError(request, response);
    }

    private void writeError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiErrorResponse body = new ApiErrorResponse(
                java.time.Instant.now().toString(),
                HttpStatus.UNAUTHORIZED.value(),
                "não autenticado",
                "Credenciais inválidas ou expiradas.",
                List.of(),
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getWriter(), body);
    }
}