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
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.dto.AnualidadResumen;
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
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

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

    List<Predicate> listPredicates = new ArrayList<Predicate>();
    List<Predicate> listPredicatesCount = new ArrayList<Predicate>();

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
    Page<ProyectoAnualidadResumen> returnValue = new PageImpl<ProyectoAnualidadResumen>(result, pageable, count);

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

    List<Predicate> listPredicates = new ArrayList<Predicate>();
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

    listPredicates = new ArrayList<Predicate>();
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

}
