package co.edu.umb.smartstock.repository;

import co.edu.umb.smartstock.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio de Lote — capa de Persistencia (JPA / Spring Data).
 * Spring Data genera el CRUD basico automaticamente.
 */
@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    /** Lotes con existencias, del que vence primero al que vence despues (FEFO). */
    List<Lote> findByCantidadGreaterThanOrderByFechaVencimientoAsc(Integer cantidad);
}
