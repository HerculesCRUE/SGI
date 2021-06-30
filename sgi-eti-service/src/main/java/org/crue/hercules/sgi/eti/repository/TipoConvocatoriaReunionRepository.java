package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoConvocatoriaReunion}.
 */

@Repository
public interface TipoConvocatoriaReunionRepository
    extends JpaRepository<TipoConvocatoriaReunion, Long>, JpaSpecificationExecutor<TipoConvocatoriaReunion> {

}