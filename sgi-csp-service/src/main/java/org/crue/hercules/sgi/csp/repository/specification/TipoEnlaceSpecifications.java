package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlace_;
import org.springframework.data.jpa.domain.Specification;

public class TipoEnlaceSpecifications {

  /**
   * {@link TipoEnlace} activos.
   * 
   * @return specification para obtener los {@link TipoEnlace} activos.
   */
  public static Specification<TipoEnlace> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoEnlace_.activo), Boolean.TRUE);
    };
  }

}