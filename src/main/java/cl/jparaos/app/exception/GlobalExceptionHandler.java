package cl.jparaos.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, List<Map<String, Object>>>> handleUserExists(UserAlreadyExistsException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, List<Map<String, Object>>>> handleInvalidToken(InvalidTokenException ex) {
        return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, List<Map<String, Object>>>> handleValidation(IllegalArgumentException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, List<Map<String, Object>>>> handleGeneral(Exception ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    // Java 11 feature: uso de Map.of() y List.of() — inmutable collections
    private ResponseEntity<Map<String, List<Map<String, Object>>>> buildError(HttpStatus status, String detail) {
        var errorDetail = Map.<String, Object>of(
                "timestamp", Timestamp.from(Instant.now()),
                "codigo", status.value(),
                "detail", detail
        );
        var body = Map.of("error", List.of(errorDetail));
        return ResponseEntity.status(status).body(body);
    }
}
