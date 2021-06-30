package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.Informe_;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.springframework.data.jpa.domain.Specification;

public class InformeSpecifications {

  public static Specification<Informe> byMemoriaOrderByVersionDesc(Long idMemoria) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Informe_.memoria).get(Memoria_.id), idMemoria);
    };
  }

}
