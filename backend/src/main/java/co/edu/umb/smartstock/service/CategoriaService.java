package co.edu.umb.smartstock.service;

import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.exception.ApiException;
import co.edu.umb.smartstock.model.Categoria;
import co.edu.umb.smartstock.repository.CategoriaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {
    private final CategoriaRepository categorias;
    private final AuditoriaService auditoria;

    public CategoriaService(CategoriaRepository categorias, AuditoriaService auditoria) {
        this.categorias = categorias;
        this.auditoria = auditoria;
    }

    @Transactional(readOnly = true)
    public List<CategoriaDto> listar(boolean incluirInactivas) {
        List<Categoria> lista = incluirInactivas ? categorias.findAllByOrderByNombreAsc()
                : categorias.findByEstadoTrueOrderByNombreAsc();
        return lista.stream().map(Mapeador::categoria).toList();
    }

    @Transactional
    public CategoriaDto crear(CategoriaRequest req, Long idActor) {
        String nombre = req.nombre().trim();
        if (categorias.existsByNombreIgnoreCase(nombre)) {
            throw ApiException.conflicto("Ya existe una categoría con ese nombre");
        }
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setDescripcion(req.descripcion());
        c.setEstado(req.estado() == null || req.estado());
        c = categorias.save(c);
        auditoria.registrar(idActor, "CREAR_CATEGORIA", "categoria", c.getIdCategoria(), c.getNombre());
        return Mapeador.categoria(c);
    }

    @Transactional
    public CategoriaDto actualizar(Long id, CategoriaRequest req, Long idActor) {
        Categoria c = categorias.findById(id).orElseThrow(() -> ApiException.noEncontrado("Categoría no encontrada"));
        String nombre = req.nombre().trim();
        if (categorias.existsByNombreIgnoreCaseAndIdCategoriaNot(nombre, id)) {
            throw ApiException.conflicto("Ya existe una categoría con ese nombre");
        }
        c.setNombre(nombre);
        c.setDescripcion(req.descripcion());
        if (req.estado() != null) {
            c.setEstado(req.estado());
        }
        c = categorias.save(c);
        auditoria.registrar(idActor, "EDITAR_CATEGORIA", "categoria", id, c.getNombre());
        return Mapeador.categoria(c);
    }

    @Transactional
    public void desactivar(Long id, Long idActor) {
        Categoria c = categorias.findById(id).orElseThrow(() -> ApiException.noEncontrado("Categoría no encontrada"));
        c.setEstado(false);
        categorias.save(c);
        auditoria.registrar(idActor, "DESACTIVAR_CATEGORIA", "categoria", id, c.getNombre());
    }
}
