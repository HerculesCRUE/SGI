package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.SolicitudHito_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudHitoSpecifications {

  /**
   * {@link SolicitudHito} de la {@link Solicitud} con el id indicado.
   * 
   * @param id identificador de la {@link Solicitud}.
   * @return specification para obtener los {@link SolicitudHito} de la
   *         {@link Solicitud} con el id indicado.
   */
  public static Specification<SolicitudHito> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudHito_.solicitud).get(Solicitud_.id), id);
    };
  }

}
