package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoPeriodoSeguimientoSpecifications {

  /**
   * Se obtienen los {@link ProyectoPeriodoSeguimiento} por proyecto
   * 
   * @param idProyecto identificador del {@link Proyecto}
   * @return specification para obtener los {@link ProyectoPeriodoSeguimiento} por
   *         proyecto
   */
  public static Specification<ProyectoPeriodoSeguimiento> byProyecto(Long idProyecto) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoPeriodoSeguimiento_.proyecto).get(Proyecto_.id), idProyecto);
    };
  }

  /**
   * Se obtienen los {@link ProyectoPeriodoSeguimiento} con valores para las
   * fechas de inicio y fin
   * 
   * @return specification para obtener los {@link ProyectoPeriodoSeguimiento} con
   *         valor en las fechas
   */
  public static Specification<ProyectoPeriodoSeguimiento> withFechas() {
    return (root, query, cb) -> {

      return cb.and(cb.isNotNull(root.get(ProyectoPeriodoSeguimiento_.fechaInicio)),
          cb.isNotNull(root.get(ProyectoPeriodoSeguimiento_.fechaFin)));
    };
  }

  /**
   * {@link ProyectoPeriodoSeguimiento} de la {@link Convocatoria} con fechas
   * solapadas
   * 
   * @param fechaInicio fecha inicio de {@link ProyectoPeriodoSeguimiento}
   * @param fechaFin    fecha fin de la {@link ProyectoPeriodoSeguimiento}.
   * @return specification para obtener los {@link ProyectoPeriodoSeguimiento} con
   *         rango de fechas solapadas
   */
  public static Specification<ProyectoPeriodoSeguimiento> byRangoFechaSolapados(Instant fechaInicio, Instant fechaFin) {
    return (root, query, cb) -> {
      return cb.and(cb.lessThanOrEqualTo(root.get(ProyectoPeriodoSeguimiento_.fechaInicio), fechaFin),
          cb.greaterThanOrEqualTo(root.get(ProyectoPeriodoSeguimiento_.fechaFin), fechaInicio));
    };
  }

  /**
   * {@link ProyectoPeriodoSeguimiento} excluido en la lista.
   * 
   * @param excluidos lista de ids excluidos
   * @return specification para obtener los {@link ProyectoPeriodoSeguimiento}
   *         cuyo id no se encuentre entre los recibidos.
   */
  public static Specification<ProyectoPeriodoSeguimiento> notIn(List<Long> excluidos) {
    return (root, query, cb) -> {
      return root.get(ProyectoPeriodoSeguimiento_.id).in(excluidos).not();
    };
  }
}