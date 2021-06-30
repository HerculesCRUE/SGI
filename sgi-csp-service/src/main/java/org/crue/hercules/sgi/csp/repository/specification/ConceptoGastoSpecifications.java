package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConceptoGasto_;
import org.springframework.data.jpa.domain.Specification;

public class ConceptoGastoSpecifications {

  /**
   * {@link ConceptoGasto} activos.
   * 
   * @return specification para obtener los {@link ConceptoGasto} activos.
   */
  public static Specification<ConceptoGasto> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConceptoGasto_.activo), Boolean.TRUE);
    };
  }

}