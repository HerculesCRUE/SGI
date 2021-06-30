package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion_;
import org.springframework.data.jpa.domain.Specification;

public class TipoFinanciacionSpecifications {

  /**
   * {@link TipoFinanciacion} activos.
   * 
   * @return specification para obtener los {@link TipoFinanciacion} activos.
   */
  public static Specification<TipoFinanciacion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoFinanciacion_.activo), Boolean.TRUE);
    };
  }

}