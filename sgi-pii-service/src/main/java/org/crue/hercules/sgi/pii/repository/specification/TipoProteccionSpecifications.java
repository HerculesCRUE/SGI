package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.crue.hercules.sgi.pii.model.TipoProteccion_;
import org.springframework.data.jpa.domain.Specification;

public class TipoProteccionSpecifications {
  /**
   * {@link TipoProteccion} activos.
   * 
   * @return Specification para obtener los {@link TipoProteccion} activos.
   */
  public static Specification<TipoProteccion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoProteccion_.activo), Boolean.TRUE);
    };
  }

}
