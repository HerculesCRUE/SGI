package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoResponsableEconomico_;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudProyectoResponsableEconomicoSpecifications {

  /**
   * {@link SolicitudProyectoResponsableEconomico} de la {@link Solicitud} con el
   * id indicado.
   * 
   * @param id identificador del {@link Solicitud}.
   * @return specification para obtener los
   *         {@link SolicitudProyectoResponsableEconomico} de la {@link Solicitud}
   *         con el id indicado.
   */
  public static Specification<SolicitudProyectoResponsableEconomico> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudProyectoResponsableEconomico_.solicitudProyecto)
          .get(SolicitudProyecto_.solicitud).get(Solicitud_.id), id);
    };
  }

}