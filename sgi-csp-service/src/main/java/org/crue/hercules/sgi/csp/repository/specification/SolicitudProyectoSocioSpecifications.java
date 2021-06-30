package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudProyectoSocioSpecifications {
  /**
   * {@link SolicitudProyectoSocio} del {@link Solicitud} con el id indicado.
   * 
   * @param id identificador de la {@link Solicitud}.
   * @return specification para obtener los {@link SolicitudProyectoSocio} de la
   *         {@link SolicitudProyecto} con el id indicado.
   */
  public static Specification<SolicitudProyectoSocio> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(
          root.get(SolicitudProyectoSocio_.solicitudProyecto).get(SolicitudProyecto_.solicitud).get(Solicitud_.id), id);
    };
  }

}
