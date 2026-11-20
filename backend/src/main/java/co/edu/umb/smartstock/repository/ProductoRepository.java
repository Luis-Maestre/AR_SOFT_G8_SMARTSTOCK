package co.edu.umb.smartstock.repository;

import co.edu.umb.smartstock.model.Producto;
<<<<<<< HEAD
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    boolean existsByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCaseAndIdProductoNot(String codigo, Long idProducto);
    boolean existsByCategoria_IdCategoria(Long idCategoria);
    List<Producto> findByEstadoTrue();
=======
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de Producto — capa de Persistencia (JPA / Spring Data).
 * Fase 2: interfaz vacia; Spring Data genera el CRUD basico automaticamente.
 * En Fase 3 se agregaran consultas personalizadas (ej. findByFechaVencimientoLessThan).
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
>>>>>>> d73906b8395de950b3b26a93762371c581175868
}
