package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.AgrupacionGastoConcepto;
import org.crue.hercules.sgi.csp.model.AgrupacionGastoConcepto_;
import org.crue.hercules.sgi.csp.model.ProyectoAgrupacionGasto;
import org.crue.hercules.sgi.csp.model.ProyectoAgrupacionGasto_;
import org.springframework.data.jpa.domain.Specification;

public class AgrupacionGastoConceptoSpecifications {
  /**
   * {@link AgrupacionGastoConcepto} del {@link ProyectoAgrupacionGasto} con el id
   * indicado.
   * 
   * @param id identificador del {@link ProyectoAgrupacionGasto}.
   * @return specification para obtener los {@link AgrupacionGastoConcepto} de la
   *         {@link ProyectoAgrupacionGasto} con el id indicado.
   */

  public static Specification<AgrupacionGastoConcepto> byProyectoAgrupacionGastoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(AgrupacionGastoConcepto_.proyectoAgrupacionGasto).get(ProyectoAgrupacionGasto_.id), id);
    };
  }
}
