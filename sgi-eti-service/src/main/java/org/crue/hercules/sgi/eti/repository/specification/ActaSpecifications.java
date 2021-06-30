package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Acta_;
import org.springframework.data.jpa.domain.Specification;

public class ActaSpecifications {

  public static Specification<Acta> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Acta_.activo), Boolean.TRUE);
    };
  }

  public static Specification<Acta> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Acta_.activo), Boolean.FALSE);
    };
  }
}
