package co.edu.umb.smartstock.repository;

import co.edu.umb.smartstock.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de Lote — capa de Persistencia (JPA / Spring Data).
 * Fase 2: interfaz vacia; Spring Data genera el CRUD basico automaticamente.
 * En Fase 3 se agregaran consultas personalizadas (ej. findByFechaVencimientoLessThan).
 */
@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
}
