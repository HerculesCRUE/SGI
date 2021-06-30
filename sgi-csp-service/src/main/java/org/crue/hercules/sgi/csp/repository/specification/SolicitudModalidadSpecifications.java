package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudModalidadSpecifications {

  /**
   * {@link SolicitudModalidad} de la {@link Solicitud} con el id indicado.
   * 
   * @param id identificador de la {@link Solicitud}.
   * @return specification para obtener los {@link SolicitudModalidad} de la
   *         {@link Solicitud} con el id indicado.
   */
  public static Specification<SolicitudModalidad> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudModalidad_.solicitud).get(Solicitud_.id), id);
    };
  }

}
