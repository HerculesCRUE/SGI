package org.crue.hercules.sgi.csp.repository.specification;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.AgrupacionGastoConcepto;
import org.crue.hercules.sgi.csp.model.AgrupacionGastoConcepto_;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConceptoGasto_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAgrupacionGasto_;
import org.springframework.data.jpa.domain.Specification;

public class ConceptoGastoSpecifications {

  /**
   * {@link ConceptoGasto} activos.
   * 
   * @return specification para obtener los {@link ConceptoGasto} activos.
   */
  public static Specification<ConceptoGasto> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConceptoGasto_.activo), Boolean.TRUE);
    };
  }

  /**
   * Devuelve todos los {@link ConceptoGasto} que no esten asignados a la
   * {@link AgrupacionGastoConcepto}
   * 
   * @param proyectoId Identificador del proyecto.
   * @return specification para obtener los {@link ConceptoGasto} de la
   *         {@link Proyecto} con el id indicado.
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
}
