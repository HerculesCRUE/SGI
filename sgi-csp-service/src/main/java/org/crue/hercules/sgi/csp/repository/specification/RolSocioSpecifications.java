package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.RolSocio_;
import org.springframework.data.jpa.domain.Specification;

public class RolSocioSpecifications {

  /**
   * {@link RolSocio} con Activo a True
   * 
   * @return specification para obtener las {@link RolSocio} activas
   */
  public static Specification<RolSocio> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(RolSocio_.activo), Boolean.TRUE);
    };
  }

}
