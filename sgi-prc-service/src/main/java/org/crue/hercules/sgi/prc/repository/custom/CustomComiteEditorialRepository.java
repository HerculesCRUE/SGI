package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.dto.ComiteEditorialResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository para {@link ComiteEditorialResumen}.
 */
public interface CustomComiteEditorialRepository {
  /**
   * Recupera todas los {@link ComiteEditorialResumen} paginadas y/o filtradas
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return Listado paginado y/o filtrado de {@link ComiteEditorialResumen}
   */
  public Page<ComiteEditorialResumen> findAllComitesEditoriales(String query, Pageable pageable);
}
