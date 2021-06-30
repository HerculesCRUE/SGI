package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFase_;
import org.springframework.data.jpa.domain.Specification;

public class TipoFaseSpecifications {

  /**
   * {@link TipoFase} activos.
   * 
   * @return specification para obtener los {@link TipoFase} activos.
   */
  public static Specification<TipoFase> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoFase_.activo), Boolean.TRUE);
    };
  }

}