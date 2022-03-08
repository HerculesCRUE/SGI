package org.crue.hercules.sgi.eti.repository.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion_;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria_;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository para {@link PeticionEvaluacion}.
 */
@Slf4j
@Component
public class CustomPeticionEvaluacionRepositoryImpl implements CustomPeticionEvaluacionRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Page<PeticionEvaluacionWithIsEliminable> findAllPeticionEvaluacionMemoria(Specification<Memoria> specsMem,
      Specification<PeticionEvaluacion> specsPet, Pageable pageable, String personaRefConsulta) {
    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<PeticionEvaluacionWithIsEliminable> cq = cb.createQuery(PeticionEvaluacionWithIsEliminable.class);
    Root<PeticionEvaluacion> root = cq.from(PeticionEvaluacion.class);

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<PeticionEvaluacion> rootCount = countQuery.from(PeticionEvaluacion.class);
    countQuery.select(cb.count(rootCount));

    Predicate predicateMemoria = cb.in(root.get(PeticionEvaluacion_.id))
        .value(getIdsPeticionEvaluacionMemoria(root, cb, cq, specsMem, personaRefConsulta));
    Predicate predicateMemoriaCount = cb.in(rootCount.get(PeticionEvaluacion_.id))
        .value(getIdsPeticionEvaluacionMemoria(rootCount, cb, cq, specsMem, personaRefConsulta));

    Predicate predicatePersonaRef = null;
    Predicate predicatePersonaRefCount = null;
    if (personaRefConsulta != null) {
      predicatePersonaRef = cb.equal(root.get(PeticionEvaluacion_.personaRef), personaRefConsulta);
      predicatePersonaRefCount = cb.equal(rootCount.get(PeticionEvaluacion_.personaRef), personaRefConsulta);
    }

    List<Predicate> predicates = new ArrayList<Predicate>();
    List<Predicate> predicatesCount = new ArrayList<Predicate>();
    // Where
    if (specsMem != null) {
      if (predicatePersonaRef != null) {
        predicates.add(cb.or(predicateMemoria, cb.and(predicatePersonaRef,
            cb.isTrue(root.get(PeticionEvaluacion_.activo)), specsPet.toPredicate(root, cq, cb))));
        predicatesCount.add(cb.or(predicateMemoriaCount, cb.and(predicatePersonaRefCount,
            cb.isTrue(rootCount.get(PeticionEvaluacion_.activo)), specsPet.toPredicate(rootCount, cq, cb))));
      } else {
        predicates.add(cb.or(predicateMemoria,
            cb.and(cb.isTrue(root.get(PeticionEvaluacion_.activo)), specsPet.toPredicate(root, cq, cb))));
        predicatesCount.add(cb.or(predicateMemoriaCount,
            cb.and(cb.isTrue(rootCount.get(PeticionEvaluacion_.activo)), specsPet.toPredicate(rootCount, cq, cb))));
      }
    } else {
      if (predicatePersonaRef != null) {
        predicates
            .add(cb.or(predicateMemoria, cb.and(predicatePersonaRef, cb.isTrue(root.get(PeticionEvaluacion_.activo)))));
        predicatesCount.add(cb.or(predicateMemoriaCount,
            cb.and(predicatePersonaRefCount, cb.isTrue(rootCount.get(PeticionEvaluacion_.activo)))));
      } else {
        predicates.add(cb.or(predicateMemoria, cb.isTrue(root.get(PeticionEvaluacion_.activo))));
        predicatesCount.add(cb.or(predicateMemoriaCount, cb.isTrue(rootCount.get(PeticionEvaluacion_.activo))));
      }
    }

    cq.multiselect(root.alias("peticionEvaluacion"),
        cb.selectCase().when(cb.isNull(isNotEliminable(root, cb, cq, personaRefConsulta)), true).otherwise(false)
            .alias("eliminable"));

    cq.where(predicates.toArray(new Predicate[] {}));
    countQuery.where(predicatesCount.toArray(new Predicate[] {}));

    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    Long count = entityManager.createQuery(countQuery).getSingleResult();
    TypedQuery<PeticionEvaluacionWithIsEliminable> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<PeticionEvaluacionWithIsEliminable> result = typedQuery.getResultList();
    Page<PeticionEvaluacionWithIsEliminable> returnValue = new PageImpl<PeticionEvaluacionWithIsEliminable>(result,
        pageable, count);
    return returnValue;
  }

  /**
   * Obtiene las peticiones de evaluación filtradas por memoria
   * 
   * @param root
   * @param cb
   * @param cq
   * @param specsMem
   * @param personaRef
   * @return
   */
  private Subquery<Long> getIdsPeticionEvaluacionMemoria(Root<PeticionEvaluacion> root, CriteriaBuilder cb,
      CriteriaQuery<PeticionEvaluacionWithIsEliminable> cq, Specification<Memoria> specsMem, String personaRef) {

    log.debug(
        "getIdsPeticionEvaluacionMemoria(Root<PeticionEvaluacion> root, CriteriaBuilder cb, CriteriaQuery<PeticionEvaluacionWithIsEliminable> cq, Specification<Memoria> specsMem, String personaRef) - start");

    Subquery<Long> queryGetIdPeticionEvaluacion = cq.subquery(Long.class);
    Root<Memoria> subqRoot = queryGetIdPeticionEvaluacion.from(Memoria.class);

    List<Predicate> predicates = new ArrayList<Predicate>();
    predicates.add(cb.isTrue(subqRoot.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.activo)));
    predicates.add(cb.isTrue(subqRoot.get(Memoria_.activo)));

    if (specsMem != null) {
      if (personaRef != null) {
        predicates.add(cb.or(cb.equal(subqRoot.get(Memoria_.personaRef), personaRef),
            cb.equal(subqRoot.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.personaRef), personaRef)));
      }
      predicates.add(specsMem.toPredicate(subqRoot, cq, cb));
    } else {
      if (personaRef != null) {
        predicates.add(cb.equal(subqRoot.get(Memoria_.personaRef), personaRef));
      }
    }

    queryGetIdPeticionEvaluacion.select(subqRoot.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id))
        .where(predicates.toArray(new Predicate[] {}));
    log.debug(
        "getIdsPeticionEvaluacionMemoria(Root<PeticionEvaluacion> root, CriteriaBuilder cb, CriteriaQuery<PeticionEvaluacionWithIsEliminable> cq, Specification<Memoria> specsMem, String personaRef) - end");

    return queryGetIdPeticionEvaluacion;
  }

  /**
   * Informa si una petición de evaluación se puede eliminar o no
   * 
   * @param root
   * @param cb
   * @param cq
   * @return
   */

  private Subquery<Long> isNotEliminable(Root<PeticionEvaluacion> root, CriteriaBuilder cb,
      CriteriaQuery<PeticionEvaluacionWithIsEliminable> cq, String personaRefConsulta) {
    log.debug("isNotEliminable : {} - start");

    Subquery<Long> queryNotEliminable = cq.subquery(Long.class);
    Root<Memoria> rootQueryNotEliminable = queryNotEliminable.from(Memoria.class);

    queryNotEliminable.select(cb.min(rootQueryNotEliminable.get(Memoria_.id)))
        .where(
            cb.or(
                cb.equal(root.get(PeticionEvaluacion_.personaRef), personaRefConsulta).not(),
                cb.not(rootQueryNotEliminable.get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id)
                    .in(Arrays.asList(Constantes.TIPO_ESTADO_MEMORIA_EN_ELABORACION,
                        Constantes.TIPO_ESTADO_MEMORIA_COMPLETADA)))),
            cb.equal(rootQueryNotEliminable.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id),
                root.get(PeticionEvaluacion_.id)));

    log.debug("isNotEliminable : {} - end");

    return queryNotEliminable;
  }

}