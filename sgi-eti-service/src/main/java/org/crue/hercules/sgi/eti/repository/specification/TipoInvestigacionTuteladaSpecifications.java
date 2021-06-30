package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada;
import org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada_;
import org.springframework.data.jpa.domain.Specification;

public class TipoInvestigacionTuteladaSpecifications {

  public static Specification<TipoInvestigacionTutelada> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoInvestigacionTutelada_.activo), Boolean.TRUE);
    };
  }

  public static Specification<TipoInvestigacionTutelada> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoInvestigacionTutelada_.activo), Boolean.FALSE);
    };
  }
}
