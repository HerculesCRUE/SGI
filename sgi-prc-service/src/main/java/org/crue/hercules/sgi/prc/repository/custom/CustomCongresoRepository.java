package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.dto.CongresoResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomCongresoRepository {
  /**
   * Recupera todas los {@link CongresoResumen} paginadas y/o filtradas
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return Listado paginado y/o filtrado de {@link CongresoResumen}
   */
  public Page<CongresoResumen> findAllCongresos(String query, Pageable pageable);
}
