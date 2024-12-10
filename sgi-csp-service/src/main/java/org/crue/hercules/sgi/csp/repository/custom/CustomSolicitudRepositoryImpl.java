package org.crue.hercules.sgi.csp.repository.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud_;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.crue.hercules.sgi.csp.util.CriteriaQueryUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA custom repository para {@link Solicitud}.
 */
@Slf4j
@Component
public class CustomSolicitudRepositoryImpl implements CustomSolicitudRepository {

  private static final String SELECTION_NAME_SEPARATOR = ".";
  private static final String SELECTION_NAME_ESTADO_ESTADO = Solicitud_.ESTADO + SELECTION_NAME_SEPARATOR
      + EstadoSolicitud_.ESTADO;
  private static final String SELECTION_NAME_ESTADO_FECHAESTADO = Solicitud_.ESTADO + SELECTION_NAME_SEPARATOR
      + EstadoSolicitud_.FECHA_ESTADO;

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtiene los ids de {@link Solicitud} que cumplen con la specification
   * recibida.
   * 
   * @param specification condiciones que deben cumplir.
   * @return lista de ids de {@link Solicitud}.
   */
  @Override
  public List<Long> findIds(Specification<Solicitud> specification) {
    log.debug("findIds(Specification<Solicitud> specification) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Solicitud> root = cq.from(Solicitud.class);

    cq.select(root.get(Solicitud_.id)).distinct(true).where(specification.toPredicate(root, cq, cb));

    log.debug("findIds(Specification<Solicitud> specification) - end");

    return entityManager.createQuery(cq).getResultList();
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Solicitud} sin duplicados y
   * ordenable por el estaod y la fecha del estado.
   * 
   * @param specs    condiciones que deben cumplir.
   * @param pageable la información de la paginación.
   * @return la lista de {@link Solicitud} paginadas y/o filtradas.
   */
  @Override
  public Page<Solicitud> findAllDistinct(Specification<Solicitud> specs, Pageable pageable) {
    log.debug("findAllDistinct(String query, Pageable pageable) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> cq = cb.createTupleQuery();

    // Define FROM clause
    Root<Solicitud> root = cq.from(Solicitud.class);
    Join<Solicitud, EstadoSolicitud> join = root.join(Solicitud_.estado, JoinType.INNER);

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Solicitud> rootCount = countQuery.from(Solicitud.class);
    countQuery.select(cb.countDistinct(rootCount));

    List<Predicate> listPredicates = new ArrayList<>();
    List<Predicate> listPredicatesCount = new ArrayList<>();

    // Where
    if (specs != null) {
      listPredicates.add(specs.toPredicate(root, cq, cb));
      listPredicatesCount.add(specs.toPredicate(rootCount, cq, cb));
    }

    cq.where(listPredicates.toArray(new Predicate[] {}));

    // Execute query
    cq.distinct(true).multiselect(
        root,
        join.get(EstadoSolicitud_.estado).alias(SELECTION_NAME_ESTADO_ESTADO),
        join.get(EstadoSolicitud_.fechaEstado).alias(SELECTION_NAME_ESTADO_FECHAESTADO));

    String[] selectionNames = new String[] {
        SELECTION_NAME_ESTADO_ESTADO,
        SELECTION_NAME_ESTADO_FECHAESTADO
    };

    cq.orderBy(CriteriaQueryUtils.toOrders(pageable.getSort(), root, cb, cq, selectionNames));

    // Número de registros totales para la paginación
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<Tuple> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<Solicitud> result = typedQuery.getResultList().stream().map(a -> (Solicitud) a.get(0))
        .collect(Collectors.toList());
    Page<Solicitud> returnValue = new PageImpl<>(result, pageable, count);

    log.debug("findAllDistinct(String query, Pageable pageable) - end");

    return returnValue;
  }

}
