package co.edu.umb.smartstock.controller;

import co.edu.umb.smartstock.config.Permisos;
import co.edu.umb.smartstock.config.UsuarioActual;
import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.service.InventarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {
    private final InventarioService servicio;

    public InventarioController(InventarioService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<InventarioDto> listar(@RequestParam(defaultValue = "false") boolean soloBajoMinimo) {
        return servicio.listar(soloBajoMinimo);
    }

    @PutMapping("/{idProducto}/ubicacion")
    @PreAuthorize(Permisos.GESTION)
    public InventarioDto ubicacion(@PathVariable Long idProducto, @Valid @RequestBody UbicacionRequest req,
                                   @AuthenticationPrincipal UsuarioActual u) {
        return servicio.actualizarUbicacion(idProducto, req.ubicacion(), u.id());
    }
}
