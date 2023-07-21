package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.TipoFacturacion;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.springframework.data.jpa.domain.Specification;

public class TipoFacturacionSpecifications {

  /**
   * {@link TipoFacturacion} activos.
   * 
   * @return specification para obtener los {@link TipoFacturacion}
   *         activos.
   */
  public static Specification<TipoFacturacion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Activable_.activo), Boolean.TRUE);
    };
  }

}