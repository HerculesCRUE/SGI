package org.crue.hercules.sgi.csp.repository.custom;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoEjecucionEconomica;
import org.crue.hercules.sgi.csp.dto.RelacionEjecucionEconomica;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.repository.predicate.ProyectoProyectoSgePredicateResolver;
import org.crue.hercules.sgi.csp.util.CriteriaQueryUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository para {@link ProyectoProyectoSge}.
 */
@Slf4j
@Component
public class CustomProyectoProyectoSgeRepositoryImpl implements CustomProyectoProyectoSgeRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtiene datos economicos de los {@link ProyectoProyectoSge}
   * 
   * @param specification condiciones que deben cumplir.
   * @param pageable      paginación.
   * @return el listado de entidades {@link RelacionEjecucionEconomica} paginadas
   *         y filtradas.
   */
  @Override
  public Page<RelacionEjecucionEconomica> findRelacionesEjecucionEconomica(
      Specification<ProyectoProyectoSge> specification,
      Pageable pageable) {
    log.debug(
        "findRelacionesEjecucionEconomica(Specification<ProyectoProyectoSge> specification, Pageable pageable) - start");

    // Find query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<RelacionEjecucionEconomica> cq = cb.createQuery(RelacionEjecucionEconomica.class);
    Root<ProyectoProyectoSge> root = cq.from(ProyectoProyectoSge.class);
    List<Predicate> listPredicates = new ArrayList<>();

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<ProyectoProyectoSge> rootCount = countQuery.from(ProyectoProyectoSge.class);
    List<Predicate> listPredicatesCount = new ArrayList<>();

    // Select
    String[] selectionNames = new String[] {
        ProyectoProyectoSgePredicateResolver.Property.NOMBRE_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.FECHA_INICIO_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_PROYECTO.getCode()
    };

    cq.multiselect(root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.id).alias(Proyecto_.ID),
        root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.titulo).alias(
            ProyectoProyectoSgePredicateResolver.Property.NOMBRE_PROYECTO.getCode()),
        root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.fechaInicio).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_INICIO_PROYECTO.getCode()),
        root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.fechaFin).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_PROYECTO.getCode()),
        root.get(ProyectoProyectoSge_.proyectoSgeRef),
        cb.literal(RelacionEjecucionEconomica.TipoEntidad.PROYECTO.toString()));

    countQuery.select(cb.count(rootCount));

    // Where
    if (specification != null) {
      listPredicates.add(specification.toPredicate(root, cq, cb));
      listPredicatesCount.add(specification.toPredicate(rootCount, countQuery, cb));
    }

    cq.where(listPredicatesCount.toArray(new Predicate[] {}));
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));

    // Order
    cq.orderBy(CriteriaQueryUtils.toOrders(pageable.getSort(), root, cb, cq, selectionNames));

    // Número de registros totales para la paginación
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<RelacionEjecucionEconomica> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<RelacionEjecucionEconomica> results = typedQuery.getResultList();
    Page<RelacionEjecucionEconomica> returnValue = new PageImpl<>(results, pageable, count);

    log.debug(
        "findRelacionesEjecucionEconomica(Specification<ProyectoProyectoSge> specification, Pageable pageable) - end");
    return returnValue;
  }

  @Override
  public Page<ProyectoSeguimientoEjecucionEconomica> findProyectosSeguimientoEjecucionEconomica(
      Specification<ProyectoProyectoSge> specification, Pageable pageable) {
    log.debug(
        "findProyectosSeguimientoEjecucionEconomica(Specification<ProyectoProyectoSge> specification, Pageable pageable) - start");

    // Find query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ProyectoSeguimientoEjecucionEconomica> cq = cb
        .createQuery(ProyectoSeguimientoEjecucionEconomica.class);
    Root<ProyectoProyectoSge> root = cq.from(ProyectoProyectoSge.class);
    List<Predicate> listPredicates = new ArrayList<>();

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<ProyectoProyectoSge> rootCount = countQuery.from(ProyectoProyectoSge.class);
    List<Predicate> listPredicatesCount = new ArrayList<>();

    // Select
    String[] selectionNames = new String[] {
        ProyectoProyectoSgePredicateResolver.Property.NOMBRE_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.FECHA_INICIO_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.CODIGO_EXTERNO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.TITULO_CONVOCATORIA.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.IMPORTE_CONCEDIDO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.IMPORTE_CONCEDIDO_COSTES_INDIRECTOS.getCode(),
    };

    Join<ProyectoProyectoSge, Proyecto> joinProyecto = root.join(ProyectoProyectoSge_.proyecto);
    Join<Proyecto, Convocatoria> joinConvocatoria = joinProyecto.join(Proyecto_.convocatoria, JoinType.LEFT);

    cq.multiselect(
        root.get(ProyectoProyectoSge_.id),
        root.get(ProyectoProyectoSge_.proyectoId),
        root.get(ProyectoProyectoSge_.proyectoSgeRef),
        joinProyecto.get(Proyecto_.titulo).alias(
            ProyectoProyectoSgePredicateResolver.Property.NOMBRE_PROYECTO.getCode()),
        joinProyecto.get(Proyecto_.codigoExterno).alias(
            ProyectoProyectoSgePredicateResolver.Property.CODIGO_EXTERNO.getCode()),
        joinProyecto.get(Proyecto_.fechaInicio).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_INICIO_PROYECTO.getCode()),
        joinProyecto.get(Proyecto_.fechaFin).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_PROYECTO.getCode()),
        joinConvocatoria.get(Convocatoria_.titulo).alias(
            ProyectoProyectoSgePredicateResolver.Property.TITULO_CONVOCATORIA.getCode()),
        joinProyecto.get(Proyecto_.importeConcedido).alias(
            ProyectoProyectoSgePredicateResolver.Property.IMPORTE_CONCEDIDO.getCode()),
        joinProyecto.get(Proyecto_.importeConcedidoCostesIndirectos).alias(
            ProyectoProyectoSgePredicateResolver.Property.IMPORTE_CONCEDIDO_COSTES_INDIRECTOS.getCode()));

    countQuery.select(cb.count(rootCount));

    // Where
    if (specification != null) {
      listPredicates.add(specification.toPredicate(root, cq, cb));
      listPredicatesCount.add(specification.toPredicate(rootCount, countQuery, cb));
    }

    cq.where(listPredicates.toArray(new Predicate[] {}));
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));

    // Order
    cq.orderBy(CriteriaQueryUtils.toOrders(pageable.getSort(), root, cb, cq, selectionNames));

    // Número de registros totales para la paginación
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<ProyectoSeguimientoEjecucionEconomica> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<ProyectoSeguimientoEjecucionEconomica> results = typedQuery.getResultList();
    Page<ProyectoSeguimientoEjecucionEconomica> returnValue = new PageImpl<>(results, pageable, count);

    log.debug(
        "findProyectosSeguimientoEjecucionEconomica(Specification<ProyectoProyectoSge> specification, Pageable pageable) - end");
    return returnValue;
  }
}
