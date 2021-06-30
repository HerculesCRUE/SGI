package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoProrrogaSpecifications {

  /**
   * {@link ProyectoProrroga} del {@link Proyecto} con el id indicado.
   * 
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoProrroga} del
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoProrroga> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoProrroga_.proyecto).get(Proyecto_.id), id);
    };
  }

}
