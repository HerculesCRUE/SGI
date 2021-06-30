package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudProyectoSocioEquipoSpecifications {
  /**
   * {@link SolicitudProyectoSocioEquipo} del {@link Solicitud} con el id
   * indicado.
   * 
   * @param id identificador de la {@link Solicitud}.
   * @return specification para obtener los {@link SolicitudProyectoSocioEquipo}
   *         de la {@link SolicitudProyecto} con el id indicado.
   */
  public static Specification<SolicitudProyectoSocioEquipo> bySolicitudProyectoSocio(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudProyectoSocioEquipo_.solicitudProyectoSocio).get(SolicitudProyectoSocio_.id),
          id);
    };
  }

  /**
   * {@link SolicitudProyectoSocioEquipo} cuyo id no es el recibido.
   * 
   * @param id identificador de la {@link SolicitudProyectoSocioEquipo}.
   * @return specification para obtener los {@link SolicitudProyectoSocioEquipo}
   *         cuyo id no sea el recibido
   */
  public static Specification<SolicitudProyectoSocioEquipo> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      return cb.notEqual(root.get(SolicitudProyectoSocioEquipo_.id), id);
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
  public static Specification<SolicitudProyectoSocioEquipo> inRangoMesInicio(Integer mesInicio) {
    return (root, query, cb) -> {

      return cb.or(
          cb.and(cb.lessThanOrEqualTo(root.get(SolicitudProyectoSocioEquipo_.mesInicio), mesInicio),
              cb.greaterThanOrEqualTo(root.get(SolicitudProyectoSocioEquipo_.mesFin), mesInicio)),
          cb.isNull(root.get(SolicitudProyectoSocioEquipo_.mesInicio)));
    };
  }

  /**
   * Comprueba si el mes fin se encuentra {@link SolicitudProyectoSocioEquipo}
   * dentro del rango de mese fin de otro
   * 
   * @param mesFin mes fin de {@link SolicitudProyectoSocioEquipo}.
   * @return specification para obtener los {@link SolicitudProyectoSocioEquipo}
   *         cuyo mes fin se encuentra en el rango de otro
   */
  public static Specification<SolicitudProyectoSocioEquipo> inRangoMesFin(Integer mesFin) {
    return (root, query, cb) -> {

      return cb.or(
          cb.and(cb.lessThanOrEqualTo(root.get(SolicitudProyectoSocioEquipo_.mesInicio), mesFin),
              cb.greaterThanOrEqualTo(root.get(SolicitudProyectoSocioEquipo_.mesFin), mesFin)),
          cb.isNull(root.get(SolicitudProyectoSocioEquipo_.mesFin)));
    };
  }

  /**
   * {@link SolicitudProyectoSocioEquipo} cuyo personaRef de
   * 
   * @param personaRef personaRef de la {@link SolicitudProyectoSocioEquipo}.
   * @return specification para obtener los {@link SolicitudProyectoSocioEquipo}
   *         cuyo solicitante ref es el recibido.
   */
  public static Specification<SolicitudProyectoSocioEquipo> bySolicitanteRef(String personaRef) {
    return (root, query, cb) -> {

      return cb.equal(root.get(SolicitudProyectoSocioEquipo_.personaRef), personaRef);
    };
  }
}
