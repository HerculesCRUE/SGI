package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.springframework.data.jpa.domain.Specification;

public class FuenteFinanciacionSpecifications {

  /**
   * {@link FuenteFinanciacion} activos.
   * 
   * @return specification para obtener los {@link FuenteFinanciacion} activos.
   */
  public static Specification<FuenteFinanciacion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Activable_.activo), Boolean.TRUE);
    };
  }

}