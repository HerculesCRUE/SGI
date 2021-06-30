package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link EstadoRetrospectiva}.
 */

@Repository
public interface EstadoRetrospectivaRepository
    extends JpaRepository<EstadoRetrospectiva, Long>, JpaSpecificationExecutor<EstadoRetrospectiva> {

}