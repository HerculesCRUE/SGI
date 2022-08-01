package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.TipoRequerimiento;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.springframework.data.jpa.domain.Specification;

public class TipoRequerimientoSpecifications {

  private TipoRequerimientoSpecifications() {
  }

  /**
   * {@link TipoRequerimiento} activos.
   * 
   * @return specification para obtener los {@link TipoRequerimiento}
   *         activos.
   */
  public static Specification<TipoRequerimiento> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Activable_.activo), Boolean.TRUE);
    };
  }
}
