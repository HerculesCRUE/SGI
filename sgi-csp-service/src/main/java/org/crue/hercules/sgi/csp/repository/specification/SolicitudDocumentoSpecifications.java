package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudDocumentoSpecifications {

  /**
   * {@link SolicitudDocumento} de la {@link Solicitud} con el id indicado.
   * 
   * @param id identificador de la {@link Solicitud}.
   * @return specification para obtener los {@link SolicitudDocumento} de la
   *         {@link Solicitud} con el id indicado.
   */
  public static Specification<SolicitudDocumento> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudDocumento_.solicitud).get(Solicitud_.id), id);
    };
  }

}
