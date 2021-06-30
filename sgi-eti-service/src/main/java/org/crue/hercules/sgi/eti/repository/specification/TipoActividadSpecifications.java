package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoActividad_;
import org.springframework.data.jpa.domain.Specification;

public class TipoActividadSpecifications {

  public static Specification<TipoActividad> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoActividad_.activo), Boolean.TRUE);
    };
  }

  public static Specification<TipoActividad> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoActividad_.activo), Boolean.FALSE);
    };
  }
}
