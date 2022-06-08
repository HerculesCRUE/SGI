package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoResponsableEconomico;
import org.crue.hercules.sgi.csp.model.GrupoResponsableEconomico_;
import org.springframework.data.jpa.domain.Specification;

public class GrupoResponsableEconomicoSpecifications {

  private GrupoResponsableEconomicoSpecifications() {
  }

  /**
   * {@link GrupoResponsableEconomico} del {@link Grupo} con el id indicado.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @return specification para obtener los {@link GrupoResponsableEconomico} del
   *         {@link Grupo} con el id indicado.
   */
  public static Specification<GrupoResponsableEconomico> byGrupoId(Long grupoId) {
    return (root, query, cb) -> cb.equal(root.get(GrupoResponsableEconomico_.grupoId), grupoId);
  }

  /**
   * {@link GrupoResponsableEconomico} con fechas solapadas
   * 
   * @param fechaInicio fecha inicio de {@link GrupoResponsableEconomico}
   * @param fechaFin    fecha fin de la {@link GrupoResponsableEconomico}.
   * @return specification para obtener los {@link GrupoResponsableEconomico} con
   *         rango de
   *         fechas solapadas
   */
  public static Specification<GrupoResponsableEconomico> byRangoFechaSolapados(Instant fechaInicio, Instant fechaFin) {
    return (root, query, cb) -> {
      return cb.and(
          cb.or(cb.isNull(root.get(GrupoResponsableEconomico_.fechaInicio)),
              cb.lessThanOrEqualTo(root.get(GrupoResponsableEconomico_.fechaInicio),
                  fechaFin != null ? fechaFin : Instant.parse("2500-01-01T23:59:59Z"))),
          cb.or(cb.isNull(root.get(GrupoResponsableEconomico_.fechaFin)),
              cb.greaterThanOrEqualTo(root.get(GrupoResponsableEconomico_.fechaFin),
                  fechaInicio != null ? fechaInicio : Instant.parse("1900-01-01T00:00:00Z"))));
    };
  }

  /**
   * {@link GrupoResponsableEconomico} cuya persona Ref sea la recibida.
   * 
   * @param personaRef persona ref de {@link GrupoResponsableEconomico}
   * @return specification para obtener los {@link GrupoResponsableEconomico} cuya
   *         persona
   *         Ref sea la recibida.
   */
  public static Specification<GrupoResponsableEconomico> byPersonaRef(String personaRef) {
    return (root, query, cb) -> {
      return cb.equal(root.get(GrupoResponsableEconomico_.personaRef), personaRef);
    };
  }

  /**
   * {@link GrupoResponsableEconomico} id diferente de
   * {@link GrupoResponsableEconomico} con el
   * indicado.
   * 
   * @param id identificador de la {@link GrupoResponsableEconomico}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link GrupoResponsableEconomico} indicado.
   */
  public static Specification<GrupoResponsableEconomico> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(GrupoResponsableEconomico_.id), id).not();
    };

  }

}
