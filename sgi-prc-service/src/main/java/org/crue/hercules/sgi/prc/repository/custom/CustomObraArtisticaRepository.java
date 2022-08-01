package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.dto.ObraArtisticaResumen;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CustomObraArtisticaRepository {
  /**
   * Recupera todas los {@link ObraArtisticaResumen} paginadas y/o filtradas
   * 
   * @param specIsInvestigador Specification para filtrar para el investigador
   * @param query              la información del filtro.
   * @param pageable           la información de la paginación.
   * @return Listado paginado y/o filtrado de {@link ObraArtisticaResumen}
   */
  public Page<ObraArtisticaResumen> findAllObrasArtisticas(Specification<ProduccionCientifica> specIsInvestigador,
      String query, Pageable pageable);
}
