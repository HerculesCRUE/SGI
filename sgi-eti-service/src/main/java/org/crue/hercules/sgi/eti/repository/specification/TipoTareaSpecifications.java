package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.crue.hercules.sgi.eti.model.TipoTarea_;
import org.springframework.data.jpa.domain.Specification;

public class TipoTareaSpecifications {

  public static Specification<TipoTarea> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoTarea_.activo), Boolean.TRUE);
    };
  }

  public static Specification<TipoTarea> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoTarea_.activo), Boolean.FALSE);
    };
  }
}
