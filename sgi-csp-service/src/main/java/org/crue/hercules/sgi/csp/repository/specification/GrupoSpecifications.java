package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo_;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion_;
import org.crue.hercules.sgi.csp.model.GrupoPersonaAutorizada;
import org.crue.hercules.sgi.csp.model.GrupoPersonaAutorizada_;
import org.crue.hercules.sgi.csp.model.GrupoResponsableEconomico;
import org.crue.hercules.sgi.csp.model.GrupoResponsableEconomico_;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.springframework.data.jpa.domain.Specification;

public class GrupoSpecifications {

  private GrupoSpecifications() {
  }

  /**
   * {@link Grupo} activos.
   * 
   * @return specification para obtener los {@link Grupo} activos.
   */
  public static Specification<Grupo> activos() {
    return (root, query, cb) -> cb.isTrue(root.get(Activable_.activo));
  }

  /**
   * {@link Grupo} distintos.
   * 
   * @return specification para obtener las entidades {@link Grupo} sin repetidos.
   */
  public static Specification<Grupo> distinct() {
    return (root, query, cb) -> {
      query.distinct(true);
      return cb.isTrue(cb.literal(true));
    };
  }

  /**
   * {@link Grupo} con el codigo indicado.
   * 
   * @param codigo un codigo
   * @return specification para obtener los {@link Grupo} con el codigo indicado.
   */
  public static Specification<Grupo> byCodigo(String codigo) {
    return (root, query, cb) -> cb.equal(root.get(Grupo_.codigo), codigo);
  }

  /**
   * {@link Grupo} con el departamento origen indicado.
   * 
   * @param departamentoRef referencia del departamento
   * @return specification para obtener los {@link Grupo} con el departamento
   *         indicado.
   */
  public static Specification<Grupo> byDepartamentoOrigenRef(String departamentoRef) {
    return (root, query, cb) -> cb.equal(root.get(Grupo_.departamentoOrigenRef), departamentoRef);
  }

  /**
   * {@link Grupo} con el id indicado.
   * 
   * @param grupoId Identificador del {@link Grupo}
   * @return specification para obtener los {@link Grupo} con id distinto del
   *         indicado.
   */
  public static Specification<Grupo> byId(Long grupoId) {
    return (root, query, cb) -> cb.equal(root.get(Grupo_.id), grupoId);
  }

  /**
   * {@link Grupo} con un id distinto del indicado.
   * 
   * @param grupoId Identificador del {@link Grupo}
   * @return specification para obtener los {@link Grupo} con id distinto del
   *         indicado.
   */
  public static Specification<Grupo> byIdNotEqual(Long grupoId) {
    return (root, query, cb) -> byId(grupoId).toPredicate(root, query, cb).not();
  }

  /**
   * {@link Grupo} para los que la persona esta en su {@link GrupoEquipo}
   * 
   * @param personaRef Identificador de la persona
   * @return specification para obtener los {@link Grupo} para los que la persona
   *         esta en su {@link GrupoEquipo}
   */
  public static Specification<Grupo> byPersonaInGrupoEquipo(String personaRef) {
    return (root, query, cb) -> {
      Join<Grupo, GrupoEquipo> joinGrupoEquipo = root.join(Grupo_.miembrosEquipo, JoinType.LEFT);

      return cb.equal(joinGrupoEquipo.get(GrupoEquipo_.personaRef), personaRef);
    };
  }

  /**
   * {@link Grupo} para los que la persona esta
   * entre las {@link GrupoPersonaAutorizada} en la fecha indicada
   * 
   * @param personaRef Identificador de la persona
   * @param fecha      fecha para la que se hace la comprobracion
   * @return specification para obtener los {@link Grupo} para los que la persona
   *         esta entre las {@link GrupoPersonaAutorizada} en la fecha indicada
   */
  public static Specification<Grupo> byPersonaAutorizada(String personaRef, Instant fecha) {
    return (root, query, cb) -> {
      Join<Grupo, GrupoPersonaAutorizada> joinPersonasAutorizadas = root.join(Grupo_.personasAutorizadas,
          JoinType.LEFT);

      Predicate personaRefEquals = cb.equal(joinPersonasAutorizadas.get(GrupoPersonaAutorizada_.personaRef),
          personaRef);

      Predicate greaterThanFechaInicio = cb.or(
          cb.lessThanOrEqualTo(joinPersonasAutorizadas.get(GrupoPersonaAutorizada_.fechaInicio), fecha),
          cb.and(
              cb.isNull(joinPersonasAutorizadas.get(GrupoPersonaAutorizada_.fechaInicio)),
              cb.or(
                  cb.lessThanOrEqualTo(root.get(Grupo_.fechaInicio), fecha))));

      Predicate lowerThanFechaFin = cb.or(
          cb.greaterThanOrEqualTo(joinPersonasAutorizadas.get(GrupoPersonaAutorizada_.fechaFin), fecha),
          cb.and(
              cb.isNull(joinPersonasAutorizadas.get(GrupoPersonaAutorizada_.fechaFin)),
              cb.or(
                  cb.isNull(root.get(Grupo_.fechaFin)),
                  cb.greaterThanOrEqualTo(root.get(Grupo_.fechaFin), fecha))));

      return cb.and(
          personaRefEquals,
          greaterThanFechaInicio,
          lowerThanFechaFin);
    };
  }

