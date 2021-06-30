package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoClasificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoClasificacion_;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudProyectoClasificacionSpecifications {

  /**
   * {@link SolicitudProyectoClasificacion} de la {@link Solicitud} con el id
   * indicado.
   * 
   * @param id identificador del {@link SolicitudProyecto}.
   * @return specification para obtener los {@link SolicitudProyectoClasificacion}
   *         de la {@link Solicitud} con el id indicado.
   */
  public static Specification<SolicitudProyectoClasificacion> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudProyectoClasificacion_.solicitudProyecto).get(SolicitudProyecto_.solicitud)
          .get(Solicitud_.id), id);
    };
  }

}