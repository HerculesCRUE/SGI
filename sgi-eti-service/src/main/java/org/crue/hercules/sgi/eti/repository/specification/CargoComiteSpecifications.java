package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.CargoComite_;
import org.springframework.data.jpa.domain.Specification;

public class CargoComiteSpecifications {

  public static Specification<CargoComite> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(CargoComite_.activo), Boolean.TRUE);
    };
  }

  public static Specification<CargoComite> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(CargoComite_.activo), Boolean.FALSE);
    };
  }
}
