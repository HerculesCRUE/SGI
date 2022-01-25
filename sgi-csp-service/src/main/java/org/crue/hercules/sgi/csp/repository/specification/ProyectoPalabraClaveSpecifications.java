package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPalabraClave;
import org.crue.hercules.sgi.csp.model.ProyectoPalabraClave_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoPalabraClaveSpecifications {

  /**
   * {@link ProyectoPalabraClave} de la entidad {@link Proyecto} con el id
   * indicado.
   * 
   * @param id identificador de la entidad {@link Proyecto}.
   * @return specification para obtener las {@link ProyectoPalabraClave} de
   *         la entidad {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoPalabraClave> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoPalabraClave_.proyectoId), id);
    };
  }
}
