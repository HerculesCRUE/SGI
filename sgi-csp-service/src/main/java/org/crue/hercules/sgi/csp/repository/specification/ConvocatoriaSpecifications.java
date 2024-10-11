package org.crue.hercules.sgi.csp.repository.specification;

import java.util.List;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.Autorizacion_;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud_;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConvocatoriaSpecifications {

  /**
   * {@link Convocatoria} activas.
   * 
   * @return specification para obtener las {@link Convocatoria} activas.
   */
  public static Specification<Convocatoria> activos() {
    return (root, query, cb) -> cb.isTrue(root.get(Convocatoria_.activo));
  }

  /**
   * {@link Convocatoria} no activas
   * 
   * @return specification para obtener las {@link Convocatoria} no activas
   */
  public static Specification<Convocatoria> notActivos() {
    return (root, query, cb) -> cb.isFalse(root.get(Convocatoria_.activo));
  }

  /**
   * {@link Convocatoria} registradas
   * 
   * @return specification para obtener las {@link Convocatoria} registradas
   */
  public static Specification<Convocatoria> registradas() {
    return (root, query, cb) -> cb.equal(root.get(Convocatoria_.estado), Convocatoria.Estado.REGISTRADA);
  }

  /**
   * {@link Convocatoria} cuya {@link ConfiguracionSolicitud} tiene tramitación
   * SGI
   * 
   * @return specification para obtener las {@link Convocatoria} cuya
   *         {@link ConfiguracionSolicitud} tiene tramitación SGI
   */
  public static Specification<Convocatoria> configuracionSolicitudTramitacionSGI() {
    return (root, query, cb) -> {
      Subquery<Long> queryConfiguracionSolicitud = query.subquery(Long.class);
      Root<ConfiguracionSolicitud> subqRoot = queryConfiguracionSolicitud.from(ConfiguracionSolicitud.class);
      queryConfiguracionSolicitud.select(subqRoot.get(ConfiguracionSolicitud_.convocatoria).get(Convocatoria_.id))
          .where(cb.equal(subqRoot.get(ConfiguracionSolicitud_.TRAMITACION_SG_I), Boolean.TRUE));
      return root.get(Convocatoria_.id).in(queryConfiguracionSolicitud);
    };
  }

  /**
   * {@link Convocatoria} unidadGestionRef.
   * 
   * @param acronimos lista de acronimos
   * @return specification para obtener los {@link Convocatoria} cuyo
   *         unidadGestionRef se encuentre entre los recibidos.
   */
  public static Specification<Convocatoria> acronimosIn(List<String> acronimos) {
    return (root, query, cb) -> root.get(Convocatoria_.unidadGestionRef).in(acronimos);
  }

  /**
   * Solo {@link Convocatoria} distintas.
   * 
   * @return specification para obtener las entidades {@link Convocatoria}
   *         distintas solamente.
   */
  public static Specification<Convocatoria> distinct() {
    return (root, query, cb) -> {
      query.distinct(true);
      return null;
    };
  }

  /**
   * {@link Convocatoria} de la {@link Solicitud} con el id
   * indicado.
   * 
   * @param solicitudId identificador de la {@link Solicitud}.
   * @return specification para obtener las {@link Convocatoria} de
   *         la {@link Solicitud} con el id indicado.
   */
  public static Specification<Convocatoria> bySolicitudId(Long solicitudId) {
    return (root, query, cb) -> {
      Subquery<Long> querySolicitud = query.subquery(Long.class);
      Root<Solicitud> querySolicitudRoot = querySolicitud.from(Solicitud.class);
      querySolicitud.select(querySolicitudRoot.get(Solicitud_.convocatoria).get(Convocatoria_.id))
          .where(cb.equal(querySolicitudRoot.get(Solicitud_.id), solicitudId));
      return root.get(Convocatoria_.id).in(querySolicitud);
    };
  }

  /**
   * {@link Convocatoria} de la {@link Autorizacion} con el id
   * indicado.
   * 
   * @param autorizacionId identificador de la {@link Autorizacion}.
   * @return specification para obtener las {@link Convocatoria} de
   *         la {@link Autorizacion} con el id indicado.
   */
  public static Specification<Convocatoria> byAutorizacionId(Long autorizacionId) {
    return (root, query, cb) -> {
      Subquery<Long> queryAutorizacion = query.subquery(Long.class);
      Root<Autorizacion> queryAutorizacionRoot = queryAutorizacion.from(Autorizacion.class);
      queryAutorizacion.select(queryAutorizacionRoot.get(Autorizacion_.convocatoria).get(Convocatoria_.id))
          .where(cb.equal(queryAutorizacionRoot.get(Autorizacion_.id), autorizacionId));
      return root.get(Convocatoria_.id).in(queryAutorizacion);
    };
  }

  /**
   * {@link Convocatoria} del {@link Proyecto} con el id
   * indicado.
   * 
   * @param proyectoId identificador de el {@link Proyecto}.
   * @return specification para obtener las {@link Convocatoria} de
   *         el {@link Proyecto} con el id indicado.
   */
  public static Specification<Convocatoria> byProyectoId(Long proyectoId) {
    return (root, query, cb) -> {
      Subquery<Long> queryProyecto = query.subquery(Long.class);
      Root<Proyecto> queryProyectoRoot = queryProyecto.from(Proyecto.class);
      queryProyecto.select(queryProyectoRoot.get(Proyecto_.convocatoria).get(Convocatoria_.id))
          .where(cb.equal(queryProyectoRoot.get(Proyecto_.id), proyectoId));
      return root.get(Convocatoria_.id).in(queryProyecto);
    };
  }

  /**
   * {@link Convocatoria} publicas
   * 
   * @return specification para obtener las {@link Convocatoria} publicas
   */
  public static Specification<Convocatoria> publicas() {
    return (root, query, cb) -> cb.and(
        activos().toPredicate(root, query, cb),
        registradas().toPredicate(root, query, cb),
        byFormularioSolicitud(FormularioSolicitud.RRHH).toPredicate(root, query, cb),
        configuracionSolicitudTramitacionSGI().toPredicate(root, query, cb));
  }

  /**
   * {@link Convocatoria} con el {@link FormularioSolicitud} indicado
   * 
   * @param formularioSolicitud {@link FormularioSolicitud}
   * @return specification para obtener las {@link Convocatoria} activas
   */
  public static Specification<Convocatoria> byFormularioSolicitud(FormularioSolicitud formularioSolicitud) {
    return (root, query, cb) -> cb.equal(root.get(Convocatoria_.formularioSolicitud), formularioSolicitud);
  }

}
