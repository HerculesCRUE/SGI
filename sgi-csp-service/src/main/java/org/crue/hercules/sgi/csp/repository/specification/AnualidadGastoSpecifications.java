package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.AnualidadGasto_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad_;
import org.springframework.data.jpa.domain.Specification;

public class AnualidadGastoSpecifications {

  /**
   * {@link AnualidadGasto} del {@link ProyectoAnualidad} con el id indicado.
   * 
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener los {@link AnualidadGasto} del
   *         {@link ProyectoAnualidad} con el id indicado.
   */
  public static Specification<AnualidadGasto> byProyectoAnualidadId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(AnualidadGasto_.proyectoAnualidad).get(ProyectoAnualidad_.id), id);
    };
  }

}