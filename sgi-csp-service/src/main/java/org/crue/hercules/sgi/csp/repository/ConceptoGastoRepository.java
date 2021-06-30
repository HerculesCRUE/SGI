package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ConceptoGasto}.
 */
@Repository
public interface ConceptoGastoRepository
    extends JpaRepository<ConceptoGasto, Long>, JpaSpecificationExecutor<ConceptoGasto> {

  /**
   * Obtiene la entidad {@link ConceptoGasto} activa con el nombre indicado
   *
   * @param nombre el nombre de {@link ConceptoGasto}.
   * @return el {@link ConceptoGasto} con el nombre indicado
   */
  Optional<ConceptoGasto> findByNombreAndActivoIsTrue(String nombre);

}
