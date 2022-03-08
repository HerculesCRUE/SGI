package org.crue.hercules.sgi.prc.repository;

import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.repository.custom.CustomEstadoProduccionCientificaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link EstadoProduccionCientifica}.
 */

@Repository
public interface EstadoProduccionCientificaRepository
    extends JpaRepository<EstadoProduccionCientifica, Long>, JpaSpecificationExecutor<EstadoProduccionCientifica>,
    CustomEstadoProduccionCientificaRepository {

}
