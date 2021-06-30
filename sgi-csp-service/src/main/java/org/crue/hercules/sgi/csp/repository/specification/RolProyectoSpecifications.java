package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.springframework.data.jpa.domain.Specification;

public class RolProyectoSpecifications {

  /**
   * {@link RolProyecto} con Activo a True
   * 
   * @return specification para obtener las {@link RolProyecto} activas
   */
  public static Specification<RolProyecto> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(RolProyecto_.activo), Boolean.TRUE);
    };
  }

}
