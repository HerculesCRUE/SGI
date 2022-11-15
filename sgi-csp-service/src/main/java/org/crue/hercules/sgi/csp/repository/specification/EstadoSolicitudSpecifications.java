package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud_;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EstadoSolicitudSpecifications {

  /**
   * {@link EstadoSolicitud} de la {@link Solicitud} con el id indicado.
   * 
   * @param id identificador de la {@link Solicitud}.
   * @return specification para obtener los {@link EstadoSolicitud} de la
   *         {@link Solicitud} con el id indicado.
   */
  public static Specification<EstadoSolicitud> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(EstadoSolicitud_.solicitud).get(Solicitud_.id), id);
    };
  }

}
