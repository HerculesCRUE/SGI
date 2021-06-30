package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudProyectoEquipoSpecifications {

  /**
   * {@link SolicitudProyectoEquipo} del {@link Solicitud} con el id indicado.
   * 
   * @param id identificador de la {@link Solicitud}.
   * @return specification para obtener los {@link SolicitudProyectoEquipo} de la
   *         {@link SolicitudProyecto} con el id indicado.
   */
  public static Specification<SolicitudProyectoEquipo> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(
          root.get(SolicitudProyectoEquipo_.solicitudProyecto).get(SolicitudProyecto_.solicitud).get(Solicitud_.id),
          id);
    };
  }

  /**
   * {@link SolicitudProyectoEquipo} cuyo id no es el recibido.
   * 
   * @param id identificador de la {@link SolicitudProyectoEquipo}.
   * @return specification para obtener los {@link SolicitudProyectoEquipo} cuyo
   *         id no sea el recibido
   */
  public static Specification<SolicitudProyectoEquipo> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      return cb.notEqual(root.get(SolicitudProyectoEquipo_.id), id);
    };
  }

  /**
   * Comprueba si el mes inicio se encuentra {@link SolicitudProyectoEquipo}
   * dentro del rango de mese inicio de otro
   * 
   * @param mesInicio mes inicio de {@link SolicitudProyectoEquipo}.
   * @return specification para obtener los {@link SolicitudProyectoEquipo} cuyo
   *         mes inicio se encuentra en el rango de otro
   */
  public static Specification<SolicitudProyectoEquipo> inRangoMesInicio(Integer mesInicio) {
    return (root, query, cb) -> {
      return cb.or(
          cb.and(cb.lessThanOrEqualTo(root.get(SolicitudProyectoEquipo_.mesInicio), mesInicio),
              cb.greaterThanOrEqualTo(root.get(SolicitudProyectoEquipo_.mesFin), mesInicio)),
          cb.isNull(root.get(SolicitudProyectoEquipo_.mesInicio)));
    };
  }

  /**
   * {@link SolicitudProyectoEquipo} comprueba solpamientos de fechas
   * 
   * @param mesInicio fecha inicio de {@link SolicitudProyectoEquipo}
   * @param mesFin    fecha fin de la {@link SolicitudProyectoEquipo}.
   * @return specification para obtener los {@link SolicitudProyectoEquipo} con
   *         rango de meses solapadas
   */
  public static Specification<SolicitudProyectoEquipo> byRangoMesesSolapados(Integer mesInicio, Integer mesFin) {
    return (root, query, cb) -> {
      return cb.and(
          cb.or(cb.isNull(root.get(SolicitudProyectoEquipo_.mesInicio)),
              cb.lessThanOrEqualTo(root.get(SolicitudProyectoEquipo_.mesInicio),
                  mesFin != null ? mesFin : Integer.MAX_VALUE)),
          cb.or(cb.isNull(root.get(SolicitudProyectoEquipo_.mesFin)), cb.greaterThanOrEqualTo(
              root.get(SolicitudProyectoEquipo_.mesFin), mesInicio != null ? mesInicio : Integer.MIN_VALUE)));
    };
  }

  /**
   * Comprueba si el mes fin se encuentra {@link SolicitudProyectoEquipo} dentro
   * del rango de mese fin de otro
   * 
   * @param mesFin mes fin de {@link SolicitudProyectoEquipo}.
   * @return specification para obtener los {@link SolicitudProyectoEquipo} cuyo
   *         mes fin se encuentra en el rango de otro
   */
  public static Specification<SolicitudProyectoEquipo> inRangoMesFin(Integer mesFin) {
    return (root, query, cb) -> {

      return cb.or(
          cb.and(cb.lessThanOrEqualTo(root.get(SolicitudProyectoEquipo_.mesInicio), mesFin),
              cb.greaterThanOrEqualTo(root.get(SolicitudProyectoEquipo_.mesFin), mesFin)),
          cb.isNull(root.get(SolicitudProyectoEquipo_.mesFin)));
    };
  }

  /**
   * {@link SolicitudProyectoEquipo} cuyo solicitante ref de {@link Solicitud} se
   * corresponde con el recibido por parámetro.
   * 
   * @param solicitanteRef solicitante ref de la {@link Solicitud}.
   * @return specification para obtener los {@link SolicitudProyectoEquipo} cuyo
   *         solicitante ref es el recibido.
   */
  public static Specification<SolicitudProyectoEquipo> bySolicitanteRef(String solicitanteRef) {
    return (root, query, cb) -> {

      return cb.equal(root.get(SolicitudProyectoEquipo_.solicitudProyecto).get(SolicitudProyecto_.solicitud)
          .get(Solicitud_.solicitanteRef), solicitanteRef);
    };
  }

  /**
   * {@link SolicitudProyectoEquipo} cuyo personaRef ref de
   * {@link SolicitudProyectoEquipo} se corresponde con el recibido por parámetro.
   * 
   * @param personaRef solicitante ref de la {@link SolicitudProyectoEquipo}.
   * @return specification para obtener los {@link SolicitudProyectoEquipo} cuyo
   *         personaRef es el recibido.
   */
  public static Specification<SolicitudProyectoEquipo> byPersonaRef(String personaRef) {
    return (root, query, cb) -> {

      return cb.equal(root.get(SolicitudProyectoEquipo_.personaRef), personaRef);
    };
  }

}
