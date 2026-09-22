package co.edu.umb.smartstock.controller;

import co.edu.umb.smartstock.model.Reposicion;
import co.edu.umb.smartstock.repository.ReposicionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * ReposicionController — capa Controlador.
 * Gestiona las solicitudes y sugerencias de reposicion.
 */
@RestController
@RequestMapping("/api/reposiciones")
public class ReposicionController {

    @Autowired
    private ReposicionRepository reposicionRepository;

    @GetMapping
    public List<Reposicion> listar() {
        return reposicionRepository.findAll();
    }

    // TODO Fase 3: integrar con el microservicio Python de reposicion inteligente.
}
