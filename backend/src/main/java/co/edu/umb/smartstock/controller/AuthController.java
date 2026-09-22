package co.edu.umb.smartstock.controller;

import org.springframework.web.bind.annotation.*;

/**
 * AuthController — capa Controlador.
 * Gestiona el inicio de sesion y autenticacion de usuarios.
 * Fase 2: esqueleto sin logica real (sin JWT/Spring Security todavia).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public String login(@RequestBody Object credenciales) {
        // TODO Fase 3: validar credenciales contra UsuarioRepository y emitir token JWT.
        return "Endpoint de autenticacion — pendiente de implementar en Fase 3";
    }
}
