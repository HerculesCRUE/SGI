package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico_;
import org.springframework.data.jpa.domain.Specification;

public class TipoAmbitoGeograficoSpecifications {

  /**
   * {@link TipoAmbitoGeografico} activos.
   * 
   * @return specification para obtener los {@link TipoAmbitoGeografico} activos.
   */
  public static Specification<TipoAmbitoGeografico> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoAmbitoGeografico_.activo), Boolean.TRUE);
    };
  }

}