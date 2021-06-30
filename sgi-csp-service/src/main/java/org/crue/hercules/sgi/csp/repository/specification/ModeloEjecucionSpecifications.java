package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion_;
import org.springframework.data.jpa.domain.Specification;

public class ModeloEjecucionSpecifications {

  /**
   * {@link ModeloEjecucion} activos.
   * 
   * @return specification para obtener los {@link ModeloEjecucion} activos.
   */
  public static Specification<ModeloEjecucion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloEjecucion_.activo), Boolean.TRUE);
    };
  }

}
