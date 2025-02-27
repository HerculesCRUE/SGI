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
   * @param id identificador del {@link ProyectoAnualidad}.
   * @return specification para obtener los {@link AnualidadGasto} del
   *         {@link ProyectoAnualidad} con el id indicado.
   */
  public static Specification<AnualidadGasto> byProyectoAnualidadId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(AnualidadGasto_.proyectoAnualidad).get(ProyectoAnualidad_.id), id);

  }

  /**
   * {@link AnualidadGasto} del {@link Proyecto} con el id indicado.
   * 
   * @param proyectoId identificador del {@link Proyecto}.
   * @return specification para obtener los {@link AnualidadGasto} del
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<AnualidadGasto> byProyectoId(Long proyectoId) {
    return (root, query, cb) -> cb.equal(root.get(AnualidadGasto_.proyectoAnualidad).get(ProyectoAnualidad_.proyectoId),
        proyectoId);
  }

  /**
   * {@link AnualidadGasto} con el proyectoSgeRef indicado
   * 
   * @param proyectoSgeRef identificador del proyecto en el SGE.
   * @return specification para obtener los {@link AnualidadGasto} con el
   *         proyectoSgeRef indicado
   */
  public static Specification<AnualidadGasto> byProyectoSgeRef(String proyectoSgeRef) {
    return (root, query, cb) -> cb.equal(root.get(AnualidadGasto_.proyectoSgeRef), proyectoSgeRef);
  }

}