package co.edu.umb.smartstock.repository;

import co.edu.umb.smartstock.model.Auditoria;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findAllByOrderByFechaDesc(Pageable pageable);
}
