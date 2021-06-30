package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoEntidadGestoraSpecifications {

  /**
   * {@link ProyectoEntidadGestora} de la {@link Proyecto} con el id indicado.
   * 
   * @param id identificador de la {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoEntidadGestora} de la
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoEntidadGestora> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoEntidadGestora_.proyecto).get(Proyecto_.id), id);
    };
  }

}