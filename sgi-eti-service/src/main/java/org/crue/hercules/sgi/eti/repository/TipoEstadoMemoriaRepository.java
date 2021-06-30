package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoEstadoMemoria}.
 */

@Repository
public interface TipoEstadoMemoriaRepository
    extends JpaRepository<TipoEstadoMemoria, Long>, JpaSpecificationExecutor<TipoEstadoMemoria> {

}