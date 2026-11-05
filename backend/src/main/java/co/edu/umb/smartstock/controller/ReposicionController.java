package co.edu.umb.smartstock.controller;

import co.edu.umb.smartstock.config.Permisos;
import co.edu.umb.smartstock.config.UsuarioActual;
import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.service.ReposicionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reposiciones")
public class ReposicionController {
    private final ReposicionService servicio;

    public ReposicionController(ReposicionService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<ReposicionDto> listar(@RequestParam(required = false) String estado) {
        return servicio.listar(estado);
    }

    @PostMapping("/generar")
    @PreAuthorize(Permisos.GESTION)
    public List<ReposicionDto> generar(@RequestParam(required = false) Long idProducto,
                                       @AuthenticationPrincipal UsuarioActual u) {
        return servicio.generar(idProducto, u.id());
    }

    @PutMapping("/{id}/confirmar")
    @PreAuthorize(Permisos.GESTION)
    public ReposicionDto confirmar(@PathVariable Long id, @Valid @RequestBody ConfirmarReposicionRequest req,
                                   @AuthenticationPrincipal UsuarioActual u) {
        return servicio.confirmar(id, req, u.id());
    }

    @PutMapping("/{id}/rechazar")
    @PreAuthorize(Permisos.GESTION)
    public ReposicionDto rechazar(@PathVariable Long id,
                                  @RequestBody(required = false) @Valid RechazarReposicionRequest req,
                                  @AuthenticationPrincipal UsuarioActual u) {
        return servicio.rechazar(id, req, u.id());
    }
}
