package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo_;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.springframework.data.jpa.domain.Specification;

public class GrupoEquipoSpecifications {

  private GrupoEquipoSpecifications() {
  }

  /**
   * {@link GrupoEquipo} del {@link Grupo} con el id indicado.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @return specification para obtener los {@link GrupoEquipo} del
   *         {@link Grupo} con el id indicado.
   */
  public static Specification<GrupoEquipo> byGrupoId(Long grupoId) {
    return (root, query, cb) -> cb.equal(root.get(GrupoEquipo_.grupoId), grupoId);
  }

  /**
   * {@link GrupoEquipo} con fechas solapadas
   * 
   * @param fechaInicio fecha inicio de {@link GrupoEquipo}
   * @param fechaFin    fecha fin de la {@link GrupoEquipo}.
   * @return specification para obtener los {@link GrupoEquipo} con rango de
   *         fechas solapadas
   */
  public static Specification<GrupoEquipo> byRangoFechaSolapados(Instant fechaInicio, Instant fechaFin) {
    return (root, query, cb) -> cb.and(
        cb.or(cb.isNull(root.get(GrupoEquipo_.fechaInicio)),
            cb.lessThanOrEqualTo(root.get(GrupoEquipo_.fechaInicio),
                fechaFin != null ? fechaFin : Instant.parse("2500-01-01T23:59:59Z"))),
        cb.or(cb.isNull(root.get(GrupoEquipo_.fechaFin)),
            cb.greaterThanOrEqualTo(root.get(GrupoEquipo_.fechaFin),
                fechaInicio != null ? fechaInicio : Instant.parse("1900-01-01T00:00:00Z"))));
  }

  /**
   * {@link GrupoEquipo} cuya persona Ref sea la recibida.
   * 
   * @param personaRef persona ref de {@link GrupoEquipo}
   * @return specification para obtener los {@link GrupoEquipo} cuya persona
   *         Ref sea la recibida.
   */
  public static Specification<GrupoEquipo> byPersonaRef(String personaRef) {
    return (root, query, cb) -> cb.equal(root.get(GrupoEquipo_.personaRef), personaRef);
  }

  /**
   * {@link GrupoEquipo} id diferente de {@link GrupoEquipo} con el
   * indicado.
   * 
   * @param id identificador de la {@link GrupoEquipo}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link GrupoEquipo} indicado.
   */
  public static Specification<GrupoEquipo> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(GrupoEquipo_.id), id).not();
    };
  }

  /**
   * {@link GrupoEquipo} cuya persona Ref sea la recibida o formen parte de un
   * grupo en el que la persona sea un investigador principal.
   * 
   * @param personaRef persona ref de {@link GrupoEquipo}
   * @param fecha      fecha para la que se hace la comprobracion
   * @return specification para obtener los {@link GrupoEquipo} cuya persona
   *         Ref sea la recibida o formen parte de un grupo en el que la persona
   *         sea un investigador principal.
   */
  public static Specification<GrupoEquipo> byPersonaRefOrInvestigadorPrincipal(String personaRef, Instant fecha) {
    return (root, query, cb) -> {

      Predicate personaRefEquals = cb.equal(root.get(GrupoEquipo_.personaRef), personaRef);

      root.join(GrupoEquipo_.grupo);

      Subquery<Long> queryGruposInvestigadorPrincipal = query.subquery(Long.class);
      Root<Grupo> subqRoot = queryGruposInvestigadorPrincipal.from(Grupo.class);
      queryGruposInvestigadorPrincipal.select(subqRoot.get(Grupo_.id))
          .where(GrupoSpecifications.byResponsable(personaRef, fecha).toPredicate(subqRoot, query, cb));

      Predicate grupoInvestigadorPrincipal = root.get(GrupoEquipo_.grupoId).in(queryGruposInvestigadorPrincipal);

      return cb.or(personaRefEquals, grupoInvestigadorPrincipal);
    };
  }

}
