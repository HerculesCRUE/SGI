package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.EstadoActa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link EstadoActa}.
 */
@Repository
public interface EstadoActaRepository extends JpaRepository<EstadoActa, Long>, JpaSpecificationExecutor<EstadoActa> {

}