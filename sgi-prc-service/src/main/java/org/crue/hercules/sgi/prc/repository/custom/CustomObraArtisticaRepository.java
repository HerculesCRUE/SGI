package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.dto.ObraArtisticaResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomObraArtisticaRepository {
  /**
   * Recupera todas los {@link ObraArtisticaResumen} paginadas y/o filtradas
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return Listado paginado y/o filtrado de {@link ObraArtisticaResumen}
   */
  public Page<ObraArtisticaResumen> findAllObrasArtisticas(String query, Pageable pageable);
}
