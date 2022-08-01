package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.dto.ComiteEditorialResumen;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * Custom repository para {@link ComiteEditorialResumen}.
 */
public interface CustomComiteEditorialRepository {
  /**
   * Recupera todas los {@link ComiteEditorialResumen} paginadas y/o filtradas
   * 
   * @param specIsInvestigador filtro para investigadores.
   * @param query              la información del filtro.
   * @param pageable           la información de la paginación.
   * @return Listado paginado y/o filtrado de {@link ComiteEditorialResumen}
   */
  public Page<ComiteEditorialResumen> findAllComitesEditoriales(Specification<ProduccionCientifica> specIsInvestigador,
      String query, Pageable pageable);
}
