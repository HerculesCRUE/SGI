package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudPalabraClave;
import org.crue.hercules.sgi.csp.model.SolicitudPalabraClave_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudPalabraClaveSpecifications {

  /**
   * {@link SolicitudPalabraClave} de la entidad {@link Solicitud} con el id
   * indicado.
   * 
   * @param id identificador de la entidad {@link Solicitud}.
   * @return specification para obtener las {@link SolicitudPalabraClave} de
   *         la entidad {@link Solicitud} con el id indicado.
   */
  public static Specification<SolicitudPalabraClave> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudPalabraClave_.solicitudId), id);
    };
  }
}
