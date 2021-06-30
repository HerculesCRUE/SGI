package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Asistentes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Asistentes}.
 */

@Repository
public interface AsistentesRepository extends JpaRepository<Asistentes, Long>, JpaSpecificationExecutor<Asistentes> {

  /**
   * Obtener todas las entidades paginadas {@link Asistentes} activas para una
   * determinada {@link ConvocatoriaReunion}.
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Asistentes} paginadas.
   */
  Page<Asistentes> findAllByConvocatoriaReunionId(Long id, Pageable pageable);
}