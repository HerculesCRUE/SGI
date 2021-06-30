package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaReunionSpecifications {

  public static Specification<ConvocatoriaReunion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaReunion_.activo), Boolean.TRUE);
    };
  }

  public static Specification<ConvocatoriaReunion> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaReunion_.activo), Boolean.FALSE);
    };
  }

  public static Specification<ConvocatoriaReunion> byId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaReunion_.id), id);
    };
  }
}
