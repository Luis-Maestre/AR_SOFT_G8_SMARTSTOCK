package co.edu.umb.smartstock.controller;

import co.edu.umb.smartstock.config.Permisos;
import co.edu.umb.smartstock.config.UsuarioActual;
import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@PreAuthorize(Permisos.ADMIN)
public class UsuarioController {
    private final UsuarioService servicio;

    public UsuarioController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/usuarios")
    public List<UsuarioDto> listar() {
        return servicio.listar();
    }

    @GetMapping("/roles")
    public List<RolDto> roles() {
        return servicio.roles();
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioDto> crear(@Valid @RequestBody UsuarioRequest req, @AuthenticationPrincipal UsuarioActual u) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(req, u.id()));
    }

    @PutMapping("/usuarios/{id}")
    public UsuarioDto actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest req,
                                 @AuthenticationPrincipal UsuarioActual u) {
        return servicio.actualizar(id, req, u.id());
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id, @AuthenticationPrincipal UsuarioActual u) {
        servicio.desactivar(id, u.id());
        return ResponseEntity.noContent().build();
    }
}
