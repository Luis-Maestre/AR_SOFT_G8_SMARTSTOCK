package co.edu.umb.smartstock.controller;

import co.edu.umb.smartstock.config.UsuarioActual;
import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return auth.login(req);
    }

    @GetMapping("/me")
    public UsuarioDto me(@AuthenticationPrincipal UsuarioActual u) {
        return auth.me(u.id());
    }

    @PostMapping("/cambiar-password")
    public ResponseEntity<Void> cambiarPassword(@AuthenticationPrincipal UsuarioActual u,
                                                @Valid @RequestBody CambiarPasswordRequest req) {
        auth.cambiarPassword(u.id(), req);
        return ResponseEntity.noContent().build();
    }
}
