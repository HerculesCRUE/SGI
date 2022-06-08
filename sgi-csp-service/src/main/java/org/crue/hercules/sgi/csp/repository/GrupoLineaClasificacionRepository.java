package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.GrupoLineaClasificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link GrupoLineaClasificacion}.
 */
@Repository
public interface GrupoLineaClasificacionRepository
    extends JpaRepository<GrupoLineaClasificacion, Long>, JpaSpecificationExecutor<GrupoLineaClasificacion> {

}
