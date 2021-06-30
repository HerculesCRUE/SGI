package org.crue.hercules.sgi.eti.repository;

import java.util.List;

import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link EstadoMemoria}.
 */

@Repository
public interface EstadoMemoriaRepository
    extends JpaRepository<EstadoMemoria, Long>, JpaSpecificationExecutor<EstadoMemoria> {

  // EstadoMemoria findByMemoriaIdAndIdNotInByOrderByFechaEstadoDescLimitedTo(Long
  // idMemoria, Long idEstadoMemoria,
  // int limit);

  List<EstadoMemoria> findAllByMemoriaIdOrderByFechaEstadoDesc(Long idMemoria);

}