package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoAreaConocimiento;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoAreaConocimiento_;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudProyectoAreaConocimientoSpecifications {

  /**
   * {@link SolicitudProyectoAreaConocimiento} de la {@link Solicitud} con el id
   * indicado.
   * 
   * @param id identificador del {@link SolicitudProyectoAreaConocimiento}.
   * @return specification para obtener los
   *         {@link SolicitudProyectoAreaConocimiento} de la {@link Solicitud} con
   *         el id indicado.
   */
  public static Specification<SolicitudProyectoAreaConocimiento> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudProyectoAreaConocimiento_.solicitudProyecto).get(SolicitudProyecto_.solicitud)
          .get(Solicitud_.id), id);
    };

  }
}
