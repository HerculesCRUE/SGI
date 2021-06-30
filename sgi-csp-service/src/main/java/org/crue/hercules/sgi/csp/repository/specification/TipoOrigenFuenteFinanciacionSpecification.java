package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion_;
import org.springframework.data.jpa.domain.Specification;

public class TipoOrigenFuenteFinanciacionSpecification {

  /**
   * {@link TipoOrigenFuenteFinanciacion} con Activo a True
   * 
   * @return specification para obtener los {@link TipoOrigenFuenteFinanciacion}
   *         activos
   */
  public static Specification<TipoOrigenFuenteFinanciacion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoOrigenFuenteFinanciacion_.activo), Boolean.TRUE);
    };
  }

}
