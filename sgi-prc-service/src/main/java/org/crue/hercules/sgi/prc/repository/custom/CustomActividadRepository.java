package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.dto.ActividadResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomActividadRepository {
  /**
   * Recupera todas los {@link ActividadResumen} paginadas y/o filtradas
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return Listado paginado y/o filtrado de {@link ActividadResumen}
   */
  public Page<ActividadResumen> findAllActividades(String query, Pageable pageable);
}
