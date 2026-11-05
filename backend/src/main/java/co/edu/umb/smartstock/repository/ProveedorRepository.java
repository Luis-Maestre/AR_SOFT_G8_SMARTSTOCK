package co.edu.umb.smartstock.repository;

import co.edu.umb.smartstock.model.Proveedor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    List<Proveedor> findByEstadoTrueOrderByNombreAsc();
    List<Proveedor> findAllByOrderByNombreAsc();
}
