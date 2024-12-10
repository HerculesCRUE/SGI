package org.crue.hercules.sgi.csp.repository.custom;

import java.time.Instant;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA custom repository para {@link ProyectoEquipo}.
 */
@Slf4j
@Component
public class CustomProyectoEquipoRepositoryImpl implements CustomProyectoEquipoRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * {@link ProyectoEquipo} que son investigador o investigadores principales del
   * {@link Proyecto} con el id indicado.
   * 
   * Se considera investiador principal al {@link ProyectoEquipo} que a fecha
   * actual tiene el {@link RolProyecto} con el flag "principal" a true. En caso
   * de que varios coincidan se devuelven todos los que coincidan.
   * 
   * @param proyectoId identificador del {@link Proyecto}.
   * @param fecha      fecha en la que se busca el investigador principal.
   * @return la lista de investigadores principales del {@link Proyecto} en la
   *         fecha.
   */
  @Override
  public List<ProyectoEquipo> findInvestigadoresPrincipales(Long proyectoId, Instant fecha) {
    log.debug("findInvestigadoresPrincipales(Long proyectoId, Instant fecha) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ProyectoEquipo> cq = cb.createQuery(ProyectoEquipo.class);
    Root<ProyectoEquipo> root = cq.from(ProyectoEquipo.class);

    cq.select(root).where(cb.and(
        getPredicateRolPrincipalFecha(root, cb, proyectoId, fecha),
        getPredicateRolPrincipalFechaFin(root, cq, cb, proyectoId)))
        .distinct(true);

    List<ProyectoEquipo> returnValue = entityManager.createQuery(cq).getResultList();

    log.debug("findInvestigadoresPrincipales(Long proyectoId, Instant fecha) - end");
    return returnValue;
  }

  private Predicate getPredicateRolPrincipalFechaFin(Root<ProyectoEquipo> root, CriteriaQuery<?> cq, CriteriaBuilder cb,
      Long proyectoId) {

    Subquery<String> queryRolPrincipalFechaFinNull = cq.subquery(String.class);
    Root<ProyectoEquipo> subqRolPrincipalFechaFinNull = queryRolPrincipalFechaFinNull.from(ProyectoEquipo.class);
    Join<ProyectoEquipo, RolProyecto> joinRolProyectoFechaFinNull = subqRolPrincipalFechaFinNull
        .join(ProyectoEquipo_.rolProyecto);
    Predicate rolPrincipalFechaFinNull = cb
        .equal(joinRolProyectoFechaFinNull.get(RolProyecto_.rolPrincipal), true);
    Predicate grupoEqualsFechaFinNull = cb.equal(subqRolPrincipalFechaFinNull.get(ProyectoEquipo_.proyectoId),
        proyectoId);
    queryRolPrincipalFechaFinNull.select(subqRolPrincipalFechaFinNull.get(ProyectoEquipo_.personaRef))
        .where(cb.and(
            rolPrincipalFechaFinNull,
            grupoEqualsFechaFinNull,
            cb.isNull(subqRolPrincipalFechaFinNull.get(ProyectoEquipo_.fechaFin))));

    Subquery<Instant> queryMaxFechaFin = cq.subquery(Instant.class);
    Root<ProyectoEquipo> subqRootMaxFechaFin = queryMaxFechaFin.from(ProyectoEquipo.class);
    Join<ProyectoEquipo, RolProyecto> joinRolProyectoMaxFechaFin = subqRootMaxFechaFin
        .join(ProyectoEquipo_.rolProyecto);
    Predicate rolPrincipalMaxFechaFin = cb.equal(joinRolProyectoMaxFechaFin.get(RolProyecto_.rolPrincipal), true);
    Predicate grupoEqualsMaxFechaFin = cb.equal(subqRootMaxFechaFin.get(ProyectoEquipo_.proyectoId), proyectoId);

    queryMaxFechaFin.select(cb.greatest(subqRootMaxFechaFin.get(ProyectoEquipo_.fechaFin)))
        .where(cb.and(
            rolPrincipalMaxFechaFin,
            grupoEqualsMaxFechaFin));

    return cb.or(
        cb.and(
            root.get(ProyectoEquipo_.personaRef).in(queryRolPrincipalFechaFinNull)),
        cb.and(
            cb.exists(queryRolPrincipalFechaFinNull).not(),
            cb.equal(root.get(ProyectoEquipo_.fechaFin),
                queryMaxFechaFin)));
  }

  private Predicate getPredicateRolPrincipalFecha(Root<ProyectoEquipo> root, CriteriaBuilder cb, Long proyectoId,
      Instant fecha) {
    Predicate proyectoEquals = cb.equal(root.get(ProyectoEquipo_.proyectoId), proyectoId);
    Predicate rolPrincipal = cb.equal(root.get(ProyectoEquipo_.rolProyecto).get(RolProyecto_.rolPrincipal), true);
    Predicate greaterThanFechaInicio = cb.or(cb.isNull(root.get(ProyectoEquipo_.fechaInicio)),
        cb.lessThanOrEqualTo(root.get(ProyectoEquipo_.fechaInicio), fecha));
    Predicate lowerThanFechaFin = cb.or(cb.isNull(root.get(ProyectoEquipo_.fechaFin)),
        cb.greaterThanOrEqualTo(root.get(ProyectoEquipo_.fechaFin), fecha));

    Predicate fechaLowerThanFechaInicioProyecto = cb.greaterThan(
        root.get(ProyectoEquipo_.proyecto).get(Proyecto_.fechaInicio),
        fecha);
    Predicate fechaGreaterThanFechaFinProyecto = cb.lessThan(root.get(ProyectoEquipo_.proyecto).get(Proyecto_.fechaFin),
        fecha);

    return cb.and(
        proyectoEquals,
        rolPrincipal,
        cb.or(fechaLowerThanFechaInicioProyecto, greaterThanFechaInicio),
        cb.or(fechaGreaterThanFechaFinProyecto, lowerThanFechaFin));
  }

}
