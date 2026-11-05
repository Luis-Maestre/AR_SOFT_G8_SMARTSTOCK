package co.edu.umb.smartstock.repository;

import co.edu.umb.smartstock.model.Inventario;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByProducto_IdProducto(Long idProducto);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventario i where i.producto.idProducto = :id")
    Optional<Inventario> buscarParaActualizar(@Param("id") Long id);
}
