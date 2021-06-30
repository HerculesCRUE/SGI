package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.model.TipoComentario_;
import org.springframework.data.jpa.domain.Specification;

public class TipoComentarioSpecifications {

  public static Specification<TipoComentario> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoComentario_.activo), Boolean.TRUE);
    };
  }

  public static Specification<TipoComentario> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoComentario_.activo), Boolean.FALSE);
    };
  }
}
