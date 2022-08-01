package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.dto.ActividadResumen;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CustomActividadRepository {
  /**
   * Recupera todas los {@link ActividadResumen} paginadas y/o filtradas
   * 
   * @param specIsInvestigador filtro para investigadores.
   * @param query              la información del filtro.
   * @param pageable           la información de la paginación.
   * @return Listado paginado y/o filtrado de {@link ActividadResumen}
   */
  public Page<ActividadResumen> findAllActividades(Specification<ProduccionCientifica> specIsInvestigador, String query,
      Pageable pageable);
}
