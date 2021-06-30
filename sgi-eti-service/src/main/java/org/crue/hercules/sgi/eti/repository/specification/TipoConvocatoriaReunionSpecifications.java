package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion_;
import org.springframework.data.jpa.domain.Specification;

public class TipoConvocatoriaReunionSpecifications {

  public static Specification<TipoConvocatoriaReunion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoConvocatoriaReunion_.activo), Boolean.TRUE);
    };
  }

  public static Specification<TipoConvocatoriaReunion> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoConvocatoriaReunion_.activo), Boolean.FALSE);
    };
  }
}
