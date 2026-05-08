package cl.jparaos.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "test-secret-key-very-long-for-tests");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);
    }

    @Test
    @DisplayName("generateToken produce un token no nulo")
    void generateToken_returnsNonNullToken() {
        String token = jwtService.generateToken("test@test.cl");
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("extractEmail retorna el email correcto del token")
    void extractEmail_returnsCorrectEmail() {
        String email = "julio@testssw.cl";
        String token = jwtService.generateToken(email);
        assertThat(jwtService.extractEmail(token)).isEqualTo(email);
    }

    @Test
    @DisplayName("isTokenValid retorna true para token válido")
    void isTokenValid_validToken_returnsTrue() {
        String token = jwtService.generateToken("test@test.cl");
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid retorna false para token manipulado")
    void isTokenValid_tamperedToken_returnsFalse() {
        String token = jwtService.generateToken("test@test.cl") + "tampered";
        assertThat(jwtService.isTokenValid(token)).isFalse();
    }

    @Test
    @DisplayName("isTokenValid retorna false para token expirado")
    void isTokenValid_expiredToken_returnsFalse() {
        // Token con expiración negativa
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);
        String token = jwtService.generateToken("test@test.cl");
        assertThat(jwtService.isTokenValid(token)).isFalse();
    }
}