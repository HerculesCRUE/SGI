package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.ConflictoInteres;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ConflictoInteres}.
 */

@Repository
public interface ConflictoInteresRepository
    extends JpaRepository<ConflictoInteres, Long>, JpaSpecificationExecutor<ConflictoInteres> {

  /**
   * Obtiene todas las entidades paginadas {@link ConflictoInteres} para un
   * determinado {@link Evaluador}.
   *
   * @param id       Id de {@link Evaluador}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link ConflictoInteres} paginadas.
   */
  Page<ConflictoInteres> findAllByEvaluadorId(Long id, Pageable pageable);

}