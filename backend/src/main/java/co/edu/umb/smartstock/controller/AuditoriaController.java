package co.edu.umb.smartstock.controller;

import co.edu.umb.smartstock.config.Permisos;
import co.edu.umb.smartstock.dto.Dtos.AuditoriaDto;
import co.edu.umb.smartstock.service.AuditoriaService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auditoria")
@PreAuthorize(Permisos.ADMIN)
public class AuditoriaController {
    private final AuditoriaService servicio;

    public AuditoriaController(AuditoriaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<AuditoriaDto> listar(@RequestParam(defaultValue = "200") int limite) {
        return servicio.listar(limite);
    }
}
