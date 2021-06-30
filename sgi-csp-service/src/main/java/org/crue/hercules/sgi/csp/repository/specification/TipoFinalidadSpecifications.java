package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidad_;
import org.springframework.data.jpa.domain.Specification;

public class TipoFinalidadSpecifications {

  /**
   * {@link TipoFinalidad} activos.
   * 
   * @return specification para obtener los {@link TipoFinalidad} activos.
   */
  public static Specification<TipoFinalidad> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoFinalidad_.activo), Boolean.TRUE);
    };
  }

}