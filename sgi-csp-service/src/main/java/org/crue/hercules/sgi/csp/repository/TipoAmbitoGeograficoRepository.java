package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoAmbitoGeografico}.
 */
@Repository
public interface TipoAmbitoGeograficoRepository
    extends JpaRepository<TipoAmbitoGeografico, Long>, JpaSpecificationExecutor<TipoAmbitoGeografico> {

  /**
   * Obtiene la entidad {@link TipoAmbitoGeografico} con el nombre indicado
   *
   * @param nombre el nombre de {@link TipoAmbitoGeografico}.
   * @return el {@link TipoAmbitoGeografico} con el nombre indicado
   */
  Optional<TipoAmbitoGeografico> findByNombre(String nombre);

}
