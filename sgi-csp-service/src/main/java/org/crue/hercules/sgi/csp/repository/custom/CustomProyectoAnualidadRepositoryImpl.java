package org.crue.hercules.sgi.csp.repository.custom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;

import org.crue.hercules.sgi.csp.dto.AnualidadResumen;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadNotificacionSge;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadResumen;
import org.crue.hercules.sgi.csp.enums.TipoPartida;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.AnualidadGasto_;
import org.crue.hercules.sgi.csp.model.AnualidadIngreso;
import org.crue.hercules.sgi.csp.model.AnualidadIngreso_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad_;
import org.crue.hercules.sgi.csp.model.ProyectoPartida_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link SolicitudProyectoSocio}.
 */
@Slf4j
@Component
public class CustomProyectoAnualidadRepositoryImpl implements CustomProyectoAnualidadRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Recupera todas las {@link ProyectoAnualidad} del proyecto recibido,
   * calculando el total de de gastos presupuesto, gastos concedidos y total
   * ingresos por anualidad.
   * 
   * @param proyectoId Id de la {@link Proyecto}.
   * @return Listado paginado de {@link ProyectoAnualidad}
   */
  public Page<ProyectoAnualidadResumen> findAllResumenByProyectoId(Long proyectoId, Pageable pageable) {
    log.debug("findAllResumenByProyectoId(Long proyectoId, Pageable pageable) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ProyectoAnualidadResumen> cq = cb.createQuery(ProyectoAnualidadResumen.class);
    Root<ProyectoAnualidad> root = cq.from(ProyectoAnualidad.class);
    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<ProyectoAnualidad> rootCount = countQuery.from(ProyectoAnualidad.class);
    countQuery.select(cb.count(rootCount));

    List<Predicate> listPredicates = new ArrayList<>();
    List<Predicate> listPredicatesCount = new ArrayList<>();

    listPredicates.add(cb.and(cb.equal(root.get(ProyectoAnualidad_.proyectoId), proyectoId)));
    listPredicatesCount.add(cb.and(cb.equal(rootCount.get(ProyectoAnualidad_.proyectoId), proyectoId)));

    cq.where(listPredicates.toArray(new Predicate[] {}));

    // Total Gasto Presupuesto
    Subquery<BigDecimal> queryTotalGastoPresupuesto = cq.subquery(BigDecimal.class);
    Root<AnualidadGasto> subqRoot = queryTotalGastoPresupuesto.from(AnualidadGasto.class);
    queryTotalGastoPresupuesto.select(cb.sum(subqRoot.get(AnualidadGasto_.importePresupuesto)))
        .where(cb.equal(subqRoot.get(AnualidadGasto_.proyectoAnualidadId), root.get(ProyectoAnualidad_.id)));

    // Total Gasto Concedido
    Subquery<BigDecimal> queryTotalGastoConcedido = cq.subquery(BigDecimal.class);
    Root<AnualidadGasto> subqRootConcedido = queryTotalGastoConcedido.from(AnualidadGasto.class);
    queryTotalGastoConcedido.select(cb.sum(subqRootConcedido.get(AnualidadGasto_.importeConcedido)))
        .where(cb.equal(subqRootConcedido.get(AnualidadGasto_.proyectoAnualidadId), root.get(ProyectoAnualidad_.id)));

    // Total Ingreso
    Subquery<BigDecimal> queryTotalIngreso = cq.subquery(BigDecimal.class);
    Root<AnualidadIngreso> subqRootIngreso = queryTotalIngreso.from(AnualidadIngreso.class);
    queryTotalIngreso.select(cb.sum(subqRootIngreso.get(AnualidadIngreso_.importeConcedido)))
        .where(cb.equal(subqRootIngreso.get(AnualidadIngreso_.proyectoAnualidadId), root.get(ProyectoAnualidad_.id)));

    // Execute query
    cq.multiselect(root.get(ProyectoAnualidad_.id).alias("id"), root.get(ProyectoAnualidad_.anio).alias("anio"),
        root.get(ProyectoAnualidad_.fechaInicio).alias("fechaInicio"),
        root.get(ProyectoAnualidad_.fechaFin).alias("fechaFin"),
        cb.coalesce(queryTotalGastoPresupuesto.getSelection(), new BigDecimal(0)).alias("totalGastosPresupuesto"),
        cb.coalesce(queryTotalGastoConcedido.getSelection(), new BigDecimal(0)).alias("totalGastosConcedido"),
        cb.coalesce(queryTotalIngreso.getSelection(), new BigDecimal(0)).alias("totalIngresos"),
        root.get(ProyectoAnualidad_.presupuestar).alias("presupuestar"),
        root.get(ProyectoAnualidad_.enviadoSge).alias("enviadoSge"));

    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    // Número de registros totales para la paginación
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<ProyectoAnualidadResumen> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<ProyectoAnualidadResumen> result = typedQuery.getResultList();
    Page<ProyectoAnualidadResumen> returnValue = new PageImpl<>(result, pageable, count);

    log.debug("findAllResumenByProyectoId(Long proyectoId, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Recupera el resumen de {@link AnualidadGasto} y {@link AnualidadIngreso}
   * ({@link AnualidadResumen}) de una {@link ProyectoAnualidad}.
   * 
   * @param proyectoAnualidadId Identificador de {@link ProyectoAnualidad}.
   * @return Listado del resumen de {@link AnualidadResumen}.
   */
  public List<AnualidadResumen> getPartidasResumen(Long proyectoAnualidadId) {
    log.debug("getPartidasResumen(Long proyectoAnualidadId) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<AnualidadResumen> cqAnualidadGasto = cb.createQuery(AnualidadResumen.class);
    Root<AnualidadGasto> rootGasto = cqAnualidadGasto.from(AnualidadGasto.class);

    List<Predicate> listPredicates = new ArrayList<>();
    listPredicates.add(cb.and(cb.equal(rootGasto.get(AnualidadGasto_.proyectoAnualidadId), proyectoAnualidadId)));
    cqAnualidadGasto.where(listPredicates.toArray(new Predicate[] {}));

    // Execute query
    cqAnualidadGasto
        .multiselect(
            rootGasto.get(AnualidadGasto_.proyectoPartida).get(ProyectoPartida_.codigo)
                .alias("codigoPartidaPresupuestaria"),
            cb.sum(rootGasto.get(AnualidadGasto_.importePresupuesto)).alias("importePresupuesto"),
            cb.sum(rootGasto.get(AnualidadGasto_.importeConcedido)).alias("importeConcedido"))
        .groupBy(rootGasto.get(AnualidadGasto_.proyectoPartida).get(ProyectoPartida_.id));

    // ANUALIDAD INGRESO
    CriteriaQuery<AnualidadResumen> cqAnualidadIngreso = cb.createQuery(AnualidadResumen.class);
    Root<AnualidadIngreso> rootIngreso = cqAnualidadIngreso.from(AnualidadIngreso.class);

    listPredicates = new ArrayList<>();
    listPredicates.add(cb.and(cb.equal(rootIngreso.get(AnualidadIngreso_.proyectoAnualidadId), proyectoAnualidadId)));
    cqAnualidadIngreso.where(listPredicates.toArray(new Predicate[] {}));

    // Execute query
    cqAnualidadIngreso
        .multiselect(
            rootIngreso.get(AnualidadIngreso_.proyectoPartida).get(ProyectoPartida_.codigo)
                .alias("codigoPartidaPresupuestaria"),
            cb.sum(rootIngreso.get(AnualidadIngreso_.importeConcedido)).alias("importeConcedido"))
        .groupBy(rootIngreso.get(AnualidadIngreso_.proyectoPartida).get(ProyectoPartida_.id));

    TypedQuery<AnualidadResumen> typedQueryGastos = entityManager.createQuery(cqAnualidadGasto);
    List<AnualidadResumen> resultGastos = typedQueryGastos.getResultList();
    resultGastos.stream().map(gasto -> {
      gasto.setTipo(TipoPartida.GASTO);
      return gasto;
    }).collect(Collectors.toList());

    TypedQuery<AnualidadResumen> typedQueryIngresos = entityManager.createQuery(cqAnualidadIngreso);
    List<AnualidadResumen> resultIngresos = typedQueryIngresos.getResultList();
    resultIngresos.stream().map(ingreso -> {
      ingreso.setTipo(TipoPartida.INGRESO);
      return ingreso;
    }).collect(Collectors.toList());

    List<AnualidadResumen> returnValue = Stream.concat(resultGastos.stream(), resultIngresos.stream())
        .collect(Collectors.toList());

    log.debug("getPartidasResumen(Long proyectoAnualidadId) - end");

    return returnValue;
  }

  /**
   * Recupera los {@link ProyectoAnualidadNotificacionSge} que cumplan las
   * condiciones de búsqueda y tengan a true el indicador presupuestar.
   * 
   * @param query           filtro de búsqueda.
   * @param unidadesGestion unidades de gestión.
   * @return Listado de {@link ProyectoAnualidadNotificacionSge}.
   */
  @Override
  public List<ProyectoAnualidadNotificacionSge> findAllNotificacionSge(String query, List<String> unidadesGestion) {
    log.debug("findAllNotificacionSge(String query, List<String> unidadesGestion) - start");

    List<ProyectoAnualidadNotificacionSge> anualidadGastos = getProyectoAnualidadNotificacionSgeGastos(query,
        unidadesGestion);
    List<ProyectoAnualidadNotificacionSge> anualidadIngresos = getProyectoAnualidadNotificacionSgeIngresos(query,
        unidadesGestion);

    List<ProyectoAnualidadNotificacionSge> result = new ArrayList<>();
    result.addAll(anualidadGastos);

    result.stream().forEach(anualidadGastoNotificacionSge -> anualidadIngresos.stream()
        .filter(anualidadIngresoNotificacionSge -> anualidadIngresoNotificacionSge.getProyectoSgeRef()
            .equals(anualidadGastoNotificacionSge.getProyectoSgeRef())
            && anualidadIngresoNotificacionSge.getAnio().equals(anualidadGastoNotificacionSge.getAnio())
            && anualidadIngresoNotificacionSge.getProyectoId().equals(anualidadGastoNotificacionSge.getProyectoId()))
        .findFirst().ifPresent(anualidadIngresoNotificacionSge -> anualidadGastoNotificacionSge
            .setTotalIngresos(anualidadIngresoNotificacionSge.getTotalIngresos())));

    for (ProyectoAnualidadNotificacionSge anualidadIngresoNotificacionSge : anualidadIngresos) {
      if (result.stream().noneMatch(anualidadGastoNotificacionSge -> anualidadIngresoNotificacionSge.getProyectoSgeRef()
          .equals(anualidadGastoNotificacionSge.getProyectoSgeRef())
          && anualidadIngresoNotificacionSge.getAnio().equals(anualidadGastoNotificacionSge.getAnio())
          && anualidadIngresoNotificacionSge.getProyectoId().equals(anualidadGastoNotificacionSge.getProyectoId()))) {
        result.add(anualidadIngresoNotificacionSge);
      }
    }

    log.debug("findAllNotificacionSge(String query, List<String> unidadesGestion) - end");
    return result;
  }

  private List<ProyectoAnualidadNotificacionSge> getProyectoAnualidadNotificacionSgeGastos(String query,
      List<String> unidadesGestion) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    // Select query
    CriteriaQuery<ProyectoAnualidadNotificacionSge> selectQuery = cb
        .createQuery(ProyectoAnualidadNotificacionSge.class);
    Root<AnualidadGasto> root = selectQuery.from(AnualidadGasto.class);

    // Join AnualidadGasto - ProyectoAnualidad
    Join<AnualidadGasto, ProyectoAnualidad> joinProyectoAnualidad = root.join(AnualidadGasto_.proyectoAnualidad,
        JoinType.INNER);

    // Join ProyectoAnualidad - Proyecto
    Join<ProyectoAnualidad, Proyecto> joinProyecto = joinProyectoAnualidad.join(ProyectoAnualidad_.proyecto,
        JoinType.INNER);

    // Where
    List<Predicate> listPredicates = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(unidadesGestion)) {
      listPredicates.add(cb.and(root.get(AnualidadGasto_.proyectoAnualidad).get(ProyectoAnualidad_.proyecto)
          .get(Proyecto_.unidadGestionRef).in(unidadesGestion)));
    }

    listPredicates.add(cb.equal(
        root.get(AnualidadGasto_.proyectoAnualidad).get(ProyectoAnualidad_.proyecto).get(Proyecto_.activo), true));

    if (StringUtils.hasText(query)) {
      Specification<AnualidadGasto> spec = SgiRSQLJPASupport.toSpecification(query);
      listPredicates.add(spec.toPredicate(root, selectQuery, cb));
    }

    Predicate isPresupuestarSelect = cb.isTrue(joinProyectoAnualidad.get(ProyectoAnualidad_.presupuestar));
    listPredicates.add(isPresupuestarSelect);

    // Total Gastos
    Subquery<BigDecimal> queryTotalGasto = selectQuery.subquery(BigDecimal.class);
    Root<AnualidadGasto> subqRootGasto = queryTotalGasto.from(AnualidadGasto.class);
    queryTotalGasto.select(cb.sum(subqRootGasto.get(AnualidadGasto_.importeConcedido))).where(
        cb.and(cb.equal(subqRootGasto.get(AnualidadGasto_.proyectoAnualidadId),
            joinProyectoAnualidad.get(ProyectoAnualidad_.id))),
        cb.equal(subqRootGasto.get(AnualidadGasto_.proyectoSgeRef), root.get(AnualidadGasto_.proyectoSgeRef)));

    selectQuery.where(listPredicates.toArray(new Predicate[] {}));

    // Execute query
    selectQuery
        .multiselect(joinProyectoAnualidad.get(ProyectoAnualidad_.id).alias("id"),
            joinProyectoAnualidad.get(ProyectoAnualidad_.anio).alias("anio"),
            joinProyecto.get(Proyecto_.fechaInicio).alias("proyectoFechaInicio"),
            joinProyecto.get(Proyecto_.fechaFin).alias("proyectoFechaFin"),
            cb.coalesce(queryTotalGasto.getSelection(), new BigDecimal(0)).alias("totalGastos"),
            cb.literal(new BigDecimal(0)).alias("totalIngreso"), joinProyecto.get(Proyecto_.id).alias("proyectoId"),
            joinProyecto.get(Proyecto_.titulo).alias("proyectoTitulo"),
            joinProyecto.get(Proyecto_.acronimo).alias("proyectoAcronimo"),
            joinProyecto.get(Proyecto_.estado).alias("proyectoEstado"),
            root.get(AnualidadGasto_.proyectoSgeRef).alias("proyectoSgeRef"),
            joinProyectoAnualidad.get(ProyectoAnualidad_.enviadoSge).alias("enviadoSge"))
        .groupBy(joinProyectoAnualidad.get(ProyectoAnualidad_.id), root.get(AnualidadGasto_.proyectoSgeRef),
            joinProyecto.get(Proyecto_.fechaInicio), joinProyecto.get(Proyecto_.fechaFin),
            joinProyecto.get(Proyecto_.id), joinProyecto.get(Proyecto_.titulo), joinProyecto.get(Proyecto_.acronimo),
            joinProyecto.get(Proyecto_.estado));

    return entityManager.createQuery(selectQuery).getResultList();
  }

  private List<ProyectoAnualidadNotificacionSge> getProyectoAnualidadNotificacionSgeIngresos(String query,
      List<String> unidadesGestion) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    // Select query
    CriteriaQuery<ProyectoAnualidadNotificacionSge> selectQuery = cb
        .createQuery(ProyectoAnualidadNotificacionSge.class);
    Root<AnualidadIngreso> root = selectQuery.from(AnualidadIngreso.class);

    // Join AnualidadIngreso - ProyectoAnualidad
    Join<AnualidadIngreso, ProyectoAnualidad> joinProyectoAnualidad = root.join(AnualidadIngreso_.proyectoAnualidad,
        JoinType.INNER);

    // Join ProyectoAnualidad - Proyecto
    Join<ProyectoAnualidad, Proyecto> joinProyecto = joinProyectoAnualidad.join(ProyectoAnualidad_.proyecto,
        JoinType.INNER);

    // Where
    List<Predicate> listPredicates = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(unidadesGestion)) {
      listPredicates.add(cb.and(root.get(AnualidadIngreso_.proyectoAnualidad).get(ProyectoAnualidad_.proyecto)
          .get(Proyecto_.unidadGestionRef).in(unidadesGestion)));
    }

    listPredicates.add(cb.equal(
        root.get(AnualidadIngreso_.proyectoAnualidad).get(ProyectoAnualidad_.proyecto).get(Proyecto_.activo), true));

    if (StringUtils.hasText(query)) {
      Specification<AnualidadIngreso> spec = SgiRSQLJPASupport.toSpecification(query);
      listPredicates.add(spec.toPredicate(root, selectQuery, cb));
    }

    Predicate isPresupuestarSelect = cb.isTrue(joinProyectoAnualidad.get(ProyectoAnualidad_.presupuestar));
    listPredicates.add(isPresupuestarSelect);

    // Total Gastos
    Subquery<BigDecimal> queryTotalIngreso = selectQuery.subquery(BigDecimal.class);
    Root<AnualidadIngreso> subqRootIngreso = queryTotalIngreso.from(AnualidadIngreso.class);
    queryTotalIngreso.select(cb.sum(subqRootIngreso.get(AnualidadIngreso_.importeConcedido))).where(
        cb.and(cb.equal(subqRootIngreso.get(AnualidadIngreso_.proyectoAnualidadId),
            joinProyectoAnualidad.get(ProyectoAnualidad_.id))),
        cb.equal(subqRootIngreso.get(AnualidadIngreso_.proyectoSgeRef), root.get(AnualidadIngreso_.proyectoSgeRef)));

    selectQuery.where(listPredicates.toArray(new Predicate[] {}));

    // Execute query
    selectQuery.multiselect(joinProyectoAnualidad.get(ProyectoAnualidad_.id).alias("id"),
        joinProyectoAnualidad.get(ProyectoAnualidad_.anio).alias("anio"),
        joinProyecto.get(Proyecto_.fechaInicio).alias("proyectoFechaInicio"),
        joinProyecto.get(Proyecto_.fechaFin).alias("proyectoFechaFin"),
        cb.literal(new BigDecimal(0)).alias("totalGastos"),
        cb.coalesce(queryTotalIngreso.getSelection(), new BigDecimal(0)).alias("totalIngreso"),
        joinProyecto.get(Proyecto_.id).alias("proyectoId"), joinProyecto.get(Proyecto_.titulo).alias("proyectoTitulo"),
        joinProyecto.get(Proyecto_.acronimo).alias("proyectoAcronimo"),
        joinProyecto.get(Proyecto_.estado).alias("proyectoEstado"),
        root.get(AnualidadIngreso_.proyectoSgeRef).alias("proyectoSgeRef"),
        joinProyectoAnualidad.get(ProyectoAnualidad_.enviadoSge).alias("enviadoSge"))
        .groupBy(joinProyectoAnualidad.get(ProyectoAnualidad_.id), root.get(AnualidadIngreso_.proyectoSgeRef),
            joinProyecto.get(Proyecto_.fechaInicio), joinProyecto.get(Proyecto_.fechaFin),
            joinProyecto.get(Proyecto_.id), joinProyecto.get(Proyecto_.titulo), joinProyecto.get(Proyecto_.acronimo),
            joinProyecto.get(Proyecto_.estado));

    return entityManager.createQuery(selectQuery).getResultList();
  }

}
