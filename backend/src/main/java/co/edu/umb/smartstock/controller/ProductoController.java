package co.edu.umb.smartstock.controller;

import co.edu.umb.smartstock.model.Producto;
import co.edu.umb.smartstock.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * ProductoController — capa Controlador.
 * Administra productos y categorias (equivalente REST del
 * productoController.js del frontend).
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    @PostMapping
    public Producto registrar(@RequestBody Producto producto) {
        // TODO Fase 3: validaciones de negocio antes de guardar.
        return productoRepository.save(producto);
    }
}
