package org.crue.hercules.sgi.eti.repository.specification;

import java.util.List;

import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Bloque_;
import org.crue.hercules.sgi.eti.model.Formulario_;
import org.springframework.data.jpa.domain.Specification;

public class BloqueSpecifications {

  public static Specification<Bloque> formularioIdsIn(List<Long> formulariosIds) {
    return (root, query, cb) -> {
      return (root.get(Bloque_.formulario).get(Formulario_.id)).in(formulariosIds);
    };
  }
}
