package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.crue.hercules.sgi.pii.model.SectorAplicacion;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SectorAplicacionSpecifications {

  /**
   * {@link SectorAplicacion} activos.
   * 
   * @return specification para obtener los {@link SectorAplicacion} activos.
   */
  public static Specification<SectorAplicacion> activos() {
    return (root, query, cb) -> cb.equal(root.get(Activable_.activo), Boolean.TRUE);
  }
}
