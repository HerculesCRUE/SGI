package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa_;
import org.springframework.data.jpa.domain.Specification;

public class TipoEstadoActaSpecifications {

  public static Specification<TipoEstadoActa> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoEstadoActa_.activo), Boolean.TRUE);
    };
  }

  public static Specification<TipoEstadoActa> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoEstadoActa_.activo), Boolean.FALSE);
    };
  }
}
