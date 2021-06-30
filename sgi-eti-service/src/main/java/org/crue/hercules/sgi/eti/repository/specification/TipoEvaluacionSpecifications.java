package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion_;
import org.springframework.data.jpa.domain.Specification;

public class TipoEvaluacionSpecifications {

  public static Specification<TipoEvaluacion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoEvaluacion_.activo), Boolean.TRUE);
    };
  }

  public static Specification<TipoEvaluacion> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoEvaluacion_.activo), Boolean.FALSE);
    };
  }
}
