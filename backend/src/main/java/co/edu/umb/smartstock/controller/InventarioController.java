package co.edu.umb.smartstock.controller;

import co.edu.umb.smartstock.model.Inventario;
import co.edu.umb.smartstock.model.Lote;
import co.edu.umb.smartstock.repository.InventarioRepository;
import co.edu.umb.smartstock.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * InventarioController — capa Controlador.
 * Gestiona existencias, ubicaciones y el control de lotes (FEFO).
 */
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private LoteRepository loteRepository;

    @GetMapping
    public List<Inventario> listar() {
        return inventarioRepository.findAll();
    }

    /**
     * Politica FEFO (primero en vencer, primero en salir):
     * devuelve los lotes con existencias, ordenados por fecha de vencimiento.
     * El primero de la lista es el que se debe despachar primero.
     */
    @GetMapping("/lotes-fefo")
    public List<Lote> lotesFefo() {
        return loteRepository.findByCantidadGreaterThanOrderByFechaVencimientoAsc(0);
    }
}
