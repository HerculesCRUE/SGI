package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoEntidadFinanciadoraSpecifications {

  /**
   * {@link ProyectoEntidadFinanciadora} del {@link Proyecto} con el id indicado.
   * 
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener las {@link ProyectoEntidadFinanciadora}
   *         del {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoEntidadFinanciadora> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoEntidadFinanciadora_.proyectoId), id);
    };
  }
}