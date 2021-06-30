package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo_;
import org.crue.hercules.sgi.csp.model.ProyectoSocio_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoSocioEquipoSpecifications {

  /**
   * {@link ProyectoSocioEquipo} de la {@link ProyectoSocio} con el id indicado.
   * 
   * @param id identificador de la {@link ProyectoSocio}.
   * @return specification para obtener los {@link ProyectoSocioEquipo} de la
   *         {@link ProyectoSocio} con el id indicado.
   */
  public static Specification<ProyectoSocioEquipo> byProyectoSocioId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoSocioEquipo_.proyectoSocio).get(ProyectoSocio_.id), id);
    };
  }

}
