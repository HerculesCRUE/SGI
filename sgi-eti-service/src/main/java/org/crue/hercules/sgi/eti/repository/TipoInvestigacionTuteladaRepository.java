package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoInvestigacionTutelada}.
 */

@Repository
public interface TipoInvestigacionTuteladaRepository
    extends JpaRepository<TipoInvestigacionTutelada, Long>, JpaSpecificationExecutor<TipoInvestigacionTutelada> {

}