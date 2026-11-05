package co.edu.umb.smartstock.repository;

import co.edu.umb.smartstock.model.Reposicion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReposicionRepository extends JpaRepository<Reposicion, Long> {
    List<Reposicion> findAllByOrderByFechaDesc();
    List<Reposicion> findByEstadoOrderByFechaDesc(String estado);
    Optional<Reposicion> findFirstByProducto_IdProductoAndEstado(Long idProducto, String estado);
    long countByEstado(String estado);
}
