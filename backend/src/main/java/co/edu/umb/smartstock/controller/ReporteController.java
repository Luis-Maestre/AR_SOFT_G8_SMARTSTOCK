package co.edu.umb.smartstock.controller;

import co.edu.umb.smartstock.dto.Dtos.DashboardDto;
import co.edu.umb.smartstock.service.ReporteService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    private static final MediaType CSV = MediaType.parseMediaType("text/csv; charset=UTF-8");
    private final ReporteService servicio;

    public ReporteController(ReporteService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/dashboard")
    public DashboardDto dashboard() {
        return servicio.dashboard();
    }

    @GetMapping("/inventario/csv")
    public ResponseEntity<byte[]> inventarioCsv() {
        return csv(servicio.inventarioCsv(), "inventario.csv");
    }

    @GetMapping("/movimientos/csv")
    public ResponseEntity<byte[]> movimientosCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return csv(servicio.movimientosCsv(desde, hasta), "movimientos.csv");
    }

    private static ResponseEntity<byte[]> csv(byte[] datos, String nombre) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .contentType(CSV)
                .body(datos);
    }
}
