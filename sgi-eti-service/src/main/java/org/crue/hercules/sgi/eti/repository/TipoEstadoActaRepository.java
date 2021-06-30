package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoEstadoActa}.
 */

@Repository
public interface TipoEstadoActaRepository
    extends JpaRepository<TipoEstadoActa, Long>, JpaSpecificationExecutor<TipoEstadoActa> {

}