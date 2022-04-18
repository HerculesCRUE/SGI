package org.crue.hercules.sgi.csp.repository.custom;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.dto.GrupoEquipoDto;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo_;
import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion_;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA custom repository para {@link GrupoEquipo}.
 */
@Slf4j
@Component
public class CustomGrupoEquipoRepositoryImpl implements CustomGrupoEquipoRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * {@link GrupoEquipo} que son investigador o investigadores principales del
   * {@link Grupo} con el id indicado.
   * 
   * Se considera investiador principal al {@link GrupoEquipo} que a fecha actual
   * tiene el {@link RolProyecto} con el flag "principal" a <code>true</code>. En
   * caso de existir mas de un {@link GrupoEquipo}, se
   * recupera el que tenga el mayor porcentaje de dedicación al grupo (campo
   * "participación").
   * Y en caso de que varios coincidan se devuelven todos los que coincidan.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @param fecha   fecha en la que se busca el investigador principal.
   * @return la lista de personaRef de los investigadores principales del
   *         {@link Grupo} en el momento actual.
   */
  @Override
  public List<String> findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion(Long grupoId, Instant fecha) {
    log.debug("findPersonaRefInvestigadoresPrincipales(Long grupoId, Instant fecha) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> cq = cb.createQuery(String.class);
    Root<GrupoEquipo> root = cq.from(GrupoEquipo.class);

    Subquery<BigDecimal> queryMaxParticipacion = cq.subquery(BigDecimal.class);
    Root<GrupoEquipo> subqRoot = queryMaxParticipacion.from(GrupoEquipo.class);

    queryMaxParticipacion.select(cb.max(subqRoot.get(GrupoEquipo_.participacion)))
        .where(getPredicateRolPrincipalFecha(subqRoot, cb, grupoId, fecha));

    cq.select(root.get(GrupoEquipo_.personaRef)).where(cb.and(
        getPredicateRolPrincipalFecha(root, cb, grupoId, fecha),
        cb.equal(root.get(GrupoEquipo_.participacion),
            queryMaxParticipacion)))
        .distinct(true);

    List<String> returnValue = entityManager.createQuery(cq).getResultList();

    log.debug("findPersonaRefInvestigadoresPrincipales(Long grupoId, Instant fecha) - end");
    return returnValue;
  }

  /**
   * {@link GrupoEquipo} que son investigador o investigadores principales del
   * {@link Grupo} con el id indicado.
   * 
   * Se considera investiador principal al {@link GrupoEquipo} que a fecha actual
   * tiene el {@link RolProyecto} con el flag "principal" a true. En caso de que
   * varios coincidan se devuelven todos los que coincidan.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @param fecha   fecha en la que se busca el investigador principal.
   * @return la lista de personaRef de los investigadores principales del
   *         {@link Grupo} en el momento actual.
   */
  @Override
  public List<String> findPersonaRefInvestigadoresPrincipales(Long grupoId, Instant fecha) {
    log.debug("findPersonaRefInvestigadoresPrincipales(Long grupoId, Instant fecha) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> cq = cb.createQuery(String.class);
    Root<GrupoEquipo> root = cq.from(GrupoEquipo.class);

    cq.select(root.get(GrupoEquipo_.personaRef)).where(cb.and(
        getPredicateRolPrincipalFecha(root, cb, grupoId, fecha)))
        .distinct(true);

    List<String> returnValue = entityManager.createQuery(cq).getResultList();

    log.debug("findPersonaRefInvestigadoresPrincipales(Long grupoId, Instant fecha) - end");
    return returnValue;
  }

  private Predicate getPredicateRolPrincipalFecha(Root<GrupoEquipo> root, CriteriaBuilder cb, Long grupoId,
      Instant fecha) {
    Predicate grupoEquals = cb.equal(root.get(GrupoEquipo_.grupoId), grupoId);
    Predicate rolPrincipal = cb.equal(root.get(GrupoEquipo_.rol).get(RolProyecto_.rolPrincipal), true);
    Predicate greaterThanFechaInicio = cb.lessThanOrEqualTo(root.get(GrupoEquipo_.fechaInicio), fecha);
    Predicate lowerThanFechaFin = cb.or(cb.isNull(root.get(GrupoEquipo_.fechaFin)),
        cb.greaterThanOrEqualTo(root.get(GrupoEquipo_.fechaFin), fecha));

    Predicate fechaLowerThanFechaInicioGrupo = cb.greaterThan(root.get(GrupoEquipo_.grupo).get(Grupo_.fechaInicio),
        fecha);
    Predicate fechaGreaterThanFechaFinGrupo = cb.lessThan(root.get(GrupoEquipo_.grupo).get(Grupo_.fechaFin), fecha);

    return cb.and(
        grupoEquals,
        rolPrincipal,
        cb.or(fechaLowerThanFechaInicioGrupo, greaterThanFechaInicio),
        cb.or(fechaGreaterThanFechaFinGrupo, lowerThanFechaFin));
  }

  /**
   * Comprueba si personaRef pertenece a un grupo de investigación con un rol con
   * el flag de baremable a true a fecha 31 de diciembre del año que se esta
   * baremando y el grupo al que pertenecen los autores (tabla Grupo) este activo
   * y el campo "Grupo especial de investigación" a "No" el 31 de diciembre del
   * año que se esta baremando
   *
   * @param personaRef      personaRef
   * @param fechaBaremacion fecha de baremación
   * @return true/false
   */
  @Override
  public boolean isPersonaBaremable(String personaRef, Instant fechaBaremacion) {
    log.debug("isPersonaBaremable(personaRef, fechaBaremacion) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> cq = cb.createQuery(String.class);
    Root<GrupoEquipo> root = cq.from(GrupoEquipo.class);

    Join<GrupoEquipo, Grupo> joinGrupo = root.join(GrupoEquipo_.grupo);

    Root<GrupoEspecialInvestigacion> rootGrupoEspecialInvestigacion = cq.from(GrupoEspecialInvestigacion.class);

    Predicate predicateIsBaremable = getPredicatesIsBaremable(fechaBaremacion, cb, root, joinGrupo,
        rootGrupoEspecialInvestigacion);

    Predicate predicateIsPersonaRef = cb.equal(root.get(GrupoEquipo_.personaRef), personaRef);

    Predicate predicateFinal = cb.and(
        predicateIsPersonaRef,
        predicateIsBaremable);

    cq.select(root.get(GrupoEquipo_.personaRef)).where(predicateFinal);

    log.debug("isPersonaBaremable(personaRef, fechaBaremacion) - end");
    return !entityManager.createQuery(cq).getResultList().isEmpty();
  }

  /**
   * Devuelve una lista de {@link GrupoEquipo} pertenecientes a un determinado
   * grupo y que estén a 31 de diciembre del año de baremación
   *
   * @param grupoRef        grupoRef
   * @param fechaBaremacion fecha de baremación
   * 
   * @return Lista de {@link GrupoEquipo}
   */
  @Override
  public List<GrupoEquipoDto> findByGrupoIdAndAnio(Long grupoRef, Instant fechaBaremacion) {
    log.debug("findByGrupoIdAndAnio(grupoRef, fechaBaremacion) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<GrupoEquipoDto> cq = cb.createQuery(GrupoEquipoDto.class);

    Root<GrupoEquipo> root = cq.from(GrupoEquipo.class);
    Join<GrupoEquipo, Grupo> joinGrupo = root.join(GrupoEquipo_.grupo);

    Root<GrupoEspecialInvestigacion> rootGrupoEspecialInvestigacion = cq.from(GrupoEspecialInvestigacion.class);
    Predicate predicateIsGrupoRef = cb.equal(joinGrupo.get(Grupo_.id), grupoRef);

    Predicate predicateIsBaremable = getPredicatesIsBaremable(fechaBaremacion, cb, root, joinGrupo,
        rootGrupoEspecialInvestigacion);

    cq.multiselect(root.get(GrupoEquipo_.id),
        root.get(GrupoEquipo_.personaRef),
        root.get(GrupoEquipo_.fechaInicio),
        root.get(GrupoEquipo_.fechaFin),
        root.get(GrupoEquipo_.participacion));
    cq.where(cb.and(predicateIsGrupoRef, predicateIsBaremable));

    TypedQuery<GrupoEquipoDto> typedQuery = entityManager.createQuery(cq);
    List<GrupoEquipoDto> result = typedQuery.getResultList();

    log.debug("findByGrupoIdAndAnio(grupoRef, fechaBaremacion) - ends");

    return result;
  }

  private Predicate getPredicatesIsBaremable(Instant fechaBaremacion, CriteriaBuilder cb, Root<GrupoEquipo> root,
      Join<GrupoEquipo, Grupo> joinGrupo, Root<GrupoEspecialInvestigacion> rootGrupoEspecialInvestigacion) {

    Join<GrupoEquipo, RolProyecto> joinRolProyecto = root.join(GrupoEquipo_.rol);

    Predicate predicateJoinGrupoEspecialInvestigacion = cb.equal(
        rootGrupoEspecialInvestigacion.get(GrupoEspecialInvestigacion_.grupoId), joinGrupo.get(Grupo_.id));

    Predicate predicateGrupoIsActivo = cb.equal(joinGrupo.get(Activable_.activo), Boolean.TRUE);
    Predicate predicateRolProyectoIsActivo = cb.equal(joinRolProyecto.get(RolProyecto_.activo), Boolean.TRUE);
    Predicate predicateRolProyectoIsBaremable = cb.equal(joinRolProyecto.get(RolProyecto_.baremablePRC), Boolean.TRUE);
    Predicate predicateGrupoEspecialInvestigacionIsFalse = cb.equal(
        rootGrupoEspecialInvestigacion.get(GrupoEspecialInvestigacion_.especialInvestigacion), Boolean.FALSE);

    Predicate predicateGrupoEquipoInFechaBaremacion = cb.and(
        cb.lessThanOrEqualTo(root.get(GrupoEquipo_.fechaInicio), fechaBaremacion),
        cb.and(cb.or(cb.isNull(root.get(GrupoEquipo_.fechaFin)),
            cb.greaterThanOrEqualTo(root.get(GrupoEquipo_.fechaFin), fechaBaremacion))));

    Predicate predicateGrupoInFechaBaremacion = cb.and(
        cb.lessThanOrEqualTo(joinGrupo.get(Grupo_.fechaInicio), fechaBaremacion),
        cb.and(cb.or(cb.isNull(joinGrupo.get(Grupo_.fechaFin)),
            cb.greaterThanOrEqualTo(joinGrupo.get(Grupo_.fechaFin), fechaBaremacion))));

    Predicate predicateGrupoEspecialInvestigacionInFechaBaremacion = cb.and(
        cb.lessThanOrEqualTo(rootGrupoEspecialInvestigacion.get(GrupoEspecialInvestigacion_.fechaInicio),
            fechaBaremacion),
        cb.and(cb.or(cb.isNull(rootGrupoEspecialInvestigacion.get(GrupoEspecialInvestigacion_.fechaFin)),
            cb.greaterThanOrEqualTo(
                rootGrupoEspecialInvestigacion.get(GrupoEspecialInvestigacion_.fechaFin), fechaBaremacion))));

    return cb.and(
        predicateJoinGrupoEspecialInvestigacion,
        predicateGrupoIsActivo,
        predicateRolProyectoIsActivo,
        predicateRolProyectoIsBaremable,
        predicateGrupoEspecialInvestigacionIsFalse,
        predicateGrupoEquipoInFechaBaremacion,
        predicateGrupoInFechaBaremacion,
        predicateGrupoEspecialInvestigacionInFechaBaremacion);
  }
}