package org.crue.hercules.sgi.prc.repository;

import org.crue.hercules.sgi.prc.model.IndiceExperimentalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link IndiceExperimentalidad}.
 */

@Repository
public interface IndiceExperimentalidadRepository
    extends JpaRepository<IndiceExperimentalidad, Long>, JpaSpecificationExecutor<IndiceExperimentalidad> {

}
