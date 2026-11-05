package co.edu.umb.smartstock.controller;

import co.edu.umb.smartstock.config.Permisos;
import co.edu.umb.smartstock.config.UsuarioActual;
import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.service.ProductoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService servicio;

    public ProductoController(ProductoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<ProductoDto> listar(@RequestParam(required = false) String q,
                                    @RequestParam(required = false) Long idCategoria,
                                    @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return servicio.listar(q, idCategoria, incluirInactivos);
    }

    @GetMapping("/{id}")
    public ProductoDto obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @PreAuthorize(Permisos.GESTION)
    public ResponseEntity<ProductoDto> crear(@Valid @RequestBody ProductoRequest req,
                                             @AuthenticationPrincipal UsuarioActual u) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(req, u.id()));
    }

    @PutMapping("/{id}")
    @PreAuthorize(Permisos.GESTION)
    public ProductoDto actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest req,
                                  @AuthenticationPrincipal UsuarioActual u) {
        return servicio.actualizar(id, req, u.id());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(Permisos.GESTION)
    public ResponseEntity<Void> desactivar(@PathVariable Long id, @AuthenticationPrincipal UsuarioActual u) {
        servicio.desactivar(id, u.id());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivar")
    @PreAuthorize(Permisos.GESTION)
    public ProductoDto reactivar(@PathVariable Long id, @AuthenticationPrincipal UsuarioActual u) {
        return servicio.reactivar(id, u.id());
    }
}
