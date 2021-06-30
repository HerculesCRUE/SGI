package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAreaConocimiento;
import org.crue.hercules.sgi.csp.model.ProyectoAreaConocimiento_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoAreaConocimientoSpecifications {

  /**
   * {@link ProyectoAreaConocimiento} del {@link Proyecto} con el id indicado.
   * 
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoAreaConocimiento} de la
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoAreaConocimiento> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoAreaConocimiento_.proyecto).get(Proyecto_.id), id);
    };

  }
}