  /**
   * {@link Grupo} para los que la persona esta entre las {@link GrupoEquipo} con
   * un rol principal en la fecha indicada
   * 
   * @param personaRef Identificador de la persona
   * @param fecha      fecha para la que se hace la comprobracion
   * @return specification para obtener los {@link Grupo} para los que la persona
   *         esta entre las {@link GrupoEquipo} con un rol principal en la fecha
   *         indicada
   */
  public static Specification<Grupo> byResponsable(String personaRef, Instant fecha) {
    return (root, query, cb) -> {
      Join<Grupo, GrupoEquipo> joinEquipos = root.join(Grupo_.miembrosEquipo, JoinType.LEFT);

      Predicate personaRefEquals = cb.equal(joinEquipos.get(GrupoEquipo_.personaRef), personaRef);
      Predicate rolPrincipal = cb.equal(joinEquipos.get(GrupoEquipo_.rol).get(RolProyecto_.rolPrincipal), true);

      Predicate greaterThanFechaInicio = cb.or(
          cb.lessThanOrEqualTo(joinEquipos.get(GrupoEquipo_.fechaInicio), fecha),
          cb.and(
              cb.isNull(joinEquipos.get(GrupoEquipo_.fechaInicio)),
              cb.lessThanOrEqualTo(root.get(Grupo_.fechaInicio), fecha)));

      Predicate lowerThanFechaFin = cb.or(
          cb.greaterThanOrEqualTo(joinEquipos.get(GrupoEquipo_.fechaFin), fecha),
          cb.and(
              cb.isNull(joinEquipos.get(GrupoEquipo_.fechaFin)),
              cb.or(
                  cb.isNull(root.get(Grupo_.fechaFin)),
                  cb.greaterThanOrEqualTo(root.get(Grupo_.fechaFin), fecha))));

      return cb.and(
          personaRefEquals,
          rolPrincipal,
          greaterThanFechaInicio,
          lowerThanFechaFin);
    };
  }

  /**
   * {@link Grupo} para los que la persona esta
   * entre los {@link GrupoResponsableEconomico} en la fecha indicada
   * 
   * @param personaRef Identificador de la persona
   * @param fecha      fecha para la que se hace la comprobracion
   * @return specification para obtener los {@link Grupo} para los que la persona
   *         esta entre los {@link GrupoResponsableEconomico} en la fecha indicada
   */
  public static Specification<Grupo> byResponsableEconomico(String personaRef, Instant fecha) {
    return (root, query, cb) -> {
      Join<Grupo, GrupoResponsableEconomico> joinResponsablesEconomicos = root.join(Grupo_.responsableEconomicos,
          JoinType.LEFT);

      Predicate personaRefEquals = cb.equal(joinResponsablesEconomicos.get(GrupoResponsableEconomico_.personaRef),
          personaRef);

      Predicate greaterThanFechaInicio = cb.or(
          cb.lessThanOrEqualTo(joinResponsablesEconomicos.get(GrupoResponsableEconomico_.fechaInicio), fecha),
          cb.and(
              cb.isNull(joinResponsablesEconomicos.get(GrupoResponsableEconomico_.fechaInicio)),
              cb.or(
                  cb.lessThanOrEqualTo(root.get(Grupo_.fechaInicio), fecha))));

      Predicate lowerThanFechaFin = cb.or(
          cb.greaterThanOrEqualTo(joinResponsablesEconomicos.get(GrupoResponsableEconomico_.fechaFin), fecha),
          cb.and(
              cb.isNull(joinResponsablesEconomicos.get(GrupoResponsableEconomico_.fechaFin)),
              cb.or(
                  cb.isNull(root.get(Grupo_.fechaFin)),
                  cb.greaterThanOrEqualTo(root.get(Grupo_.fechaFin), fecha))));

      return cb.and(
          personaRefEquals,
          greaterThanFechaInicio,
          lowerThanFechaFin);
    };
  }

  /**
   * {@link Grupo} de la {@link GrupoLineaInvestigacion} con el id
   * indicado.
   * 
   * @param grupoLineaInvestigacionId identificador de la
   *                                  {@link Grupo}.
   * @return specification para obtener las {@link Convocatoria} de
   *         la {@link GrupoLineaInvestigacion} con el id indicado.
   */
  public static Specification<Grupo> byGrupoLineaInvestigacionId(Long grupoLineaInvestigacionId) {
    return (root, query, cb) -> {
      Subquery<Long> queryGrupoLineaInvestigacion = query.subquery(Long.class);
      Root<GrupoLineaInvestigacion> queryGrupoLineaInvestigacionRoot = queryGrupoLineaInvestigacion
          .from(GrupoLineaInvestigacion.class);
      queryGrupoLineaInvestigacion.select(queryGrupoLineaInvestigacionRoot.get(
          GrupoLineaInvestigacion_.grupoId))
          .where(
              cb.equal(queryGrupoLineaInvestigacionRoot.get(GrupoLineaInvestigacion_.id), grupoLineaInvestigacionId));
      return root.get(Grupo_.id).in(queryGrupoLineaInvestigacion);
    };
  }

}
