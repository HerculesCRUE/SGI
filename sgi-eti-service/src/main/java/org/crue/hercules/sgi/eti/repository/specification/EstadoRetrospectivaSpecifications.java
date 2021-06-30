package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva_;
import org.springframework.data.jpa.domain.Specification;

public class EstadoRetrospectivaSpecifications {

  public static Specification<EstadoRetrospectiva> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(EstadoRetrospectiva_.activo), Boolean.TRUE);
    };
  }

  public static Specification<EstadoRetrospectiva> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(EstadoRetrospectiva_.activo), Boolean.FALSE);
    };
  }
}
