package co.edu.umb.smartstock.controller;

import co.edu.umb.smartstock.model.Inventario;
import co.edu.umb.smartstock.repository.InventarioRepository;
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

    @GetMapping
    public List<Inventario> listar() {
        return inventarioRepository.findAll();
    }

    // TODO Fase 3: GET /api/inventario/lotes-fefo -> lotes ordenados por fecha_vencimiento
}
