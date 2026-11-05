package co.edu.umb.smartstock.repository;

import co.edu.umb.smartstock.model.MovimientoInventario;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MovimientoInventarioRepository
        extends JpaRepository<MovimientoInventario, Long>, JpaSpecificationExecutor<MovimientoInventario> {

    List<MovimientoInventario> findByTipoMovimientoAndFechaGreaterThanEqual(String tipoMovimiento, LocalDateTime desde);

    long countByFechaGreaterThanEqual(LocalDateTime desde);

    boolean existsByProducto_IdProducto(Long idProducto);
}
