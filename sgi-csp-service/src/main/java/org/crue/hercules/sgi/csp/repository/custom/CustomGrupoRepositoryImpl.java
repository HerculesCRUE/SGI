package org.crue.hercules.sgi.csp.repository.custom;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.dto.GrupoDto;
import org.crue.hercules.sgi.csp.dto.RelacionEjecucionEconomica;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion_;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA custom repository para {@link Grupo}.
 */
@Slf4j
@Component
public class CustomGrupoRepositoryImpl implements CustomGrupoRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Devuelve si grupoRef pertenece a un grupo de investigación con el campo
   * "Grupo especial de investigación" a "No" el 31 de diciembre del
   * año que se esta baremando
   *
   * @param grupoRef        grupoRef
   * @param fechaBaremacion fecha de baremación
   * @return true/false
   */
  @Override
  public boolean isGrupoBaremable(Long grupoRef, Instant fechaBaremacion) {
    log.debug("isGrupoBaremable(grupoRef, fechaBaremacion) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Grupo> root = cq.from(Grupo.class);

    Root<GrupoEspecialInvestigacion> rootGrupoEspecialInvestigacion = cq.from(GrupoEspecialInvestigacion.class);
    Predicate predicateIsGrupoRef = cb.equal(root.get(Grupo_.id), grupoRef);

    Predicate predicateIsBaremable = getPredicatesIsBaremable(fechaBaremacion, cb, root,
        rootGrupoEspecialInvestigacion);

    Predicate predicateFinal = cb.and(predicateIsGrupoRef, predicateIsBaremable);

    cq.select(root.get(Grupo_.id)).where(predicateFinal);

    log.debug("isGrupoBaremable(isGrupoBaremable, fechaBaremacion) - end");

    return !entityManager.createQuery(cq).getResultList().isEmpty();
  }

  /**
   * Devuelve una lista de {@link GrupoDto} pertenecientes a un determinado
   * grupo y que estén a 31 de diciembre del año de baremación
   *
   * @param fechaBaremacion fecha de baremación
   * 
   * @return Lista de {@link GrupoDto}
   */
  @Override
  public List<GrupoDto> findAllByAnio(Instant fechaBaremacion) {
    log.debug("findAllByAnio(fechaBaremacion) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<GrupoDto> cq = cb.createQuery(GrupoDto.class);

    Root<Grupo> root = cq.from(Grupo.class);

    Root<GrupoEspecialInvestigacion> rootGrupoEspecialInvestigacion = cq.from(GrupoEspecialInvestigacion.class);

    cq.multiselect(root.get(Grupo_.id),
        root.get(Grupo_.nombre),
        root.get(Grupo_.fechaInicio),
        root.get(Grupo_.fechaFin));

    Predicate predicateIsBaremable = getPredicatesIsBaremable(fechaBaremacion, cb, root,
        rootGrupoEspecialInvestigacion);

    cq.where(cb.and(predicateIsBaremable));

    TypedQuery<GrupoDto> typedQuery = entityManager.createQuery(cq);
    List<GrupoDto> result = typedQuery.getResultList();

    log.debug("findAllByAnio(fechaBaremacion) - end");

    return result;
  }

  /**
   * Obtiene datos economicos de los {@link Grupo}
   * 
   * @param specification condiciones que deben cumplir.
   * @param pageable      paginación.
   * @return el listado de entidades {@link RelacionEjecucionEconomica} paginadas
   *         y
   *         filtradas.
   */
  @Override
  public Page<RelacionEjecucionEconomica> findRelacionesEjecucionEconomica(Specification<Grupo> specification,
      Pageable pageable) {
    log.debug("findRelacionesEjecucionEconomica(Specification<Grupo> specification, Pageable pageable) - start");

    // Find query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<RelacionEjecucionEconomica> cq = cb.createQuery(RelacionEjecucionEconomica.class);
    Root<Grupo> root = cq.from(Grupo.class);
    List<Predicate> listPredicates = new ArrayList<>();

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Grupo> rootCount = countQuery.from(Grupo.class);
    List<Predicate> listPredicatesCount = new ArrayList<>();

    cq.multiselect(root.get(Grupo_.id),
        root.get(Grupo_.nombre),
        root.get(Grupo_.fechaInicio),
        root.get(Grupo_.fechaFin),
        root.get(Grupo_.proyectoSgeRef),
        cb.literal(RelacionEjecucionEconomica.TipoEntidad.GRUPO.toString()));

    countQuery.select(cb.count(rootCount));

    // Where
    if (specification != null) {
      listPredicates.add(specification.toPredicate(root, cq, cb));
      listPredicatesCount.add(specification.toPredicate(rootCount, countQuery, cb));
    }

    cq.where(listPredicatesCount.toArray(new Predicate[] {}));
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));

    // Order
    cq.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));

    // Número de registros totales para la paginación
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<RelacionEjecucionEconomica> query = entityManager.createQuery(cq);
    List<RelacionEjecucionEconomica> results = query.getResultList();
    Page<RelacionEjecucionEconomica> returnValue = new PageImpl<>(results, pageable, count);

    log.debug("findRelacionesEjecucionEconomica(Specification<Grupo> specification, Pageable pageable) - end");
    return returnValue;
  }

  private Predicate getPredicatesIsBaremable(Instant fechaBaremacion, CriteriaBuilder cb, Root<Grupo> root,
      Root<GrupoEspecialInvestigacion> rootGrupoEspecialInvestigacion) {
    Predicate predicateJoinGrupoEspecialInvestigacion = cb.equal(
        rootGrupoEspecialInvestigacion.get(GrupoEspecialInvestigacion_.grupoId), root.get(Grupo_.id));

    Predicate predicateGrupoIsActivo = cb.equal(root.get(Activable_.activo), Boolean.TRUE);
    Predicate predicateGrupoEspecialInvestigacionIsFalse = cb.equal(
        rootGrupoEspecialInvestigacion.get(GrupoEspecialInvestigacion_.especialInvestigacion), Boolean.FALSE);

    Predicate predicateGrupoInFechaBaremacion = cb.and(
        cb.lessThanOrEqualTo(root.get(Grupo_.fechaInicio), fechaBaremacion),
        cb.and(cb.or(cb.isNull(root.get(Grupo_.fechaFin)),
            cb.greaterThanOrEqualTo(root.get(Grupo_.fechaFin), fechaBaremacion))));

    return cb.and(
        predicateJoinGrupoEspecialInvestigacion,
        predicateGrupoIsActivo,
        predicateGrupoEspecialInvestigacionIsFalse,
        predicateGrupoInFechaBaremacion);
  }
}
