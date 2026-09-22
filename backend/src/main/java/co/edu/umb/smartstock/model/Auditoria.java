package co.edu.umb.smartstock.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Entidad Auditoria — trazabilidad de acciones dentro del sistema. */
@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAuditoria;

    private String accion;
    private String tablaAfectada;
    private Long registroAfectado;
    private LocalDateTime fecha;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    public Long getIdAuditoria() { return idAuditoria; }
    public void setIdAuditoria(Long idAuditoria) { this.idAuditoria = idAuditoria; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
}
