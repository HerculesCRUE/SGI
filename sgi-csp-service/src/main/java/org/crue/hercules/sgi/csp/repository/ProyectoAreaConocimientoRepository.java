package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoAreaConocimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository para {@link ProyectoAreaConocimiento}.
 */

public interface ProyectoAreaConocimientoRepository
    extends JpaRepository<ProyectoAreaConocimiento, Long>, JpaSpecificationExecutor<ProyectoAreaConocimiento> {

}
