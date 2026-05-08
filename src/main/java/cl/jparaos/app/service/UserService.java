package cl.jparaos.app.service;

import cl.jparaos.app.dto.PhoneDto;
import cl.jparaos.app.dto.SignUpRequest;
import cl.jparaos.app.dto.UserResponse;
import cl.jparaos.app.exception.InvalidTokenException;
import cl.jparaos.app.exception.UserAlreadyExistsException;
import cl.jparaos.app.model.Phone;
import cl.jparaos.app.model.User;
import cl.jparaos.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ValidationService validationService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse signUp(SignUpRequest request) {
        // Validaciones con lanzamiento de excepciones manejadas por GlobalExceptionHandler
        validationService.validateEmail(request.getEmail());
        validationService.validatePassword(request.getPassword());

        // Verificar si el usuario ya existe
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new UserAlreadyExistsException(request.getEmail());
        });

        String token = jwtService.generateToken(request.getEmail());

        // Java 8 feature: streams para mapear phones
        List<Phone> phones = Optional.ofNullable(request.getPhones())
                .orElse(Collections.emptyList())
                .stream()
                .map(dto -> Phone.builder()
                        .number(dto.getNumber())
                        .citycode(dto.getCitycode())
                        .contrycode(dto.getContrycode())
                        .build())
                .collect(Collectors.toList());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .token(token)
                .lastLogin(LocalDateTime.now())
                .build();

        // Asociar phones al user después de crearlo
        phones.forEach(p -> p.setUser(user));
        user.getPhones().addAll(phones);

        User saved = userRepository.save(user);
        return toResponse(saved, request.getPassword()); // retornamos password en claro en sign-up
    }

    @Transactional
    public UserResponse login(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException("Token no proporcionado");
        }

        // Limpiar "Bearer " si viene en el header
        String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        if (!jwtService.isTokenValid(cleanToken)) {
            throw new InvalidTokenException("Token inválido o expirado");
        }

        String email = jwtService.extractEmail(cleanToken);

        // Java 8 feature: Optional con orElseThrow y lambda
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("Usuario no encontrado para el token proporcionado"));

        // Generar nuevo token en cada login
        String newToken = jwtService.generateToken(email);
        user.setToken(newToken);
        user.setLastLogin(LocalDateTime.now());

        User updated = userRepository.save(user);
        return toResponse(updated, null);
    }

    // Java 8 feature: stream + map para convertir phones a DTOs
    private UserResponse toResponse(User user, String rawPassword) {
        List<PhoneDto> phoneDtos = user.getPhones().stream()
                .map(p -> PhoneDto.builder()
                        .number(p.getNumber())
                        .citycode(p.getCitycode())
                        .contrycode(p.getContrycode())
                        .build())
                .collect(Collectors.toList());

        return UserResponse.builder()
                .id(user.getId())
                .created(user.getCreated())
                .lastLogin(user.getLastLogin())
                .token(user.getToken())
                .isActive(user.isActive())
                .name(user.getName())
                .email(user.getEmail())
                .password(rawPassword != null ? rawPassword : user.getPassword())
                .phones(phoneDtos)
                .build();
    }
}