package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoEquipoSpecifications {

  /**
   * {@link ProyectoEquipo} de la {@link Proyecto} con el id indicado.
   * 
   * @param id identificador de la {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoEquipo} de la
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoEquipo> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoEquipo_.proyecto).get(Proyecto_.id), id);
    };
  }

  /**
   * {@link ProyectoEquipo} con fechas solapadas
   * 
   * @param fechaInicio fecha inicio de {@link ProyectoEquipo}
   * @param fechaFin    fecha fin de la {@link ProyectoEquipo}.
   * @return specification para obtener los {@link ProyectoEquipo} con rango de
   *         fechas solapadas
   */
  public static Specification<ProyectoEquipo> byRangoFechaSolapados(Instant fechaInicio, Instant fechaFin) {
    return (root, query, cb) -> {
      return cb.and(
          cb.or(cb.isNull(root.get(ProyectoEquipo_.fechaInicio)),
              cb.lessThanOrEqualTo(root.get(ProyectoEquipo_.fechaInicio),
                  fechaFin != null ? fechaFin : Instant.parse("2500-01-01T23:59:59Z"))),
          cb.or(cb.isNull(root.get(ProyectoEquipo_.fechaFin)),
              cb.greaterThanOrEqualTo(root.get(ProyectoEquipo_.fechaFin),
                  fechaInicio != null ? fechaInicio : Instant.parse("1900-01-01T00:00:00Z"))));
    };
  }

  /**
   * {@link ProyectoEquipo} cuya persona Ref sea la recibida.
   * 
   * @param personaRef persona ref de {@link ProyectoEquipo}
   * @return specification para obtener los {@link ProyectoEquipo} cuya persona
   *         Ref sea la recibida.
   */
  public static Specification<ProyectoEquipo> byPersonaRef(String personaRef) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoEquipo_.personaRef), personaRef);
    };
  }

  /**
   * {@link ProyectoEquipo} id diferente de {@link ProyectoEquipo} con el
   * indicado.
   * 
   * @param id identificador de la {@link ProyectoEquipo}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link ProyectoEquipo} indicado.
   */
  public static Specification<ProyectoEquipo> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(ProyectoEquipo_.id), id).not();
    };

  }
}