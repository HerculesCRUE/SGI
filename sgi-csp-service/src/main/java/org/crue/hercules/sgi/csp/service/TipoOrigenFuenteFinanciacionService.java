package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TipoOrigenFuenteFinanciacionService {

  /**
   * Obtiene todas las entidades {@link TipoOrigenFuenteFinanciacion} paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoOrigenFuenteFinanciacion}
   *         paginadas y filtradas.
   */
  Page<TipoOrigenFuenteFinanciacion> findAll(String query, Pageable paging);

}
