package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.ViaProteccion;
import org.crue.hercules.sgi.pii.model.ViaProteccion_;
import org.springframework.data.jpa.domain.Specification;

public class ViaProteccionSpecifications {

  /**
   * {@link ViaProteccion} activos.
   * 
   * @return Specification para obtener los {@link ViaProteccion} activos.
   */
  public static Specification<ViaProteccion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ViaProteccion_.activo), Boolean.TRUE);
    };
  }

}
