package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.GastoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link GastoProyecto}.
 */
@Repository
public interface GastoProyectoRepository
    extends JpaRepository<GastoProyecto, Long>, JpaSpecificationExecutor<GastoProyecto> {

}
