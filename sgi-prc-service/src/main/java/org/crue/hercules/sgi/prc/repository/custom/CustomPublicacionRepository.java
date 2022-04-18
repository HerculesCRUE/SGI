package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.dto.PublicacionResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository para {@link PublicacionResumen}.
 */
public interface CustomPublicacionRepository {
  /**
   * Recupera todas las {@link PublicacionResumen} con su
   * título, fecha y tipo de producción
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return Listado paginado de {@link PublicacionResumen}
   */
  Page<PublicacionResumen> findAllPublicaciones(String query, Pageable pageable);
}
