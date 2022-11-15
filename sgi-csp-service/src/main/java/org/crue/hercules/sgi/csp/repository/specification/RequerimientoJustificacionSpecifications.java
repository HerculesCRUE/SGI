package org.crue.hercules.sgi.csp.repository.specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge_;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion_;
import org.springframework.data.jpa.domain.Specification;

public class RequerimientoJustificacionSpecifications {

  private RequerimientoJustificacionSpecifications() {
  }

  /**
   * {@link RequerimientoJustificacion} pertenecientes
   * al proyectoSgeRef
   * 
   * @param proyectoSgeRef identificador del ProyectoSGE
   * @return specification para obtener los {@link RequerimientoJustificacion}
   *         pertenecientes al proyectoSgeRef .
   */
  public static Specification<RequerimientoJustificacion> byProyectoProyectoSgeProyectoSgeRef(String proyectoSgeRef) {
    return (root, query, cb) -> {
      Join<RequerimientoJustificacion, ProyectoProyectoSge> joinProyectoSge = root
          .join(RequerimientoJustificacion_.proyectoProyectoSge);
      return cb.equal(joinProyectoSge.get(ProyectoProyectoSge_.proyectoSgeRef), proyectoSgeRef);
    };
  }

  /**
   * {@link RequerimientoJustificacion} pertenecientes
   * al {@link Proyecto}.
   * 
   * @param proyectoId identificador del {@link Proyecto}.
   * @return specification para obtener los {@link RequerimientoJustificacion}
   *         pertenecientes al {@link Proyecto}.
   */
  public static Specification<RequerimientoJustificacion> byProyectoId(Long proyectoId) {
    return (root, query, cb) -> {
      Join<RequerimientoJustificacion, ProyectoProyectoSge> joinProyectoSge = root
          .join(RequerimientoJustificacion_.proyectoProyectoSge);
      return cb.equal(joinProyectoSge.get(ProyectoProyectoSge_.proyectoId), proyectoId);
    };
  }

  /**
   * {@link RequerimientoJustificacion} con {@link RequerimientoJustificacion}
   * previo.
   * 
   * @param requerimientoPrevioId id del {@link RequerimientoJustificacion} previo
   * @return specification para obtener los {@link RequerimientoJustificacion} con
   *         {@link RequerimientoJustificacion}
   *         previo.
   */
  public static Specification<RequerimientoJustificacion> byRequerimientoPrevioId(Long requerimientoPrevioId) {
    return (root, query, cb) -> {
      return cb.equal(root.get(RequerimientoJustificacion_.requerimientoPrevioId), requerimientoPrevioId);
    };
  }

  /**
   * {@link RequerimientoJustificacion} pertenecientes al {@link Proyecto}
   * relacionado con el {@link ProyectoProyectoSge} con id recibido por parametro.
   * 
   * @param proyectoProyectoSgeId identificador del {@link ProyectoProyectoSge}
   * @return specification para obtener los {@link RequerimientoJustificacion}
   *         pertenecientes al {@link Proyecto} relacionado con el
   *         {@link ProyectoProyectoSge} con id recibido por parametro.
   */
  public static Specification<RequerimientoJustificacion> byProyectoIdRelatedToProyectoProyectoSgeId(
      Long proyectoProyectoSgeId) {
    return (root, query, cb) -> {
      Subquery<Long> proyectoProyectoSgeSubquery = query.subquery(Long.class);
      Root<ProyectoProyectoSge> proyectoProyectoSgeSubqueryRoot = proyectoProyectoSgeSubquery
          .from(ProyectoProyectoSge.class);
      proyectoProyectoSgeSubquery.select(proyectoProyectoSgeSubqueryRoot.get(ProyectoProyectoSge_.proyectoId))
          .where(cb.equal(proyectoProyectoSgeSubqueryRoot.get(ProyectoProyectoSge_.id), proyectoProyectoSgeId));
      Join<RequerimientoJustificacion, ProyectoProyectoSge> joinProyectoSge = root
          .join(RequerimientoJustificacion_.proyectoProyectoSge);
      return cb.equal(joinProyectoSge.get(ProyectoProyectoSge_.proyectoId), proyectoProyectoSgeSubquery);
    };
  }

  /**
   * {@link RequerimientoJustificacion} ordenados por fechaNotificacion ASC.
   * 
   * @return specification para obtener los {@link RequerimientoJustificacion}
   *         ordenados por fechaNotificacion ASC.
   */
  public static Specification<RequerimientoJustificacion> orderByFechaNotificacion() {
    return (root, query, cb) -> {
      query.orderBy(cb.asc(root.get(RequerimientoJustificacion_.fechaNotificacion)));
      return cb.isTrue(cb.literal(true));
    };
  }

  /**
   * {@link RequerimientoJustificacion} con {@link ProyectoPeriodoJustificacion}.
   * 
   * @param proyectoPeriodoJustificacionId id de la entidad
   *                                       {@link ProyectoPeriodoJustificacion}.
   * @return specification para obtener los {@link RequerimientoJustificacion} con
   *         {@link ProyectoPeriodoJustificacion}.
   */
  public static Specification<RequerimientoJustificacion> byProyectoPeriodoJustificacionId(
      Long proyectoPeriodoJustificacionId) {
    return (root, query, cb) -> {
      return cb.equal(root.get(RequerimientoJustificacion_.proyectoPeriodoJustificacionId),
          proyectoPeriodoJustificacionId);
    };
  }
}
