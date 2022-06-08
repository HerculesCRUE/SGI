package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoPersonaAutorizada;
import org.crue.hercules.sgi.csp.model.GrupoPersonaAutorizada_;
import org.springframework.data.jpa.domain.Specification;

public class GrupoPersonaAutorizadaSpecifications {

  private GrupoPersonaAutorizadaSpecifications() {
  }

  /**
   * {@link GrupoPersonaAutorizada} del {@link Grupo} con el id indicado.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @return specification para obtener los {@link GrupoPersonaAutorizada} del
   *         {@link Grupo} con el id indicado.
   */
  public static Specification<GrupoPersonaAutorizada> byGrupoId(Long grupoId) {
    return (root, query, cb) -> cb.equal(root.get(GrupoPersonaAutorizada_.grupoId), grupoId);
  }

  /**
   * {@link GrupoPersonaAutorizada} con fechas solapadas
   * 
   * @param fechaInicio fecha inicio de {@link GrupoPersonaAutorizada}
   * @param fechaFin    fecha fin de la {@link GrupoPersonaAutorizada}.
   * @return specification para obtener los {@link GrupoPersonaAutorizada} con
   *         rango de
   *         fechas solapadas
   */
  public static Specification<GrupoPersonaAutorizada> byRangoFechaSolapados(Instant fechaInicio, Instant fechaFin) {
    return (root, query, cb) -> {
      return cb.and(
          cb.or(cb.isNull(root.get(GrupoPersonaAutorizada_.fechaInicio)),
              cb.lessThanOrEqualTo(root.get(GrupoPersonaAutorizada_.fechaInicio),
                  fechaFin != null ? fechaFin : Instant.parse("2500-01-01T23:59:59Z"))),
          cb.or(cb.isNull(root.get(GrupoPersonaAutorizada_.fechaFin)),
              cb.greaterThanOrEqualTo(root.get(GrupoPersonaAutorizada_.fechaFin),
                  fechaInicio != null ? fechaInicio : Instant.parse("1900-01-01T00:00:00Z"))));
    };
  }

  /**
   * {@link GrupoPersonaAutorizada} cuya persona Ref sea la recibida.
   * 
   * @param personaRef persona ref de {@link GrupoPersonaAutorizada}
   * @return specification para obtener los {@link GrupoPersonaAutorizada} cuya
   *         persona
   *         Ref sea la recibida.
   */
  public static Specification<GrupoPersonaAutorizada> byPersonaRef(String personaRef) {
    return (root, query, cb) -> {
      return cb.equal(root.get(GrupoPersonaAutorizada_.personaRef), personaRef);
    };
  }

  /**
   * {@link GrupoPersonaAutorizada} id diferente de
   * {@link GrupoPersonaAutorizada} con el
   * indicado.
   * 
   * @param id identificador de la {@link GrupoPersonaAutorizada}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link GrupoPersonaAutorizada} indicado.
   */
  public static Specification<GrupoPersonaAutorizada> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(GrupoPersonaAutorizada_.id), id).not();
    };

  }

}
