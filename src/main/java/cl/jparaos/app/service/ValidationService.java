package cl.jparaos.app.service;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ValidationService {

    // aaaaaaa@undominio.algo
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    // Solo una mayúscula, exactamente dos dígitos, resto minúsculas, largo 8-12
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=(.*[A-Z]){1})(?!.*[A-Z].*[A-Z])(?=(.*\\d){2})(?!.*\\d.*\\d.*\\d)[a-zA-Z\\d]{8,12}$");

    public void validateEmail(String email) {
        // Java 8 feature: Optional para manejo explícito de nulls
        Optional.ofNullable(email)
                .filter(e -> EMAIL_PATTERN.matcher(e).matches())
                .orElseThrow(() -> new IllegalArgumentException(
                        "El correo no tiene el formato correcto. Ejemplo: usuario@dominio.algo"));
    }

    public void validatePassword(String password) {
        Optional.ofNullable(password)
                .filter(p -> PASSWORD_PATTERN.matcher(p).matches())
                .orElseThrow(() -> new IllegalArgumentException(
                        "La clave no cumple el formato: una mayúscula, dos números, resto minúsculas, largo 8-12. Ejemplo: a2asfGfdfdf4"));
    }
}
