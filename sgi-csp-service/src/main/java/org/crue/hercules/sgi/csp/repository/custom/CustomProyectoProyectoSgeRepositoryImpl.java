package org.crue.hercules.sgi.csp.repository.custom;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoEjecucionEconomica;
import org.crue.hercules.sgi.csp.dto.RelacionEjecucionEconomica;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaTitulo;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge_;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.repository.predicate.ProyectoProyectoSgePredicateResolver;
import org.crue.hercules.sgi.csp.util.CriteriaQueryUtils;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
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
    CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
    Root<ProyectoProyectoSge> root = cq.from(ProyectoProyectoSge.class);
    List<Predicate> listPredicates = new ArrayList<>();

    // Si se ordena por el titulo del proyecto se hace un subquery para obtener el
    // titulo en el idioma actual para poder hacer la ordenacion, si no se ordena
    // por el titulo no es necesaria la subquery
    boolean sortingByTituloProyecto = pageable.getSort().get().anyMatch(
        sort -> sort.getProperty().equals(ProyectoProyectoSgePredicateResolver.Property.NOMBRE_PROYECTO.getCode()));

    Expression<String> tituloProyectoExpression;
    if (sortingByTituloProyecto) {
      Subquery<String> subqueryTitulo = cq.subquery(String.class);
      Root<ProyectoProyectoSge> subRoot = subqueryTitulo.correlate(root);
      Join<ProyectoProyectoSge, Proyecto> joinProyecto = subRoot.join(ProyectoProyectoSge_.proyecto);
      Join<Proyecto, ProyectoTitulo> subTituloJoin = joinProyecto.join(Proyecto_.titulo);

      subqueryTitulo.select(subTituloJoin.get(ProyectoTitulo_.value))
          .where(cb.equal(subTituloJoin.get(ProyectoTitulo_.lang), SgiLocaleContextHolder.getLanguage()));

      tituloProyectoExpression = subqueryTitulo;
    } else {
      tituloProyectoExpression = cb.literal("");
    }

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<ProyectoProyectoSge> rootCount = countQuery.from(ProyectoProyectoSge.class);
    List<Predicate> listPredicatesCount = new ArrayList<>();

    // Select
    String[] selectionNames = new String[] {
        Proyecto_.ID,
        ProyectoProyectoSgePredicateResolver.Property.NOMBRE_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.FECHA_INICIO_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.CODIGO_EXTERNO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.CODIGO_INTERNO.getCode()
    };

    cq.distinct(true).multiselect(
        root.get(ProyectoProyectoSge_.proyecto),
        root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.id).alias(Proyecto_.ID),
        root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.codigoExterno).alias(
            ProyectoProyectoSgePredicateResolver.Property.CODIGO_EXTERNO.getCode()),
        root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.codigoInterno).alias(
            ProyectoProyectoSgePredicateResolver.Property.CODIGO_INTERNO.getCode()),
        root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.fechaInicio).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_INICIO_PROYECTO.getCode()),
        root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.fechaFin).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_PROYECTO.getCode()),
        root.get(ProyectoProyectoSge_.proyectoSgeRef).alias(ProyectoProyectoSge_.PROYECTO_SGE_REF),
        root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.fechaFinDefinitiva).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_DEFINITIVA_PROYECTO.getCode()),
        tituloProyectoExpression.alias(ProyectoProyectoSgePredicateResolver.Property.NOMBRE_PROYECTO.getCode()));

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

    TypedQuery<Tuple> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<RelacionEjecucionEconomica> results = typedQuery.getResultList().stream()
        .map(tuple -> {
          Proyecto proyecto = (Proyecto) tuple.get(0);

          return RelacionEjecucionEconomica.builder()
              .id(proyecto.getId())
              .nombre(proyecto.getTitulo().stream()
                  .map(titulo -> new I18nFieldValueDto(titulo.getLang(), titulo.getValue()))
                  .collect(Collectors.toSet()))
              .codigoExterno(proyecto.getCodigoExterno())
              .codigoInterno(proyecto.getCodigoInterno())
              .fechaInicio(proyecto.getFechaInicio())
              .fechaFin(proyecto.getFechaFin())
              .proyectoSgeRef((String) tuple.get(ProyectoProyectoSge_.PROYECTO_SGE_REF))
              .tipoEntidad(RelacionEjecucionEconomica.TipoEntidad.PROYECTO)
              .fechaFinDefinitiva(proyecto.getFechaFinDefinitiva())
              .build();
        }).toList();

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
    CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
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
        ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_DEFINITIVA_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.CODIGO_EXTERNO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.TITULO_CONVOCATORIA.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.IMPORTE_CONCEDIDO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.IMPORTE_CONCEDIDO_COSTES_INDIRECTOS.getCode(),
    };

    Join<ProyectoProyectoSge, Proyecto> joinProyecto = root.join(ProyectoProyectoSge_.proyecto);
    SetJoin<Proyecto, ProyectoTitulo> joinProyectoTitulo = joinProyecto.join(Proyecto_.titulo, JoinType.LEFT);
    Join<Proyecto, Convocatoria> joinConvocatoria = joinProyecto.join(Proyecto_.convocatoria, JoinType.LEFT);
    SetJoin<Convocatoria, ConvocatoriaTitulo> joinConvocatoriaTitulo = joinConvocatoria.join(Convocatoria_.titulo,
        JoinType.LEFT);

    cq.multiselect(
        root.get(ProyectoProyectoSge_.id).alias(ProyectoProyectoSge_.ID),
        root.get(ProyectoProyectoSge_.proyectoId).alias(ProyectoProyectoSge_.PROYECTO_ID),
        root.get(ProyectoProyectoSge_.proyectoSgeRef).alias(ProyectoProyectoSge_.PROYECTO_SGE_REF),
        joinProyectoTitulo.alias(ProyectoProyectoSgePredicateResolver.Property.NOMBRE_PROYECTO.getCode()),
        joinProyecto.get(Proyecto_.codigoExterno).alias(
            ProyectoProyectoSgePredicateResolver.Property.CODIGO_EXTERNO.getCode()),
        joinProyecto.get(Proyecto_.fechaInicio).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_INICIO_PROYECTO.getCode()),
        joinProyecto.get(Proyecto_.fechaFin).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_PROYECTO.getCode()),
        joinProyecto.get(Proyecto_.fechaFinDefinitiva).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_DEFINITIVA_PROYECTO.getCode()),
        joinConvocatoriaTitulo.alias(ProyectoProyectoSgePredicateResolver.Property.TITULO_CONVOCATORIA.getCode()),
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

    TypedQuery<Tuple> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<Tuple> results = typedQuery.getResultList();

    Map<Long, ProyectoSeguimientoEjecucionEconomica> tuplesMap = new HashMap<>();
    results.forEach(tuple -> {
      Long proyectoProyectoSgeId = tuple.get(ProyectoProyectoSge_.ID, Long.class);

      ProyectoSeguimientoEjecucionEconomica proyectoSeguimiento = tuplesMap.computeIfAbsent(proyectoProyectoSgeId,
          key -> ProyectoSeguimientoEjecucionEconomica.builder()
              .id(proyectoProyectoSgeId)
              .proyectoId(tuple.get(ProyectoProyectoSge_.PROYECTO_ID, Long.class))
              .proyectoSgeRef(tuple.get(ProyectoProyectoSge_.PROYECTO_SGE_REF, String.class))
              .nombre(new HashSet<>())
              .codigoExterno(
                  tuple.get(ProyectoProyectoSgePredicateResolver.Property.CODIGO_EXTERNO.getCode(), String.class))
              .fechaInicio(tuple.get(ProyectoProyectoSgePredicateResolver.Property.FECHA_INICIO_PROYECTO.getCode(),
                  Instant.class))
              .fechaFin(
                  tuple.get(ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_PROYECTO.getCode(), Instant.class))
              .fechaFinDefinitiva(tuple.get(
                  ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_DEFINITIVA_PROYECTO.getCode(), Instant.class))
              .importeConcedido(tuple.get(ProyectoProyectoSgePredicateResolver.Property.IMPORTE_CONCEDIDO.getCode(),
                  BigDecimal.class))
              .importeConcedidoCostesIndirectos(
                  tuple.get(ProyectoProyectoSgePredicateResolver.Property.IMPORTE_CONCEDIDO_COSTES_INDIRECTOS.getCode(),
                      BigDecimal.class))
              .tituloConvocatoria(new HashSet<>())
              .build());

      // Añade el titulo del proyecto de la tupla actual
      ProyectoTitulo tituloProyecto = tuple.get(
          ProyectoProyectoSgePredicateResolver.Property.NOMBRE_PROYECTO.getCode(), ProyectoTitulo.class);
      if (tituloProyecto != null) {
        proyectoSeguimiento.getNombre().add(tituloProyecto);
      }

      // Añade el titulo de la convocatoria de la tupla actual
      ConvocatoriaTitulo tituloConvocatoria = tuple.get(
          ProyectoProyectoSgePredicateResolver.Property.TITULO_CONVOCATORIA.getCode(), ConvocatoriaTitulo.class);
      if (tituloConvocatoria != null) {
        proyectoSeguimiento.getTituloConvocatoria().add(tituloConvocatoria);
      }
    });

    Page<ProyectoSeguimientoEjecucionEconomica> returnValue = new PageImpl<>(tuplesMap.values().stream().toList(),
        pageable, count);

    log.debug(
        "findProyectosSeguimientoEjecucionEconomica(Specification<ProyectoProyectoSge> specification, Pageable pageable) - end");
    return returnValue;
  }

}
