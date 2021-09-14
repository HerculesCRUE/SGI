package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.EstadoGastoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link EstadoGastoProyecto}.
 */
@Repository
public interface EstadoGastoProyectoRepository
    extends JpaRepository<EstadoGastoProyecto, Long>, JpaSpecificationExecutor<EstadoGastoProyecto> {

}
