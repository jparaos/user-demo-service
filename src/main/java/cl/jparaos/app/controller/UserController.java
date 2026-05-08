package cl.jparaos.app.controller;

import cl.jparaos.app.dto.SignUpRequest;
import cl.jparaos.app.dto.UserResponse;
import cl.jparaos.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponse> signUp(@RequestBody SignUpRequest request) {
        UserResponse response = userService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/login")
    public ResponseEntity<UserResponse> login(
            @RequestHeader(value = "Authorization") String token) {
        UserResponse response = userService.login(token);
        return ResponseEntity.ok(response);
    }
}
