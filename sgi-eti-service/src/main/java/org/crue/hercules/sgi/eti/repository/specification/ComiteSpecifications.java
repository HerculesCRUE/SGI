package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Comite_;
import org.springframework.data.jpa.domain.Specification;

public class ComiteSpecifications {

  public static Specification<Comite> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Comite_.activo), Boolean.TRUE);
    };
  }

  public static Specification<Comite> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Comite_.activo), Boolean.FALSE);
    };
  }
}
