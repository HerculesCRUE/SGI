package org.crue.hercules.sgi.csp.repository.specification;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.AgrupacionGastoConcepto;
import org.crue.hercules.sgi.csp.model.AgrupacionGastoConcepto_;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConceptoGasto_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAgrupacionGasto_;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConceptoGastoSpecifications {

  /**
   * {@link ConceptoGasto} activos.
   * 
   * @return specification para obtener los {@link ConceptoGasto} activos.
   */
  public static Specification<ConceptoGasto> activos() {
    return (root, query, cb) -> cb.equal(root.get(ConceptoGasto_.activo), Boolean.TRUE);
  }

  /**
   * Devuelve todos los {@link ConceptoGasto} que no esten asignados a alguna
   * {@link AgrupacionGastoConcepto} del {@link Proyecto}.
   * 
   * @param proyectoId Identificador de {@link Proyecto}.
   * @return specification para obtener los {@link ConceptoGasto} no asignados a
   *         las {@link AgrupacionGastoConcepto} del {@link Proyecto}.
   */
  public static Specification<ConceptoGasto> notInProyectoAgrupacionGasto(Long proyectoId) {
    return (root, query, cb) -> {
      Subquery<Long> queryConceptosAgrupacion = query.subquery(Long.class);
      Root<AgrupacionGastoConcepto> subqRoot = queryConceptosAgrupacion.from(AgrupacionGastoConcepto.class);
      queryConceptosAgrupacion.select(subqRoot.get(AgrupacionGastoConcepto_.conceptoGasto).get(ConceptoGasto_.id))
          .where(cb.equal(
              subqRoot.get(AgrupacionGastoConcepto_.proyectoAgrupacionGasto).get(ProyectoAgrupacionGasto_.proyectoId),
              proyectoId));
      return root.get(ConceptoGasto_.id).in(queryConceptosAgrupacion).not();
    };
  }

  /**
   * Devuelve todos los {@link ConceptoGasto} permitidos en el {@link Proyecto}.
   * 
   * @param proyectoId Identificador de {@link Proyecto}.
   * @return specification para obtener los {@link ConceptoGasto} permitidos en un
   *         {@link Proyecto}.
   */
  public static Specification<ConceptoGasto> byPermitidosInProyecto(Long proyectoId) {
    return (root, query, cb) -> {
      Subquery<Long> queryConceptosProyecto = query.subquery(Long.class);
      Root<ProyectoConceptoGasto> subqRoot = queryConceptosProyecto.from(ProyectoConceptoGasto.class);
      queryConceptosProyecto
          .select(subqRoot.get(ProyectoConceptoGasto_.conceptoGastoId))
          .distinct(true)
          .where(cb.and(
              cb.equal(subqRoot.get(ProyectoConceptoGasto_.proyectoId), proyectoId),
              cb.isTrue(subqRoot.get(ProyectoConceptoGasto_.permitido))));
      return root.get(ConceptoGasto_.id).in(queryConceptosProyecto);
    };
  }

}
