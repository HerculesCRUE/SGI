package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.LineaInvestigacion;
import org.crue.hercules.sgi.csp.model.LineaInvestigacion_;
import org.springframework.data.jpa.domain.Specification;

public class LineaInvestigacionSpecifications {

  /**
   * {@link LineaInvestigacion} activos.
   * 
   * @return specification para obtener los {@link LineaInvestigacion} activos.
   */
  public static Specification<LineaInvestigacion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(LineaInvestigacion_.activo), Boolean.TRUE);
    };
  }

}