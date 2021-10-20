package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora_;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora_;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto_;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudProyectoPresupuestoSpecifications {

  /**
   * {@link SolicitudProyectoPresupuesto} del {@link Solicitud} con el id
   * indicado.
   * 
   * @param id identificador de la {@link Solicitud}.
   * @return specification para obtener los {@link SolicitudProyectoPresupuesto}
   *         de la {@link Solicitud} con el id indicado.
   */
  public static Specification<SolicitudProyectoPresupuesto> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudProyectoPresupuesto_.solicitudProyecto).get(SolicitudProyecto_.solicitud)
          .get(Solicitud_.id), id);
    };
  }

  /**
   * {@link SolicitudProyectoPresupuesto} del {@link SolicitudProyectoEntidad} con
   * el id indicado.
   * 
   * @param id identificador de la {@link Solicitud}.
   * @return specification para obtener los {@link SolicitudProyectoPresupuesto}
   *         de la {@link SolicitudProyectoEntidad} con el id indicado.
   */
  public static Specification<SolicitudProyectoPresupuesto> bySolicitudProyectoEntidadId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(
          root.get(SolicitudProyectoPresupuesto_.solicitudProyectoEntidad).get(SolicitudProyectoEntidad_.id), id);
    };
  }

  /**
   * {@link SolicitudProyectoPresupuesto} de la entidad con la referencia
   * indicada.
   * 
   * @param entidadRef identificador de la entidad.
   * @return specification para obtener los {@link SolicitudProyectoPresupuesto}
   *         de la entidad con la referencia indicada.
   */
  public static Specification<SolicitudProyectoPresupuesto> byEntidadRef(String entidadRef) {
    return (root, query, cb) -> {
      return cb.or(
          // SolicitudProyectoEntidadFinanciadoraAjena
          cb.and(
              cb.isNotNull(root.get(SolicitudProyectoPresupuesto_.solicitudProyectoEntidad)
                  .get(SolicitudProyectoEntidad_.solicitudProyectoEntidadFinanciadoraAjena)),
              cb.equal(root.get(SolicitudProyectoPresupuesto_.solicitudProyectoEntidad)
                  .get(SolicitudProyectoEntidad_.solicitudProyectoEntidadFinanciadoraAjena)
                  .get(SolicitudProyectoEntidadFinanciadoraAjena_.entidadRef), entidadRef)),
          // ConvocatoriaEntidadFinanciadora
          cb.and(
              cb.isNotNull(root.get(SolicitudProyectoPresupuesto_.solicitudProyectoEntidad)
                  .get(SolicitudProyectoEntidad_.convocatoriaEntidadFinanciadora)),
              cb.equal(root.get(SolicitudProyectoPresupuesto_.solicitudProyectoEntidad)
                  .get(SolicitudProyectoEntidad_.convocatoriaEntidadFinanciadora)
                  .get(ConvocatoriaEntidadFinanciadora_.entidadRef), entidadRef)),
          // ConvocatoriaEntidadGestora
          cb.and(
              cb.isNotNull(root.get(SolicitudProyectoPresupuesto_.solicitudProyectoEntidad)
                  .get(SolicitudProyectoEntidad_.convocatoriaEntidadGestora)),
              cb.equal(root.get(SolicitudProyectoPresupuesto_.solicitudProyectoEntidad)
                  .get(SolicitudProyectoEntidad_.convocatoriaEntidadGestora)
                  .get(ConvocatoriaEntidadGestora_.entidadRef), entidadRef)));
    };
  }

  /**
   * {@link SolicitudProyectoPresupuesto} con financiacionAjena igual a la
   * indicada.
   * 
   * @param financiacionAjena flag financiacionAjena.
   * @return specification para obtener los {@link SolicitudProyectoPresupuesto}
   *         de la entidad con la referencia indicada.
   */
  public static Specification<SolicitudProyectoPresupuesto> byFinanciacionAjena(Boolean financiacionAjena) {
    return (root, query, cb) -> {
      if (financiacionAjena) {
        return cb.isNotNull(root.get(SolicitudProyectoPresupuesto_.solicitudProyectoEntidad)
            .get(SolicitudProyectoEntidad_.solicitudProyectoEntidadFinanciadoraAjena));
      } else {
        return cb.isNull(root.get(SolicitudProyectoPresupuesto_.solicitudProyectoEntidad)
            .get(SolicitudProyectoEntidad_.solicitudProyectoEntidadFinanciadoraAjena));
      }
    };
  }

  /**
   * {@link SolicitudProyectoPresupuesto} asociado al
   * {@link SolicitudProyectoEntidadFinanciadoraAjena}
   * 
   * @param solicitudProyectoEntidadFinanciadoraAjenaId identificador del
   *                                                    {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * @return specification para obtener los {@link SolicitudProyectoPresupuesto}
   *         de la entidad {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   */
  public static Specification<SolicitudProyectoPresupuesto> bySolicitudProyectoEntidadFinanciadoraAjenaId(
      Long solicitudProyectoEntidadFinanciadoraAjenaId) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudProyectoPresupuesto_.solicitudProyectoEntidad)
          .get(SolicitudProyectoEntidad_.solicitudProyectoEntidadFinanciadoraAjena)
          .get(SolicitudProyectoEntidadFinanciadoraAjena_.id), solicitudProyectoEntidadFinanciadoraAjenaId);
    };
  }

}
