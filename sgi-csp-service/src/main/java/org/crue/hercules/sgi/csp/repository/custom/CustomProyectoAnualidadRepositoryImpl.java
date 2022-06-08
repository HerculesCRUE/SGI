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
import javax.persistence.criteria.CriteriaDelete;
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
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConceptoGasto_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad_;
import org.crue.hercules.sgi.csp.model.ProyectoPartida_;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoAmortizacion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoAmortizacion_;
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

  private static final String ALIAS_CODIGO_PARTIDA_PRESUPUESTARIA = "codigoPartidaPresupuestaria";
  private static final String ALIAS_ENVIADO_SGE = "enviadoSge";
  private static final String ALIAS_PROYECTO_ACRONIMO = "proyectoAcronimo";
  private static final String ALIAS_PROYECTO_ESTADO = "proyectoEstado";
  private static final String ALIAS_PROYECTO_FECHA_FIN = "proyectoFechaFin";
  private static final String ALIAS_PROYECTO_FECHA_INICIO = "proyectoFechaInicio";
  private static final String ALIAS_PROYECTO_ID = "proyectoId";
  private static final String ALIAS_PROYECTO_TITULO = "proyectoTitulo";
  private static final String ALIAS_TOTAL_GASTOS = "totalGastos";
  private static final String ALIAS_TOTAL_GASTOS_CONCEDIDO = "totalGastosConcedido";
  private static final String ALIAS_TOTAL_GASTOS_PRESUPUESTO = "totalGastosPresupuesto";
  private static final String ALIAS_TOTAL_INGRESO = "totalIngreso";
  private static final String ALIAS_TOTAL_INGRESOS = "totalIngresos";

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

    // AnualidadGasto
    Subquery<Long> queryAnualidadGasto = cq.subquery(Long.class);
    Root<AnualidadGasto> rootAnualidadGasto = queryAnualidadGasto.from(AnualidadGasto.class);
    queryAnualidadGasto.select(cb.count(rootAnualidadGasto.get(AnualidadGasto_.id)))
        .where(cb.equal(rootAnualidadGasto.get(AnualidadGasto_.proyectoAnualidadId), root.get(ProyectoAnualidad_.id)));

    // AnualidadIngreso
    Subquery<Long> queryAnualidadIngreso = cq.subquery(Long.class);
    Root<AnualidadIngreso> rootAnualidadIngreso = queryAnualidadIngreso
        .from(AnualidadIngreso.class);
    queryAnualidadIngreso.select(cb.count(rootAnualidadIngreso.get(AnualidadIngreso_.id))).where(
        cb.equal(rootAnualidadIngreso.get(AnualidadIngreso_.proyectoAnualidadId), root.get(ProyectoAnualidad_.id)));

    Predicate relacionesProyectoAnualidad = cb.or(cb.gt(queryAnualidadGasto, 0L), cb.gt(queryAnualidadIngreso, 0L));

    // Execute query
    cq.multiselect(root.get(ProyectoAnualidad_.id).alias(ProyectoAnualidad_.ID),
        root.get(ProyectoAnualidad_.anio).alias(ProyectoAnualidad_.ANIO),
        root.get(ProyectoAnualidad_.fechaInicio).alias(ProyectoAnualidad_.FECHA_INICIO),
        root.get(ProyectoAnualidad_.fechaFin).alias(ProyectoAnualidad_.FECHA_FIN),
        cb.coalesce(queryTotalGastoPresupuesto.getSelection(), new BigDecimal(0)).alias(ALIAS_TOTAL_GASTOS_PRESUPUESTO),
        cb.coalesce(queryTotalGastoConcedido.getSelection(), new BigDecimal(0)).alias(ALIAS_TOTAL_GASTOS_CONCEDIDO),
        cb.coalesce(queryTotalIngreso.getSelection(), new BigDecimal(0)).alias(ALIAS_TOTAL_INGRESOS),
        root.get(ProyectoAnualidad_.presupuestar).alias(ProyectoAnualidad_.PRESUPUESTAR),
        root.get(ProyectoAnualidad_.enviadoSge).alias(ALIAS_ENVIADO_SGE),
        cb.selectCase().when(cb.isTrue(relacionesProyectoAnualidad), true).otherwise(false));

    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    // Número de registros totales para la paginación
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<ProyectoAnualidadResumen> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
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
                .alias(ALIAS_CODIGO_PARTIDA_PRESUPUESTARIA),
            cb.sum(rootGasto.get(AnualidadGasto_.importePresupuesto)).alias(AnualidadGasto_.IMPORTE_PRESUPUESTO),
            cb.sum(rootGasto.get(AnualidadGasto_.importeConcedido)).alias(AnualidadGasto_.IMPORTE_CONCEDIDO))
        .groupBy(rootGasto.get(AnualidadGasto_.proyectoPartida).get(ProyectoPartida_.id),
            rootGasto.get(AnualidadGasto_.proyectoPartida).get(ProyectoPartida_.codigo));

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
                .alias(ALIAS_CODIGO_PARTIDA_PRESUPUESTARIA),
            cb.sum(rootIngreso.get(AnualidadIngreso_.importeConcedido)).alias(AnualidadGasto_.IMPORTE_CONCEDIDO))
        .groupBy(rootIngreso.get(AnualidadIngreso_.proyectoPartida).get(ProyectoPartida_.id),
            rootIngreso.get(AnualidadIngreso_.proyectoPartida).get(ProyectoPartida_.codigo));

    TypedQuery<AnualidadResumen> typedQueryGastos = entityManager.createQuery(cqAnualidadGasto);
    List<AnualidadResumen> resultGastos = typedQueryGastos.getResultList();
    resultGastos.stream().forEach(gasto -> gasto.setTipo(TipoPartida.GASTO));

    TypedQuery<AnualidadResumen> typedQueryIngresos = entityManager.createQuery(cqAnualidadIngreso);
    List<AnualidadResumen> resultIngresos = typedQueryIngresos.getResultList();
    resultIngresos.stream().forEach(ingreso -> ingreso.setTipo(TipoPartida.INGRESO));

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
        .distinct(true)
        .multiselect(
            joinProyectoAnualidad.get(ProyectoAnualidad_.id).alias(ProyectoAnualidad_.ID),
            joinProyectoAnualidad.get(ProyectoAnualidad_.anio).alias(ProyectoAnualidad_.ANIO),
            joinProyecto.get(Proyecto_.fechaInicio).alias(ALIAS_PROYECTO_FECHA_INICIO),
            joinProyecto.get(Proyecto_.fechaFin).alias(ALIAS_PROYECTO_FECHA_FIN),
            cb.coalesce(queryTotalGasto.getSelection(), new BigDecimal(0)).alias(ALIAS_TOTAL_GASTOS),
            cb.literal(new BigDecimal(0)).alias(ALIAS_TOTAL_INGRESO),
            joinProyecto.get(Proyecto_.id).alias(ALIAS_PROYECTO_ID),
            joinProyecto.get(Proyecto_.titulo).alias(ALIAS_PROYECTO_TITULO),
            joinProyecto.get(Proyecto_.acronimo).alias(ALIAS_PROYECTO_ACRONIMO),
            joinProyecto.get(Proyecto_.estado).alias(ALIAS_PROYECTO_ESTADO),
            root.get(AnualidadGasto_.proyectoSgeRef).alias(AnualidadGasto_.PROYECTO_SGE_REF),
            joinProyectoAnualidad.get(ProyectoAnualidad_.enviadoSge).alias(ALIAS_ENVIADO_SGE));

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
    selectQuery
        .distinct(true)
        .multiselect(
            joinProyectoAnualidad.get(ProyectoAnualidad_.id).alias(ProyectoAnualidad_.ID),
            joinProyectoAnualidad.get(ProyectoAnualidad_.anio).alias(ProyectoAnualidad_.ANIO),
            joinProyecto.get(Proyecto_.fechaInicio).alias(ALIAS_PROYECTO_FECHA_INICIO),
            joinProyecto.get(Proyecto_.fechaFin).alias(ALIAS_PROYECTO_FECHA_FIN),
            cb.literal(new BigDecimal(0)).alias(ALIAS_TOTAL_GASTOS),
            cb.coalesce(queryTotalIngreso.getSelection(), new BigDecimal(0)).alias(ALIAS_TOTAL_INGRESO),
            joinProyecto.get(Proyecto_.id).alias(ALIAS_PROYECTO_ID),
            joinProyecto.get(Proyecto_.titulo).alias(ALIAS_PROYECTO_TITULO),
            joinProyecto.get(Proyecto_.acronimo).alias(ALIAS_PROYECTO_ACRONIMO),
            joinProyecto.get(Proyecto_.estado).alias(ALIAS_PROYECTO_ESTADO),
            root.get(AnualidadIngreso_.proyectoSgeRef).alias(AnualidadIngreso_.PROYECTO_SGE_REF),
            joinProyectoAnualidad.get(ProyectoAnualidad_.enviadoSge).alias(ALIAS_ENVIADO_SGE));

    return entityManager.createQuery(selectQuery).getResultList();
  }

  /**
   * Obtiene la suma de importe concedido de cada {@link AnualidadGasto}
   * asociados a un {@link Proyecto} cuyo id coincide con el indicado.
   * 
   * @param proyectoId el identificador del {@link Proyecto}
   * @return suma de puntos del campo importeConcedido
   */
  @Override
  public BigDecimal getTotalImporteConcedidoAnualidadGasto(Long proyectoId) {
    log.debug("getTotalImporteConcedidoAnualidadGasto(Long proyectoId) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<BigDecimal> cq = cb.createQuery(BigDecimal.class);

    Root<AnualidadGasto> root = cq.from(AnualidadGasto.class);

    Join<AnualidadGasto, ProyectoAnualidad> joinProyectoAnualidad = root.join(AnualidadGasto_.proyectoAnualidad);
    Join<ProyectoAnualidad, Proyecto> joinProyecto = joinProyectoAnualidad.join(ProyectoAnualidad_.proyecto);

    cq.select(
        cb.coalesce(cb.sum(root.get(AnualidadGasto_.importeConcedido)), new BigDecimal(0))
            .alias(AnualidadGasto_.IMPORTE_CONCEDIDO))
        .where(cb.equal(joinProyecto.get(Proyecto_.id), proyectoId));

    final TypedQuery<BigDecimal> q = entityManager.createQuery(cq);

    final BigDecimal result = q.getSingleResult();

    log.debug("getTotalImporteConcedidoAnualidadGasto(Long proyectoId) - end");

    return result;
  }

  /**
   * Obtiene la suma de importe concedido de cada {@link AnualidadGasto} de costes
   * indirectos
   * asociados a un {@link Proyecto} cuyo id coincide con el indicado.
   * 
   * @param proyectoId el identificador del {@link Proyecto}
   * @return suma de puntos del campo importeConcedido
   */
  @Override
  public BigDecimal getTotalImporteConcedidoAnualidadGastoCostesIndirectos(Long proyectoId) {
    log.debug("getTotalImporteConcedidoAnualidadGastoCostesIndirectos(Long proyectoId) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<BigDecimal> cq = cb.createQuery(BigDecimal.class);

    Root<AnualidadGasto> root = cq.from(AnualidadGasto.class);

    Join<AnualidadGasto, ConceptoGasto> joinConceptoGasto = root.join(AnualidadGasto_.conceptoGasto);
    Join<AnualidadGasto, ProyectoAnualidad> joinProyectoAnualidad = root.join(AnualidadGasto_.proyectoAnualidad);
    Join<ProyectoAnualidad, Proyecto> joinProyecto = joinProyectoAnualidad.join(ProyectoAnualidad_.proyecto);

    Predicate predicateProyectoId = cb.equal(joinProyecto.get(Proyecto_.id), proyectoId);
    Predicate predicateCosteIndirecto = cb.equal(joinConceptoGasto.get(ConceptoGasto_.costesIndirectos), Boolean.TRUE);

    cq.select(
        cb.coalesce(cb.sum(root.get(AnualidadGasto_.importeConcedido)), new BigDecimal(0))
            .alias(AnualidadGasto_.IMPORTE_CONCEDIDO))
        .where(cb.and(predicateProyectoId, predicateCosteIndirecto));

    final TypedQuery<BigDecimal> q = entityManager.createQuery(cq);

    final BigDecimal result = q.getSingleResult();

    log.debug("getTotalImporteConcedidoAnualidadGastoCostesIndirectos(Long proyectoId) - end");

    return result;
  }

  /**
   * Elimina el {@link ProyectoAnualidad} indicado y los {@link AnualidadGasto},
   * {@link AnualidadIngreso} y {@link ProyectoPeriodoAmortizacion} asociados.
   * 
   * @param id Identificador del {@link ProyectoAnualidad}.
   * @return el número de registros eliminados.
   */
  @Override
  public int deleteByIdCascade(Long id) {
    log.debug("deleteByIdCascade(Long id) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    deleteAnualidadGastoByProyectoAnualidadId(cb, id);
    deleteAnualidadIngresoByProyectoAnualidadId(cb, id);
    deletePeriodoAmortizacionByProyectoAnualidadId(cb, id);

    // Crete query
    CriteriaDelete<ProyectoAnualidad> query = cb.createCriteriaDelete(ProyectoAnualidad.class);
    Root<ProyectoAnualidad> root = query.from(ProyectoAnualidad.class);
    query.where(cb.equal(root.get(ProyectoAnualidad_.id), id));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteByIdCascade(Long id) - end");
    return returnValue;
  }

  private int deleteAnualidadGastoByProyectoAnualidadId(CriteriaBuilder cb, Long proyectoAnualidadId) {
    log.debug("deleteAnualidadGastoByProyectoAnualidadId(CriteriaBuilder cb, Long proyectoAnualidadId) - start");

    CriteriaDelete<AnualidadGasto> queryAnualidadGasto = cb.createCriteriaDelete(AnualidadGasto.class);
    Root<AnualidadGasto> rootAnualidadGasto = queryAnualidadGasto.from(AnualidadGasto.class);
    queryAnualidadGasto
        .where(cb.equal(rootAnualidadGasto.get(AnualidadGasto_.proyectoAnualidadId), proyectoAnualidadId));
    int returnValue = entityManager.createQuery(queryAnualidadGasto).executeUpdate();

    log.debug("deleteAnualidadGastoByProyectoAnualidadId(CriteriaBuilder cb, Long proyectoAnualidadId) - end");
    return returnValue;
  }

  private int deleteAnualidadIngresoByProyectoAnualidadId(CriteriaBuilder cb, long proyectoAnualidadId) {
    log.debug("deleteAnualidadIngresoByProyectoAnualidadId(CriteriaBuilder cb, Long proyectoAnualidadId) - start");

    CriteriaDelete<AnualidadIngreso> queryAnualidadIngreso = cb.createCriteriaDelete(AnualidadIngreso.class);
    Root<AnualidadIngreso> rootAnualidadIngreso = queryAnualidadIngreso.from(AnualidadIngreso.class);
    queryAnualidadIngreso
        .where(cb.equal(rootAnualidadIngreso.get(AnualidadIngreso_.proyectoAnualidadId), proyectoAnualidadId));
    int returnValue = entityManager.createQuery(queryAnualidadIngreso).executeUpdate();

    log.debug("deleteAnualidadIngresoByProyectoAnualidadId(CriteriaBuilder cb, Long proyectoAnualidadId) - end");
    return returnValue;
  }

  private int deletePeriodoAmortizacionByProyectoAnualidadId(CriteriaBuilder cb, long proyectoAnualidadId) {
    log.debug("deletePeriodoAmortizacionByProyectoAnualidadId(CriteriaBuilder cb, Long proyectoAnualidadId) - start");

    CriteriaDelete<ProyectoPeriodoAmortizacion> queryPeriodoAmortizacion = cb
        .createCriteriaDelete(ProyectoPeriodoAmortizacion.class);
    Root<ProyectoPeriodoAmortizacion> rootPeriodoAmortizacion = queryPeriodoAmortizacion
        .from(ProyectoPeriodoAmortizacion.class);
    queryPeriodoAmortizacion
        .where(cb.equal(rootPeriodoAmortizacion.get(ProyectoPeriodoAmortizacion_.proyectoAnualidadId),
            proyectoAnualidadId));
    int returnValue = entityManager.createQuery(queryPeriodoAmortizacion).executeUpdate();

    log.debug("deletePeriodoAmortizacionByProyectoAnualidadId(CriteriaBuilder cb, Long proyectoAnualidadId) - end");
    return returnValue;
  }

}
