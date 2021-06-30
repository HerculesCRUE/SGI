package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.Dictamen_;
import org.springframework.data.jpa.domain.Specification;

public class DictamenSpecifications {

  public static Specification<Dictamen> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Dictamen_.activo), Boolean.TRUE);
    };
  }

  public static Specification<Dictamen> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Dictamen_.activo), Boolean.FALSE);
    };
  }
}
