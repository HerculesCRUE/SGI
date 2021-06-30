package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoPaqueteTrabajoSpecifications {

  /**
   * {@link ProyectoPaqueteTrabajo} del {@link Proyecto} con el id indicado.
   * 
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoPaqueteTrabajo} del
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoPaqueteTrabajo> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoPaqueteTrabajo_.proyecto).get(Proyecto_.id), id);
    };
  }
}
