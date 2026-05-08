package cl.jparaos.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class ValidationServiceTest {

    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
    }

    // ====== EMAIL ======

    @ParameterizedTest
    @DisplayName("Emails válidos no lanzan excepción")
    @ValueSource(strings = {
            "julio@testssw.cl",
            "user@dominio.com",
            "test.user@empresa.org"
    })
    void validateEmail_validEmails_noException(String email) {
        assertThatCode(() -> validationService.validateEmail(email)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @DisplayName("Emails inválidos lanzan IllegalArgumentException")
    @ValueSource(strings = {
            "sinArroba",
            "sin@dominio",
            "@sinusuario.cl",
            "",
    })
    void validateEmail_invalidEmails_throwsException(String email) {
        assertThatThrownBy(() -> validationService.validateEmail(email))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Email nulo lanza IllegalArgumentException")
    void validateEmail_null_throwsException() {
        assertThatThrownBy(() -> validationService.validateEmail(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ====== PASSWORD ======

    @ParameterizedTest
    @DisplayName("Passwords válidas no lanzan excepción")
    @ValueSource(strings = {
            "a2asfGfdfdf4",   // ejemplo del PDF
            "hola1Mundo2",
            "abcD12efgh"
    })
    void validatePassword_validPasswords_noException(String password) {
        assertThatCode(() -> validationService.validatePassword(password)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @DisplayName("Passwords inválidas lanzan IllegalArgumentException")
    @ValueSource(strings = {
            "sinMayuscula1",     // sin mayúscula
            "SINMINUSCULA1a",    // dos mayúsculas
            "soloLetrasG",       // sin números
            "Abc123",            // muy corta
            "abcD1efghijklm",   // muy larga (>12)
            "abcDe123fg",        // tres números
    })
    void validatePassword_invalidPasswords_throwsException(String password) {
        assertThatThrownBy(() -> validationService.validatePassword(password))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Password nula lanza IllegalArgumentException")
    void validatePassword_null_throwsException() {
        assertThatThrownBy(() -> validationService.validatePassword(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}