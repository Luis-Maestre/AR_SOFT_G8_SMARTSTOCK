package co.edu.umb.smartstock.repository;

import co.edu.umb.smartstock.model.MovimientoInventario;
<<<<<<< HEAD
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MovimientoInventarioRepository
        extends JpaRepository<MovimientoInventario, Long>, JpaSpecificationExecutor<MovimientoInventario> {

    List<MovimientoInventario> findByTipoMovimientoAndFechaGreaterThanEqual(String tipoMovimiento, LocalDateTime desde);

    long countByFechaGreaterThanEqual(LocalDateTime desde);

    boolean existsByProducto_IdProducto(Long idProducto);
=======
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de MovimientoInventario — capa de Persistencia (JPA / Spring Data).
 * Fase 2: interfaz vacia; Spring Data genera el CRUD basico automaticamente.
 * En Fase 3 se agregaran consultas personalizadas (ej. findByFechaVencimientoLessThan).
 */
@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
>>>>>>> d73906b8395de950b3b26a93762371c581175868
}
