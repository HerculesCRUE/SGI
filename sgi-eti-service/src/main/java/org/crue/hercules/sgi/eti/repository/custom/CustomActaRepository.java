package org.crue.hercules.sgi.eti.repository.custom;

import org.crue.hercules.sgi.eti.dto.ActaWithNumEvaluaciones;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link ActaWithNumEvaluaciones}.
 */
@Component
public interface CustomActaRepository {

  /**
   * Devuelve una lista paginada y filtrada {@link ActaWithNumEvaluaciones}.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de {@link ActaWithNumEvaluaciones} paginadas y/o filtradas.
   */
  Page<ActaWithNumEvaluaciones> findAllActaWithNumEvaluaciones(String query, Pageable pageable);

}