package co.edu.umb.smartstock.service;

import co.edu.umb.smartstock.dto.Dtos.AlertaDto;
import co.edu.umb.smartstock.exception.ApiException;
import co.edu.umb.smartstock.model.Alerta;
import co.edu.umb.smartstock.repository.AlertaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertaService {
    private final AlertaRepository alertas;
    private final AuditoriaService auditoria;

    public AlertaService(AlertaRepository alertas, AuditoriaService auditoria) {
        this.alertas = alertas;
        this.auditoria = auditoria;
    }

    @Transactional(readOnly = true)
    public List<AlertaDto> listar(String estado) {
        List<Alerta> lista = (estado == null || estado.isBlank() || "todas".equalsIgnoreCase(estado))
                ? alertas.findAllByOrderByFechaDesc()
                : alertas.findByEstadoOrderByFechaDesc(estado.toLowerCase());
        return lista.stream().map(Mapeador::alerta).toList();
    }

    @Transactional
    public AlertaDto resolver(Long id, Long idActor) {
        Alerta a = alertas.findById(id).orElseThrow(() -> ApiException.noEncontrado("Alerta no encontrada"));
        a.setEstado(Alerta.RESUELTA);
        a = alertas.save(a);
        auditoria.registrar(idActor, "RESOLVER_ALERTA", "alerta", id, a.getMensaje());
        return Mapeador.alerta(a);
    }
}
