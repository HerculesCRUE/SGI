package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoIVA;
import org.crue.hercules.sgi.csp.model.ProyectoIVA_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoIVASpecifications {

  /**
   * {@link ProyectoIVA} del {@link Proyecto} con el id indicado.
   * 
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoIVA} del
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoIVA> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoIVA_.proyecto).get(Proyecto_.id), id);
    };
  }

}
