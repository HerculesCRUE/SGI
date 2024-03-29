package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.springframework.data.jpa.domain.Specification;

public class TipoRegimenConcurrenciaSpecifications {

  /**
   * {@link TipoRegimenConcurrencia} activos.
   * 
   * @return specification para obtener los {@link TipoRegimenConcurrencia}
   *         activos.
   */
  public static Specification<TipoRegimenConcurrencia> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Activable_.activo), Boolean.TRUE);
    };
  }

}