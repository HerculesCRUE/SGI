package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria_;
import org.springframework.data.jpa.domain.Specification;

public class TipoMemoriaSpecifications {

  public static Specification<TipoMemoria> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoMemoria_.activo), Boolean.TRUE);
    };
  }

  public static Specification<TipoMemoria> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoMemoria_.activo), Boolean.FALSE);
    };
  }
}
