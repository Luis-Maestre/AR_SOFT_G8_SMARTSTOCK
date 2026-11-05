package co.edu.umb.smartstock.repository;

import co.edu.umb.smartstock.model.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    boolean existsByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCaseAndIdProductoNot(String codigo, Long idProducto);
    boolean existsByCategoria_IdCategoria(Long idCategoria);
    List<Producto> findByEstadoTrue();
}
