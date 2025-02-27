package org.crue.hercules.sgi.csp.repository.specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.AnualidadGasto_;
import org.crue.hercules.sgi.csp.model.AnualidadIngreso;
import org.crue.hercules.sgi.csp.model.AnualidadIngreso_;
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

  /**
   * {@link ProyectoAnualidad} enviado al sge
   * 
   * @return specification para obtener los {@link ProyectoAnualidad} enviados al
   *         sge.
   */
  public static Specification<ProyectoAnualidad> isEnviadoSge() {
    return (root, query, cb) -> cb.isTrue(root.get(ProyectoAnualidad_.enviadoSge));
  }

  /**
   * {@link ProyectoAnualidad} con {@link AnualidadGasto} ascociados al
   * proyectoSgeRef.
   * 
   * @param proyectoSgeRef identificador del proyecto en el SGE.
   * @return specification para obtener los {@link ProyectoAnualidad} con
   *         {@link AnualidadGasto} ascociados al proyectoSgeRef.
   */
  public static Specification<ProyectoAnualidad> byAnualidadGastoProyectoSgeRef(String proyectoSgeRef) {
    return (root, query, cb) -> {
      Join<ProyectoAnualidad, AnualidadGasto> joinGastos = root.join(ProyectoAnualidad_.anualidadesGasto,
          JoinType.LEFT);

      return cb.equal(joinGastos.get(AnualidadGasto_.proyectoSgeRef), proyectoSgeRef);
    };
  }

  /**
   * {@link ProyectoAnualidad} con {@link AnualidadIngreso} ascociados al
   * proyectoSgeRef.
   * 
   * @param proyectoSgeRef identificador del proyecto en el SGE.
   * @return specification para obtener los {@link AnualidadIngreso} con
   *         {@link AnualidadGasto} ascociados al proyectoSgeRef.
   */
  public static Specification<ProyectoAnualidad> byAnualidadIngresoProyectoSgeRef(String proyectoSgeRef) {
    return (root, query, cb) -> {
      Join<ProyectoAnualidad, AnualidadIngreso> joinIngresos = root.join(ProyectoAnualidad_.anualidadesIngreso,
          JoinType.LEFT);

      return cb.equal(joinIngresos.get(AnualidadIngreso_.proyectoSgeRef), proyectoSgeRef);
    };
  }

}