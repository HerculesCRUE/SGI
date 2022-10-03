package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.dto.CongresoResumen;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CustomCongresoRepository {
  /**
   * Recupera todas los {@link CongresoResumen} paginadas y/o filtradas
   * 
   * @param specs    Specification para filtrar
   * @param pageable la información de la paginación.
   * @return Listado paginado y/o filtrado de {@link CongresoResumen}
   */
  public Page<CongresoResumen> findAllCongresos(Specification<ProduccionCientifica> specs, Pageable pageable);
}
