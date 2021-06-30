package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudProyectoSocioPeriodoPagoSpecifications {
  /**
   * {@link SolicitudProyectoSocioPeriodoPago} del {@link SolicitudProyectoSocio}
   * con el id indicado.
   * 
   * @param id identificador de la {@link SolicitudProyectoSocio}.
   * @return specification para obtener los
   *         {@link SolicitudProyectoSocioPeriodoPago} de la
   *         {@link SolicitudProyectoSocio} con el id indicado.
   */
  public static Specification<SolicitudProyectoSocioPeriodoPago> bySolicitudProyectoSocioId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(
          root.get(SolicitudProyectoSocioPeriodoPago_.solicitudProyectoSocio).get(SolicitudProyectoSocio_.id), id);
    };
  }

}
