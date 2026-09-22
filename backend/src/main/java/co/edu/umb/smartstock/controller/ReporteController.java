package co.edu.umb.smartstock.controller;

import org.springframework.web.bind.annotation.*;

/**
 * ReporteController — capa Controlador.
 * Procesa la informacion necesaria para generar reportes consolidados.
 */
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @GetMapping("/resumen")
    public String resumen() {
        // TODO Fase 3: agregar totales de productos, inventario, alertas y movimientos.
        return "Endpoint de reportes — pendiente de implementar en Fase 3";
    }
}
