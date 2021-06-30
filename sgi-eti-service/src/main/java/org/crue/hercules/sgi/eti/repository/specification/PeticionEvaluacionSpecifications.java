package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion_;
import org.springframework.data.jpa.domain.Specification;

public class PeticionEvaluacionSpecifications {

  public static Specification<PeticionEvaluacion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(PeticionEvaluacion_.activo), Boolean.TRUE);
    };
  }

  public static Specification<PeticionEvaluacion> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(PeticionEvaluacion_.activo), Boolean.FALSE);
    };
  }

  public static Specification<PeticionEvaluacion> byPersonaRef(String personaRef) {
    return (root, query, cb) -> {
      return cb.equal(root.get(PeticionEvaluacion_.personaRef), personaRef);
    };
  }
}
