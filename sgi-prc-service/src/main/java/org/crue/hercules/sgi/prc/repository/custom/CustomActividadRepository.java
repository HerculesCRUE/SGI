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
   * @param specs    Specification para filtrar.
   * @param pageable la información de la paginación.
   * @return Listado paginado y/o filtrado de {@link ActividadResumen}
   */
  public Page<ActividadResumen> findAllActividades(Specification<ProduccionCientifica> specs, Pageable pageable);
}
