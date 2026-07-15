package com.ufc.almoxarifado.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), null, ex);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientStock(InsufficientStockException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Estoque insuficiente", ex.getMessage(), null, ex);
    }

    @ExceptionHandler(DuplicateFieldException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateField(DuplicateFieldException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Conflito de dados", ex.getMessage(), null, ex);
    }

    @ExceptionHandler(SelfActionException.class)
    public ResponseEntity<ApiErrorResponse> handleSelfAction(SelfActionException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Ação não permitida", ex.getMessage(), null, ex);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Estado inválido", ex.getMessage(), null, ex);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiErrorResponse> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Conflito de atualização", "O recurso foi alterado por outra operação. Atualize os dados e tente novamente.", null, ex);
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleNotFound(Exception ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", "O recurso solicitado não foi encontrado.", null, ex);
    }

    @ExceptionHandler(ReportGenerationException.class)
    public ResponseEntity<ApiErrorResponse> handleReportGeneration(ReportGenerationException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar relatório", "Ocorreu um erro ao gerar o relatório. Tente novamente mais tarde.", null, ex);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        return buildResponse(ex.getStatusCode(), "Erro de resposta", message, null, ex);
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ApiErrorResponse> handleAuthentication(Exception ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "não autenticado", "Credenciais inválidas ou expiradas.", null, ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Acesso negado", "Você não tem permissão para realizar esta ação.", null, ex);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiErrorResponse> handleValidation(Exception ex) {
        List<String> details = switch (ex) {
            case BindException bindException -> bindException.getBindingResult().getFieldErrors().stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .toList();
            case ConstraintViolationException constraintViolationException ->
                    constraintViolationException.getConstraintViolations().stream()
                            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                            .toList();
            default -> List.of();
        };

        return buildResponse(HttpStatus.BAD_REQUEST, "Erro de validação", "Um ou mais campos estão inválidos.", details, ex);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception ex) {
        String message = switch (ex) {
            case HttpMessageNotReadableException readableEx -> "Corpo da requisição inválido ou ausente.";
            case MissingServletRequestParameterException missing ->
                    "Parâmetro obrigatório ausente: " + missing.getParameterName();
            case MethodArgumentTypeMismatchException mismatch -> "Parâmetro inválido: " + mismatch.getName();
            default -> ex.getMessage() != null ? ex.getMessage() : "Requisição inválida.";
        };

        return buildResponse(HttpStatus.BAD_REQUEST, "Requisição inválida", message, null, ex);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleUploadTooLarge(MaxUploadSizeExceededException ex) {
        return buildResponse(HttpStatus.CONTENT_TOO_LARGE, "Arquivo muito grande", "O arquivo enviado excede o tamanho permitido.", null, ex);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Erro de integridade dos dados", "Erro de integridade de dados. Verifique as restrições do banco de dados.", null, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno de servidor", "Ocorreu um erro inesperado no servidor.", null, ex);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatusCode status,
            String error,
            String message,
            List<String> details,
            Exception ex) {
        logException(status, error, message, ex);
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(
                        Instant.now().toString(),
                        status.value(),
                        error,
                        message,
                        details,
                        resolvePath()
                ));
    }

    private String resolvePath() {
        var attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest().getRequestURI();
        }
        return null;
    }

    private void logException(HttpStatusCode status, String error, String message, Exception ex) {
        if (status.value() >= 500) {
            log.error("{} - {}", error, message, ex);
            return;
        }

        log.warn("{} - {}", error, message, ex);
    }
}
