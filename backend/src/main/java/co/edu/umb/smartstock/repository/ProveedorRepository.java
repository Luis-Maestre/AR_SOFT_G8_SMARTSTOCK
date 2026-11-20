package co.edu.umb.smartstock.repository;

import co.edu.umb.smartstock.model.Proveedor;
<<<<<<< HEAD
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    List<Proveedor> findByEstadoTrueOrderByNombreAsc();
    List<Proveedor> findAllByOrderByNombreAsc();
=======
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de Proveedor — capa de Persistencia (JPA / Spring Data).
 * Fase 2: interfaz vacia; Spring Data genera el CRUD basico automaticamente.
 * En Fase 3 se agregaran consultas personalizadas (ej. findByFechaVencimientoLessThan).
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
>>>>>>> d73906b8395de950b3b26a93762371c581175868
}
