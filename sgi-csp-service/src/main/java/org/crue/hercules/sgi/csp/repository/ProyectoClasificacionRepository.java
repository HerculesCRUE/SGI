package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoClasificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ProyectoClasificacion}.
 */
@Repository
public interface ProyectoClasificacionRepository
    extends JpaRepository<ProyectoClasificacion, Long>, JpaSpecificationExecutor<ProyectoClasificacion> {

}
