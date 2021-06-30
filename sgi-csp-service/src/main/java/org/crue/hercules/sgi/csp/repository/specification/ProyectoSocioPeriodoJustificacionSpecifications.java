package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;

import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion_;
import org.crue.hercules.sgi.csp.model.ProyectoSocio_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoSocioPeriodoJustificacionSpecifications {

  /**
   * {@link ProyectoSocioPeriodoJustificacion} de la {@link ProyectoSocio} con el
   * id indicado.
   * 
   * @param id identificador de la {@link ProyectoSocio}.
   * @return specification para obtener los
   *         {@link ProyectoSocioPeriodoJustificacion} de la {@link ProyectoSocio}
   *         con el id indicado.
   */
  public static Specification<ProyectoSocioPeriodoJustificacion> byProyectoSocioId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoSocioPeriodoJustificacion_.proyectoSocio).get(ProyectoSocio_.id), id);
    };
  }

  /**
   * {@link ProyectoSocioPeriodoJustificacion} con fechas solapadas
   * 
   * @param fechaInicio fecha inicio de {@link ProyectoSocioPeriodoJustificacion}
   * @param fechaFin    fecha fin de la {@link ProyectoSocioPeriodoJustificacion}.
   * @return specification para obtener los
   *         {@link ProyectoSocioPeriodoJustificacion} con rango de fechas
   *         solapadas
   */
  public static Specification<ProyectoSocioPeriodoJustificacion> byRangoFechaSolapados(Instant fechaInicio,
      Instant fechaFin) {
    return (root, query, cb) -> {
      return cb.and(
          cb.or(cb.isNull(root.get(ProyectoSocioPeriodoJustificacion_.fechaInicio)),
              cb.lessThanOrEqualTo(root.get(ProyectoSocioPeriodoJustificacion_.fechaInicio),
                  fechaFin != null ? fechaFin : Instant.parse("2500-01-01T23:59:59Z"))),
          cb.or(cb.isNull(root.get(ProyectoSocioPeriodoJustificacion_.fechaFin)),
              cb.greaterThanOrEqualTo(root.get(ProyectoSocioPeriodoJustificacion_.fechaFin),
                  fechaInicio != null ? fechaInicio : Instant.parse("1900-01-01T00:00:00Z"))));
    };
  }

  /**
   * {@link ProyectoSocioPeriodoJustificacion} id diferente de
   * {@link ProyectoSocioPeriodoJustificacion} con el indicado.
   * 
   * @param id identificador de la {@link ProyectoSocioPeriodoJustificacion}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link ProyectoSocioPeriodoJustificacion} indicado.
   */
  public static Specification<ProyectoSocioPeriodoJustificacion> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(ProyectoSocioPeriodoJustificacion_.id), id).not();
    };
  }

}
