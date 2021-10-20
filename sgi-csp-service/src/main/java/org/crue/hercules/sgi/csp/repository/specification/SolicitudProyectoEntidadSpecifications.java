package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad_;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudProyectoEntidadSpecifications {

  /**
   * {@link SolicitudProyectoEntidad} de la {@link Solicitud} con el id indicado.
   * 
   * @param id identificador de la {@link Solicitud}.
   * @return specification para obtener las {@link SolicitudProyectoEntidad} de la
   *         {@link Solicitud} con el id indicado.
   */
  public static Specification<SolicitudProyectoEntidad> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(
          root.get(SolicitudProyectoEntidad_.solicitudProyecto).get(SolicitudProyecto_.solicitud).get(Solicitud_.id),
          id);
    };
  }

  /**
   * {@link SolicitudProyectoEntidad} con convocatoriaEntidadFinanciadora.
   * 
   * @return specification para obtener las {@link SolicitudProyectoEntidad} con
   *         convocatoriaEntidadFinanciadora.
   */
  public static Specification<SolicitudProyectoEntidad> isEntidadFinanciadoraConvocatoria() {
    return (root, query, cb) -> {
      return cb.isNotNull(root.get(SolicitudProyectoEntidad_.convocatoriaEntidadFinanciadora));
    };
  }

  /**
   * {@link SolicitudProyectoEntidad} con convocatoriaEntidadGestora.
   * 
   * @return specification para obtener las {@link SolicitudProyectoEntidad} con
   *         convocatoriaEntidadGestora.
   */
  public static Specification<SolicitudProyectoEntidad> isEntidadGestora() {
    return (root, query, cb) -> {
      return cb.isNotNull(root.get(SolicitudProyectoEntidad_.convocatoriaEntidadGestora));
    };
  }

  /**
   * {@link SolicitudProyectoEntidad} con convocatoriaEntidadGestora.
   * 
   * @return specification para obtener las {@link SolicitudProyectoEntidad} con
   *         convocatoriaEntidadGestora.
   */
  public static Specification<SolicitudProyectoEntidad> isEntidadFinanciadoraAjena() {
    return (root, query, cb) -> {
      return cb.isNotNull(root.get(SolicitudProyectoEntidad_.solicitudProyectoEntidadFinanciadoraAjena));
    };
  }

  /**
   * {@link SolicitudProyectoEntidad} de la
   * {@link SolicitudProyectoEntidadFinanciadoraAjena} con el id indicado.
   * 
   * @param id identificador de la
   *           {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * @return specification para obtener las {@link SolicitudProyectoEntidad} de la
   *         {@link SolicitudProyectoEntidadFinanciadoraAjena} con el id indicado.
   */
  public static Specification<SolicitudProyectoEntidad> bySolicitudProyectoEntidadFinanciadoraAjenaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudProyectoEntidad_.solicitudProyectoEntidadFinanciadoraAjena)
          .get(SolicitudProyectoEntidadFinanciadoraAjena_.id), id);
    };
  }

}