package co.edu.umb.smartstock.controller;

import co.edu.umb.smartstock.config.Permisos;
import co.edu.umb.smartstock.config.UsuarioActual;
import co.edu.umb.smartstock.dto.Dtos.AlertaDto;
import co.edu.umb.smartstock.service.AlertaService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {
    private final AlertaService servicio;

    public AlertaController(AlertaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<AlertaDto> listar(@RequestParam(defaultValue = "pendiente") String estado) {
        return servicio.listar(estado);
    }

    @PutMapping("/{id}/resolver")
    @PreAuthorize(Permisos.GESTION)
    public AlertaDto resolver(@PathVariable Long id, @AuthenticationPrincipal UsuarioActual u) {
        return servicio.resolver(id, u.id());
    }
}
