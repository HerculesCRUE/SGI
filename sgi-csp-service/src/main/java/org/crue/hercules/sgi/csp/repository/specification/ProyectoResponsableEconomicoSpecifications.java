package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoResponsableEconomicoSpecifications {

  /**
   * {@link ProyectoResponsableEconomico} del {@link Proyecto} con el id indicado.
   * 
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoResponsableEconomico}
   *         del {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoResponsableEconomico> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoResponsableEconomico_.proyecto).get(Proyecto_.id), id);
    };
  }

}