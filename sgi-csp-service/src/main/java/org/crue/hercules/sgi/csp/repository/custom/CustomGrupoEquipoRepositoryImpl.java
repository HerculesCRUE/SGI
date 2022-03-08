package org.crue.hercules.sgi.csp.repository.custom;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo_;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
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
   * tiene el {@link RolProyecto} con el flag "principal" a
   * <code>true</code>. En caso de existir mas de un {@link GrupoEquipo}, se
   * recupera el que tenga el mayor porcentaje de dedicación al grupo (campo
   * "participación").
   * Y en caso de que varios coincidan se devuelven todos los que coincidan.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @param fecha   fecha en la que se busca el investigador principal.
   * @return la lista de personaRef de losinvestigadores principales del
   *         {@link Grupo} en el momento actual.
   */
  @Override
  public List<String> findPersonaRefInvestigadoresPrincipales(Long grupoId, Instant fecha) {
    log.debug("findPersonaRefInvestigadoresPrincipales() - start");

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

    log.debug("findPersonaRefInvestigadoresPrincipales() - end");
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

}
