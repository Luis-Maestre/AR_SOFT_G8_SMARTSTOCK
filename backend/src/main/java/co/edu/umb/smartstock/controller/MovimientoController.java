package co.edu.umb.smartstock.controller;

import co.edu.umb.smartstock.config.Permisos;
import co.edu.umb.smartstock.config.UsuarioActual;
import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.service.InventarioService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {
    private final InventarioService servicio;

    public MovimientoController(InventarioService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<MovimientoDto> listar(
            @RequestParam(required = false) Long idProducto,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "200") int limite) {
        return servicio.listarMovimientos(idProducto, tipo, desde, hasta, limite);
    }

    @PostMapping
    @PreAuthorize(Permisos.GESTION)
    public ResponseEntity<MovimientoDto> registrar(@Valid @RequestBody MovimientoRequest req,
                                                   @AuthenticationPrincipal UsuarioActual u) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.registrarMovimiento(req, u.id()));
    }
}
