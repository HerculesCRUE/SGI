package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoClasificacion;
import org.crue.hercules.sgi.csp.model.ProyectoClasificacion_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProyectoClasificacionSpecifications {

  /**
   * {@link ProyectoClasificacion} del {@link Proyecto} con el id indicado.
   * 
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoClasificacion} de la
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoClasificacion> byProyectoId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoClasificacion_.proyecto).get(Proyecto_.id), id);
  }

}