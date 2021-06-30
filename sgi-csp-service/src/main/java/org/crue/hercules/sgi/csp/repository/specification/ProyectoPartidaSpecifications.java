package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPartida;
import org.crue.hercules.sgi.csp.model.ProyectoPartida_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoPartidaSpecifications {

  /**
   * {@link ProyectoPartida} del {@link Proyecto} con el id indicado.
   * 
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoPartida} de la
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoPartida> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoPartida_.proyecto).get(Proyecto_.id), id);
    };
  }

}