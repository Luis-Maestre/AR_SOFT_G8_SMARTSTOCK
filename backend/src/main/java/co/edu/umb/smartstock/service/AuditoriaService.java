package co.edu.umb.smartstock.service;

import co.edu.umb.smartstock.dto.Dtos.AuditoriaDto;
import co.edu.umb.smartstock.model.Auditoria;
import co.edu.umb.smartstock.repository.AuditoriaRepository;
import co.edu.umb.smartstock.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditoriaService {
    private final AuditoriaRepository auditorias;
    private final UsuarioRepository usuarios;

    public AuditoriaService(AuditoriaRepository auditorias, UsuarioRepository usuarios) {
        this.auditorias = auditorias;
        this.usuarios = usuarios;
    }

    @Transactional
    public void registrar(Long idUsuario, String accion, String tabla, Long idRegistro, String descripcion) {
        Auditoria a = new Auditoria();
        a.setUsuario(usuarios.getReferenceById(idUsuario));
        a.setAccion(accion);
        a.setTablaAfectada(tabla);
        a.setRegistroAfectado(idRegistro);
        a.setFecha(LocalDateTime.now());
        a.setDescripcion(descripcion != null && descripcion.length() > 500 ? descripcion.substring(0, 500) : descripcion);
        auditorias.save(a);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaDto> listar(int limite) {
        int n = Math.max(1, Math.min(limite, 1000));
        return auditorias.findAllByOrderByFechaDesc(PageRequest.of(0, n)).stream().map(Mapeador::auditoria).toList();
    }
}
