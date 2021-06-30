package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica_;
import org.springframework.data.jpa.domain.Specification;

public class FormacionEspecificaSpecifications {

  public static Specification<FormacionEspecifica> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(FormacionEspecifica_.activo), Boolean.TRUE);
    };
  }

  public static Specification<FormacionEspecifica> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(FormacionEspecifica_.activo), Boolean.FALSE);
    };
  }
}
