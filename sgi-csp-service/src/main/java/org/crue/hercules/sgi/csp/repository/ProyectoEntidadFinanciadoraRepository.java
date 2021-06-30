package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ProyectoEntidadFinanciadora}.
 */
@Repository
public interface ProyectoEntidadFinanciadoraRepository
    extends JpaRepository<ProyectoEntidadFinanciadora, Long>, JpaSpecificationExecutor<ProyectoEntidadFinanciadora> {

}
