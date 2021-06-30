package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudProyectoSocioPeriodoJustificacionSpecifications {
  /**
   * {@link SolicitudProyectoSocioPeriodoJustificacion} del
   * {@link SolicitudProyectoSocio} con el id indicado.
   * 
   * @param id identificador del {@link SolicitudProyectoSocio}.
   * @return specification para obtener los
   *         {@link SolicitudProyectoSocioPeriodoJustificacion} de la
   *         {@link SolicitudProyectoSocio} con el id indicado.
   */
  public static Specification<SolicitudProyectoSocioPeriodoJustificacion> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(
          root.get(SolicitudProyectoSocioPeriodoJustificacion_.solicitudProyectoSocio).get(SolicitudProyectoSocio_.id),
          id);
    };
  }

}
