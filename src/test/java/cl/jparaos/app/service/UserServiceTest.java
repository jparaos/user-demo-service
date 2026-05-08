package cl.jparaos.app.service;

import cl.jparaos.app.dto.PhoneDto;
import cl.jparaos.app.dto.SignUpRequest;
import cl.jparaos.app.dto.UserResponse;
import cl.jparaos.app.exception.InvalidTokenException;
import cl.jparaos.app.exception.UserAlreadyExistsException;
import cl.jparaos.app.model.Phone;
import cl.jparaos.app.model.User;
import cl.jparaos.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private ValidationService validationService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private SignUpRequest validRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        PhoneDto phoneDto = PhoneDto.builder()
                .number(87650009L)
                .citycode(7)
                .contrycode("25")
                .build();

        validRequest = new SignUpRequest();
        validRequest.setName("Julio Gonzalez");
        validRequest.setEmail("julio@testssw.cl");
        validRequest.setPassword("a2asfGfdfdf4");
        validRequest.setPhones(List.of(phoneDto));

        savedUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Julio Gonzalez")
                .email("julio@testssw.cl")
                .password("$2a$10$encoded")
                .token("jwt-token-123")
                .created(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .isActive(true)
                .phones(new ArrayList<>())
                .build();
    }

    // ====== SIGN-UP TESTS ======

    @Test
    @DisplayName("signUp exitoso retorna UserResponse con id y token")
    void signUp_success_returnsUserResponse() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(jwtService.generateToken(anyString())).thenReturn("jwt-token-123");
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.signUp(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token-123");
        assertThat(response.isActive()).isTrue();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("signUp lanza UserAlreadyExistsException si email ya existe")
    void signUp_userAlreadyExists_throwsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(savedUser));

        assertThatThrownBy(() -> userService.signUp(validRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("julio@testssw.cl");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("signUp con phones null no lanza excepción")
    void signUp_withNullPhones_success() {
        validRequest.setPhones(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(jwtService.generateToken(anyString())).thenReturn("token");
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        assertThatCode(() -> userService.signUp(validRequest)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("signUp con nombre null (campo opcional) no falla")
    void signUp_withNullName_success() {
        validRequest.setName(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(jwtService.generateToken(anyString())).thenReturn("token");
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.signUp(validRequest);
        assertThat(response).isNotNull();
    }

    // ====== LOGIN TESTS ======

    @Test
    @DisplayName("login exitoso actualiza token y lastLogin")
    void login_success_returnsUpdatedUser() {
        when(jwtService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractEmail("valid-token")).thenReturn("julio@testssw.cl");
        when(userRepository.findByEmail("julio@testssw.cl")).thenReturn(Optional.of(savedUser));
        when(jwtService.generateToken(anyString())).thenReturn("new-token-456");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.login("valid-token");

        assertThat(response).isNotNull();
        verify(jwtService).generateToken("julio@testssw.cl");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("login con Bearer prefix funciona correctamente")
    void login_withBearerPrefix_stripsAndValidates() {
        when(jwtService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractEmail("valid-token")).thenReturn("julio@testssw.cl");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(savedUser));
        when(jwtService.generateToken(anyString())).thenReturn("new-token");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        assertThatCode(() -> userService.login("Bearer valid-token")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("login con token inválido lanza InvalidTokenException")
    void login_invalidToken_throwsException() {
        when(jwtService.isTokenValid("bad-token")).thenReturn(false);

        assertThatThrownBy(() -> userService.login("bad-token"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("inválido");
    }

    @Test
    @DisplayName("login con token nulo lanza InvalidTokenException")
    void login_nullToken_throwsException() {
        assertThatThrownBy(() -> userService.login(null))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("login con token vacío lanza InvalidTokenException")
    void login_emptyToken_throwsException() {
        assertThatThrownBy(() -> userService.login(""))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("login con usuario no encontrado lanza InvalidTokenException")
    void login_userNotFound_throwsException() {
        when(jwtService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractEmail("valid-token")).thenReturn("noexiste@test.cl");
        when(userRepository.findByEmail("noexiste@test.cl")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login("valid-token"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("no encontrado");
    }
}