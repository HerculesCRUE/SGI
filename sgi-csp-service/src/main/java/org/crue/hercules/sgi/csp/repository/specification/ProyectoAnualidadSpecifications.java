package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoAnualidadSpecifications {

  /**
   * {@link ProyectoAnualidad} del {@link Proyecto} con el id indicado.
   * 
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoAnualidad} del
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoAnualidad> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoAnualidad_.proyecto).get(Proyecto_.id), id);
    };
  }

}