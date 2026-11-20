package co.edu.umb.smartstock.repository;

import co.edu.umb.smartstock.model.Alerta;
<<<<<<< HEAD
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByEstadoOrderByFechaDesc(String estado);
    List<Alerta> findAllByOrderByFechaDesc();
    List<Alerta> findByProducto_IdProductoAndEstado(Long idProducto, String estado);
    long countByEstado(String estado);
=======
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de Alerta — capa de Persistencia (JPA / Spring Data).
 * Fase 2: interfaz vacia; Spring Data genera el CRUD basico automaticamente.
 * En Fase 3 se agregaran consultas personalizadas (ej. findByFechaVencimientoLessThan).
 */
@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {
>>>>>>> d73906b8395de950b3b26a93762371c581175868
}
