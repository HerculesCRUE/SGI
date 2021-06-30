package org.crue.hercules.sgi.eti.repository.custom;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.eti.dto.ActaWithNumEvaluaciones;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Acta_;
import org.crue.hercules.sgi.eti.model.Comite_;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluacion_;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion_;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link Acta}.
 */
@Slf4j
@Component
public class CustomActaRepositoryImpl implements CustomActaRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Devuelve una lista paginada y filtrada {@link ActaWithNumEvaluaciones}.
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de {@link ActaWithNumEvaluaciones} paginadas y/o filtradas.
   */
  public Page<ActaWithNumEvaluaciones> findAllActaWithNumEvaluaciones(String query, Pageable pageable) {
    log.debug("findAllActaWithNumEvaluaciones(String query, Pageable paging) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ActaWithNumEvaluaciones> cq = cb.createQuery(ActaWithNumEvaluaciones.class);

    // Define FROM clause
    Root<Acta> root = cq.from(Acta.class);

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Acta> rootCount = countQuery.from(Acta.class);
    countQuery.select(cb.count(rootCount));

    List<Predicate> listPredicates = new ArrayList<Predicate>();
    List<Predicate> listPredicatesCount = new ArrayList<Predicate>();

    listPredicates.add(cb.and(cb.equal(root.get(Acta_.activo), Boolean.TRUE)));
    listPredicatesCount.add(cb.and(cb.equal(rootCount.get(Acta_.activo), Boolean.TRUE)));

    // Where
    if (query != null) {
      Specification<Acta> spec = SgiRSQLJPASupport.toSpecification(query);
      listPredicates.add(spec.toPredicate(root, cq, cb));
      listPredicatesCount.add(spec.toPredicate(rootCount, cq, cb));
    }

    cq.where(listPredicates.toArray(new Predicate[] {}));

    // Execute query
    cq.multiselect(root.get(Acta_.id).alias("id"),
        root.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.comite).get(Comite_.comite).alias("comite"),
        root.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.fechaEvaluacion).alias("fechaEvaluacion"),
        root.get(Acta_.numero).alias("numeroActa"),
        root.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.tipoConvocatoriaReunion)
            .get(TipoConvocatoriaReunion_.nombre).alias("convocatoria"),
        getNumEvaluaciones(root, cb, cq, Boolean.TRUE).alias("numEvaluaciones"),
        getNumEvaluaciones(root, cb, cq, Boolean.FALSE).alias("numRevisiones"),
        getNumEvaluacionesNoEvaluadas(root, cb, cq).alias("evaluacionesEvaluadas"),
        root.get(Acta_.estadoActual).alias("estadoActa"));

    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    // Número de registros totales para la paginación
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<ActaWithNumEvaluaciones> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<ActaWithNumEvaluaciones> result = typedQuery.getResultList();
    Page<ActaWithNumEvaluaciones> returnValue = new PageImpl<ActaWithNumEvaluaciones>(result, pageable, count);

    log.debug("findAllActaWithNumEvaluaciones(String query, Pageable paging) - end");

    return returnValue;
  }

  /**
   * Devuelve una subconsulta con el número de memorias iniciales/nuevas (campo
   * versión con valor 1 ) o reevaluadas (campo versión con valor > 1 ) que se van
   * a revisar o se han revisado en la reunión de evaluación asociada a la acta.Se
   * obtiene sumando el número de evaluaciones asociadas a la convocatoria del
   * acta que son de tipo Memoria y revisión mínima sea false
   * 
   * 
   * @param root      Query root
   * @param cb        Criteria builder
   * @param cq        Criteria query
   * @param iniciales true=iniciales, false=reevaluadas
   * @return Subquery<Long>
   */
  private Subquery<Long> getNumEvaluaciones(Root<Acta> root, CriteriaBuilder cb,
      CriteriaQuery<ActaWithNumEvaluaciones> cq, boolean iniciales) {

    log.debug(
        "getNumEvaluaciones(Root<Acta> root, CriteriaBuilder cb, CriteriaQuery<ActaWithNumEvaluaciones> cq, boolean iniciales) - start");

    Long tipoEvaluacionMemoria = 2L;

    Subquery<Long> queryNumEvaluaciones = cq.subquery(Long.class);
    Root<Evaluacion> subqRoot = queryNumEvaluaciones.from(Evaluacion.class);
    queryNumEvaluaciones.select(cb.countDistinct(subqRoot.get(Evaluacion_.id)))
        .where(cb.and(
            cb.equal(subqRoot.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.id),
                root.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.id)),
            cb.equal(subqRoot.get(Evaluacion_.tipoEvaluacion), tipoEvaluacionMemoria),
            (iniciales) ? cb.equal(subqRoot.get(Evaluacion_.version), 1)
                : cb.greaterThan(subqRoot.get(Evaluacion_.version), 1),
            cb.equal(subqRoot.get(Evaluacion_.esRevMinima), false)));

    log.debug(
        "getNumEvaluaciones(Root<Acta> root, CriteriaBuilder cb, CriteriaQuery<ActaWithNumEvaluaciones> cq, boolean iniciales) - end");

    return queryNumEvaluaciones;
  }

  /**
   * Recupera el número evaluaciones que no se encuentren evaluadas, es decir, que
   * tengan un dictamen y revisión mínima sea false
   * 
   * @param root Query root
   * @param cb   Criteria builder
   * @param cq   Criteria query
   * @return número de evaluaciones evaluadas.
   */
  private Subquery<Long> getNumEvaluacionesNoEvaluadas(Root<Acta> root, CriteriaBuilder cb,
      CriteriaQuery<ActaWithNumEvaluaciones> cq) {

    log.debug(
        "getNumEvaluacionesNoEvaluadas(Root<Acta> root, CriteriaBuilder cb, CriteriaQuery<ActaWithNumEvaluaciones> cq) - start");

    Subquery<Long> queryNumEvaluaciones = cq.subquery(Long.class);
    Root<Evaluacion> subqRoot = queryNumEvaluaciones.from(Evaluacion.class);
    queryNumEvaluaciones.select(cb.count(subqRoot.get(Evaluacion_.id)))
        .where(cb.and(cb.isNull(subqRoot.get(Evaluacion_.dictamen)), cb.isTrue(subqRoot.get(Evaluacion_.activo)),
            cb.equal(subqRoot.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.id),
                root.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.id)),
            cb.equal(subqRoot.get(Evaluacion_.esRevMinima), false)));
    log.debug(
        "getNumEvaluacionesNoEvaluadas(Root<Acta> root, CriteriaBuilder cb, CriteriaQuery<ActaWithNumEvaluaciones> cq) - end");
    return queryNumEvaluaciones;
  }

}