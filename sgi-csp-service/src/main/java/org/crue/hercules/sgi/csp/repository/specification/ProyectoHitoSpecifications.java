package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoHito_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoHitoSpecifications {

  /**
   * {@link ProyectoHito} del {@link Proyecto} con el id indicado.
   * 
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoHito} del
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoHito> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoHito_.proyecto).get(Proyecto_.id), id);
    };
  }

}
