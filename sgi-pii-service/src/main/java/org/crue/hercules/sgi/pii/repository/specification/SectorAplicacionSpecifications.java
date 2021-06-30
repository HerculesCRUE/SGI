package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.SectorAplicacion;
import org.crue.hercules.sgi.pii.model.SectorAplicacion_;
import org.springframework.data.jpa.domain.Specification;

public class SectorAplicacionSpecifications {

  /**
   * {@link SectorAplicacion} activos.
   * 
   * @return specification para obtener los {@link SectorAplicacion} activos.
   */
  public static Specification<SectorAplicacion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(SectorAplicacion_.activo), Boolean.TRUE);
    };
  }
}
