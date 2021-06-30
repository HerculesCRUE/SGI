package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria_;
import org.springframework.data.jpa.domain.Specification;

public class TipoEstadoMemoriaSpecifications {

  public static Specification<TipoEstadoMemoria> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoEstadoMemoria_.activo), Boolean.TRUE);
    };
  }

  public static Specification<TipoEstadoMemoria> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoEstadoMemoria_.activo), Boolean.FALSE);
    };
  }
}
