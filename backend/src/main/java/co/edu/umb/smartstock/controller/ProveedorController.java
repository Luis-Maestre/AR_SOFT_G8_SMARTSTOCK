package co.edu.umb.smartstock.controller;

import co.edu.umb.smartstock.config.Permisos;
import co.edu.umb.smartstock.config.UsuarioActual;
import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.service.ProveedorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {
    private final ProveedorService servicio;

    public ProveedorController(ProveedorService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<ProveedorDto> listar(@RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return servicio.listar(incluirInactivos);
    }

    @PostMapping
    @PreAuthorize(Permisos.GESTION)
    public ResponseEntity<ProveedorDto> crear(@Valid @RequestBody ProveedorRequest req,
                                              @AuthenticationPrincipal UsuarioActual u) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(req, u.id()));
    }

    @PutMapping("/{id}")
    @PreAuthorize(Permisos.GESTION)
    public ProveedorDto actualizar(@PathVariable Long id, @Valid @RequestBody ProveedorRequest req,
                                   @AuthenticationPrincipal UsuarioActual u) {
        return servicio.actualizar(id, req, u.id());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(Permisos.GESTION)
    public ResponseEntity<Void> desactivar(@PathVariable Long id, @AuthenticationPrincipal UsuarioActual u) {
        servicio.desactivar(id, u.id());
        return ResponseEntity.noContent().build();
    }
}
