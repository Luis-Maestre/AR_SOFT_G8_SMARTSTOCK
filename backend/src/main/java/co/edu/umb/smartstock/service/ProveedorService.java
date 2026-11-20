package co.edu.umb.smartstock.service;

import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.exception.ApiException;
import co.edu.umb.smartstock.model.Proveedor;
import co.edu.umb.smartstock.repository.ProveedorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProveedorService {
    private final ProveedorRepository proveedores;
    private final AuditoriaService auditoria;

    public ProveedorService(ProveedorRepository proveedores, AuditoriaService auditoria) {
        this.proveedores = proveedores;
        this.auditoria = auditoria;
    }

    @Transactional(readOnly = true)
    public List<ProveedorDto> listar(boolean incluirInactivos) {
        List<Proveedor> lista = incluirInactivos ? proveedores.findAllByOrderByNombreAsc()
                : proveedores.findByEstadoTrueOrderByNombreAsc();
        return lista.stream().map(Mapeador::proveedor).toList();
    }

    @Transactional
    public ProveedorDto crear(ProveedorRequest req, Long idActor) {
        Proveedor p = new Proveedor();
        aplicar(p, req);
        p.setEstado(req.estado() == null || req.estado());
        p = proveedores.save(p);
        auditoria.registrar(idActor, "CREAR_PROVEEDOR", "proveedor", p.getIdProveedor(), p.getNombre());
        return Mapeador.proveedor(p);
    }

    @Transactional
    public ProveedorDto actualizar(Long id, ProveedorRequest req, Long idActor) {
        Proveedor p = proveedores.findById(id).orElseThrow(() -> ApiException.noEncontrado("Proveedor no encontrado"));
        aplicar(p, req);
        if (req.estado() != null) {
            p.setEstado(req.estado());
        }
        p = proveedores.save(p);
        auditoria.registrar(idActor, "EDITAR_PROVEEDOR", "proveedor", id, p.getNombre());
        return Mapeador.proveedor(p);
    }

    @Transactional
    public void desactivar(Long id, Long idActor) {
        Proveedor p = proveedores.findById(id).orElseThrow(() -> ApiException.noEncontrado("Proveedor no encontrado"));
        p.setEstado(false);
        proveedores.save(p);
        auditoria.registrar(idActor, "DESACTIVAR_PROVEEDOR", "proveedor", id, p.getNombre());
    }

    private void aplicar(Proveedor p, ProveedorRequest req) {
        p.setNombre(req.nombre().trim());
        p.setTelefono(req.telefono());
        p.setCorreo(req.correo() == null || req.correo().isBlank() ? null : req.correo().trim());
    }
}
