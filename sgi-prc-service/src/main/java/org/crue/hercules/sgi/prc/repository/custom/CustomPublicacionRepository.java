package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.dto.PublicacionResumen;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * Custom repository para {@link PublicacionResumen}.
 */
public interface CustomPublicacionRepository {
  /**
   * Recupera todas las {@link PublicacionResumen} con su
   * título, fecha y tipo de producción
   * 
   * @param specIsInvestigador filtro para investigadores.
   * @param query              la información del filtro.
   * @param pageable           la información de la paginación.
   * @return Listado paginado de {@link PublicacionResumen}
   */
  Page<PublicacionResumen> findAllPublicaciones(Specification<ProduccionCientifica> specIsInvestigador, String query,
      Pageable pageable);
}
