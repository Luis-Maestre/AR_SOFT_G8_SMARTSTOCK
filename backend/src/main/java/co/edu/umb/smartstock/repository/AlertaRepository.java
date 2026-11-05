package co.edu.umb.smartstock.repository;

import co.edu.umb.smartstock.model.Alerta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByEstadoOrderByFechaDesc(String estado);
    List<Alerta> findAllByOrderByFechaDesc();
    List<Alerta> findByProducto_IdProductoAndEstado(Long idProducto, String estado);
    long countByEstado(String estado);
}
