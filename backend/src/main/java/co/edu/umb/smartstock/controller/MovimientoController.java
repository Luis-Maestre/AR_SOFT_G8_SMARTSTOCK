package co.edu.umb.smartstock.controller;

import co.edu.umb.smartstock.model.MovimientoInventario;
import co.edu.umb.smartstock.repository.MovimientoInventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * MovimientoController — capa Controlador.
 * Controla entradas, salidas y ajustes de inventario.
 */
@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    @GetMapping
    public List<MovimientoInventario> listar() {
        return movimientoRepository.findAll();
    }

    @PostMapping
    public MovimientoInventario registrar(@RequestBody MovimientoInventario movimiento) {
        // TODO Fase 3: actualizar cantidad_actual en Inventario y registrar en Auditoria.
        return movimientoRepository.save(movimiento);
    }
}
