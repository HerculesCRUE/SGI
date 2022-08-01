package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SolicitudProyectoSpecifications {

  /**
   * {@link SolicitudProyecto} del {@link Solicitud} con el id indicado.
   * 
   * @param id identificador de la {@link Solicitud}.
   * @return specification para obtener los {@link SolicitudProyecto} de la
   *         {@link Solicitud} con el id indicado.
   */
  public static Specification<SolicitudProyecto> bySolicitudId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(SolicitudProyecto_.solicitud).get(Solicitud_.id), id);
  }

}
