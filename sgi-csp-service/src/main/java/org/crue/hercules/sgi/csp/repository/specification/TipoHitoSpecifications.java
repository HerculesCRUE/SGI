package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.model.TipoHito_;
import org.springframework.data.jpa.domain.Specification;

public class TipoHitoSpecifications {

  /**
   * {@link TipoHito} activos.
   * 
   * @return specification para obtener los {@link TipoHito} activos.
   */
  public static Specification<TipoHito> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoHito_.activo), Boolean.TRUE);
    };
  }

}