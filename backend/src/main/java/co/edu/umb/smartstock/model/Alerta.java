package co.edu.umb.smartstock.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Entidad Alerta — generada por stock minimo o proximidad de vencimiento. */
@Entity
@Table(name = "alerta")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlerta;

    private String tipo; // stock_minimo | proximo_vencer
    private String mensaje;
    private LocalDateTime fecha;
    private String estado;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    public Long getIdAlerta() { return idAlerta; }
    public void setIdAlerta(Long idAlerta) { this.idAlerta = idAlerta; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
