package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;

import javax.persistence.criteria.Join;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion_;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoPeriodoJustificacionSpecifications {

  private ProyectoPeriodoJustificacionSpecifications() {
  }

  /**
   * {@link ProyectoPeriodoJustificacion} de la {@link Proyecto} con el id
   * indicado.
   * 
   * @param id identificador de la {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoPeriodoJustificacion}
   *         de la {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoPeriodoJustificacion> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoPeriodoJustificacion_.proyectoId), id);
    };
  }

  /**
   * {@link ProyectoPeriodoJustificacion} del {@link Proyecto} con fechas
   * solapadas
   * 
   * @param fechaInicio fecha inicio de {@link ProyectoPeriodoJustificacion}
   * @param fechaFin    fecha fin de la {@link ProyectoPeriodoJustificacion}.
   * @return specification para obtener los {@link ProyectoPeriodoJustificacion}
   *         con rango de fechas solapadas
   */
  public static Specification<ProyectoPeriodoJustificacion> byRangoFechaSolapados(Instant fechaInicio,
      Instant fechaFin) {
    return (root, query, cb) -> {
      return cb.and(
          cb.or(cb.isNull(root.get(ProyectoPeriodoJustificacion_.fechaInicio)),
              cb.lessThanOrEqualTo(root.get(ProyectoPeriodoJustificacion_.fechaInicio),
                  fechaFin != null ? fechaFin : Instant.parse("2500-01-01T23:59:59Z"))),
          cb.or(cb.isNull(root.get(ProyectoPeriodoJustificacion_.fechaFin)),
              cb.greaterThanOrEqualTo(root.get(ProyectoPeriodoJustificacion_.fechaFin),
                  fechaInicio != null ? fechaInicio : Instant.parse("1900-01-01T00:00:00Z"))));
    };
  }

  /**
   * {@link ProyectoPeriodoJustificacion} del ProyectoSGE con el id indicado.
   * 
   * @param proyectoSgeRef identificador del ProyectoSGE.
   * @return specification para obtener los {@link ProyectoPeriodoJustificacion}
   *         del ProyectoSGE con el id indicado.
   */
  public static Specification<ProyectoPeriodoJustificacion> byProyectoSgeRef(String proyectoSgeRef) {
    return (root, query, cb) -> {
      Join<ProyectoPeriodoJustificacion, Proyecto> joinProyecto = root.join(ProyectoPeriodoJustificacion_.proyecto);
      Join<Proyecto, ProyectoProyectoSge> joinProyectoSge = joinProyecto.join(Proyecto_.identificadoresSge);
      return cb.equal(joinProyectoSge.get(ProyectoProyectoSge_.proyectoSgeRef), proyectoSgeRef);
    };
  }
}
