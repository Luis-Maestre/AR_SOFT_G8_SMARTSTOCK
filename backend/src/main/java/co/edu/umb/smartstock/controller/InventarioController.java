package co.edu.umb.smartstock.controller;

<<<<<<< HEAD
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
=======
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
>>>>>>> d73906b8395de950b3b26a93762371c581175868
    }
}
