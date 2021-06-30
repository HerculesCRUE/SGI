package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoEntidadConvocanteSpecifications {

  /**
   * {@link ProyectoEntidadConvocante} de la {@link Proyecto} con el id indicado.
   * 
   * @param id identificador de la {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoEntidadConvocante} de
   *         la {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoEntidadConvocante> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoEntidadConvocante_.proyectoId), id);
    };
  }

}