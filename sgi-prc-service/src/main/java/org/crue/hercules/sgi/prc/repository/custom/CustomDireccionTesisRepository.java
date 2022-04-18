package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.dto.DireccionTesisResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomDireccionTesisRepository {
  /**
   * Recupera todas los {@link DireccionTesisResumen} paginadas y/o filtradas
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return Listado paginado y/o filtrado de {@link DireccionTesisResumen}
   */
  public Page<DireccionTesisResumen> findAllDireccionesTesis(String query, Pageable pageable);
}
