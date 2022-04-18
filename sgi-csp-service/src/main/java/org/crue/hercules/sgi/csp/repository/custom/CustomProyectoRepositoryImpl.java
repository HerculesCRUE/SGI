package org.crue.hercules.sgi.csp.repository.custom;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.dto.ProyectoDto;
import org.crue.hercules.sgi.csp.dto.ProyectoPresupuestoTotales;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.AnualidadGasto_;
import org.crue.hercules.sgi.csp.model.ConceptoGasto_;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.EstadoProyecto.Estado;
import org.crue.hercules.sgi.csp.model.EstadoProyecto_;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad_;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocio_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link Proyecto}.
 */
@Slf4j
@Component
public class CustomProyectoRepositoryImpl implements CustomProyectoRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtiene el {@link ModeloEjecucion} asignada a la {@link Proyecto}.
   *
   * @param id Id de la {@link Proyecto}.
   * @return {@link ModeloEjecucion} asignado
   */
  public Optional<ModeloEjecucion> getModeloEjecucion(Long id) {
    log.debug("getModeloEjecucion(Long id) - start");

    Optional<ModeloEjecucion> returnValue = Optional.empty();

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ModeloEjecucion> cq = cb.createQuery(ModeloEjecucion.class);
    Root<Proyecto> root = cq.from(Proyecto.class);

    Predicate finalPredicate = cb.equal(root.get(Proyecto_.id), id);
    cq.select(root.get(Proyecto_.modeloEjecucion)).where(finalPredicate);

    returnValue = entityManager.createQuery(cq).getResultList().stream().findFirst();

    log.debug("getModeloEjecucion(Long id) - end");
    return returnValue;
  }

  /**
   * Indica si en el {@link Proyecto} se permiten {@link ProyectoPaqueteTrabajo}.
   *
   * @param proyectoId Id de la {@link Proyecto}.
   * @return true si se permiten {@link ProyectoPaqueteTrabajo}, false si no se
   *         permiten {@link ProyectoPaqueteTrabajo}
   */
  public Optional<Boolean> getPermitePaquetesTrabajo(Long proyectoId) {
    log.debug("getPermitePaquetesTrabajo(Long id) - start");
    Optional<Boolean> returnValue = Optional.empty();

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
    Root<Proyecto> root = cq.from(Proyecto.class);

    Predicate finalPredicate = cb.equal(root.get(Proyecto_.id), proyectoId);
    cq.select(root.get(Proyecto_.permitePaquetesTrabajo)).where(finalPredicate);

    try {
      returnValue = entityManager.createQuery(cq).getResultList().stream().findFirst();
    } catch (NullPointerException e) {
    }

    log.debug("getPermitePaquetesTrabajo(Long id) - stop");
    return returnValue;
  }

  /**
   * Obtiene el {@link ProyectoPresupuestoTotales} de la {@link Proyecto}.
   * 
   * @param proyectoId Id de la {@link Proyecto}.
   * @return {@link ProyectoPresupuestoTotales}.
   */
  @Override
  public ProyectoPresupuestoTotales getTotales(Long proyectoId) {
    log.debug("ProyectoPresupuestoTotales getTotales(Long proyectoId) - start");
    // Crete query
    final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    final CriteriaQuery<ProyectoPresupuestoTotales> cq = cb.createQuery(ProyectoPresupuestoTotales.class);

    // Define FROM Proyecto clause
    Root<Proyecto> root = cq.from(Proyecto.class);

    // Total presupuesto universidad Sin Costes Indirectos
    Subquery<BigDecimal> sqlTotalPresupuestoUniversidad = cq.subquery(BigDecimal.class);
    Root<AnualidadGasto> rootTotalPresupuestoUniversidad = sqlTotalPresupuestoUniversidad.from(AnualidadGasto.class);

    sqlTotalPresupuestoUniversidad
        .select(cb.sum(rootTotalPresupuestoUniversidad.get(AnualidadGasto_.importePresupuesto)));
    sqlTotalPresupuestoUniversidad.where(cb.and(cb.equal(
        rootTotalPresupuestoUniversidad.get(AnualidadGasto_.proyectoAnualidad).get(ProyectoAnualidad_.proyectoId),
        proyectoId),
        cb.isFalse(
            rootTotalPresupuestoUniversidad.get(AnualidadGasto_.conceptoGasto).get(ConceptoGasto_.costesIndirectos))));

    // Total presupuesto universidad costes indirectos
    Subquery<BigDecimal> sqlTotalPresupuestoUniversidadCostesIndirectos = cq.subquery(BigDecimal.class);
    Root<AnualidadGasto> rootTotalPresupuestoUniversidadCostesIndirectos = sqlTotalPresupuestoUniversidadCostesIndirectos
        .from(AnualidadGasto.class);

    sqlTotalPresupuestoUniversidadCostesIndirectos
        .select(cb.sum(rootTotalPresupuestoUniversidadCostesIndirectos.get(AnualidadGasto_.importePresupuesto)));
    sqlTotalPresupuestoUniversidadCostesIndirectos.where(cb.and(cb.equal(
        rootTotalPresupuestoUniversidadCostesIndirectos.get(AnualidadGasto_.proyectoAnualidad)
            .get(ProyectoAnualidad_.proyectoId),
        proyectoId),
        cb.isTrue(rootTotalPresupuestoUniversidadCostesIndirectos.get(AnualidadGasto_.conceptoGasto)
            .get(ConceptoGasto_.costesIndirectos))));

    // Total concedido universidad Sin Costes Indirectos
    Subquery<BigDecimal> sqlTotalConcedidoUniversidad = cq.subquery(BigDecimal.class);
    Root<AnualidadGasto> rootTotalConcedidoUniversidad = sqlTotalConcedidoUniversidad.from(AnualidadGasto.class);

    sqlTotalConcedidoUniversidad.select(cb.sum(rootTotalConcedidoUniversidad.get(AnualidadGasto_.importeConcedido)));
    sqlTotalConcedidoUniversidad.where(cb.and(cb.equal(
        rootTotalConcedidoUniversidad.get(AnualidadGasto_.proyectoAnualidad).get(ProyectoAnualidad_.proyectoId),
        proyectoId),
        cb.isFalse(
            rootTotalConcedidoUniversidad.get(AnualidadGasto_.conceptoGasto).get(ConceptoGasto_.costesIndirectos))));

    // Total concedido universidad costes indirectos
    Subquery<BigDecimal> sqlTotalConcedidoUniversidadCostesIndirectos = cq.subquery(BigDecimal.class);
    Root<AnualidadGasto> rootTotalConcedidoUniversidadCostesIndirectos = sqlTotalConcedidoUniversidadCostesIndirectos
        .from(AnualidadGasto.class);

    sqlTotalConcedidoUniversidadCostesIndirectos
        .select(cb.sum(rootTotalConcedidoUniversidadCostesIndirectos.get(AnualidadGasto_.importeConcedido)));
    sqlTotalConcedidoUniversidadCostesIndirectos.where(cb.and(cb.equal(
        rootTotalConcedidoUniversidadCostesIndirectos.get(AnualidadGasto_.proyectoAnualidad)
            .get(ProyectoAnualidad_.proyectoId),
        proyectoId),
        cb.isTrue(rootTotalConcedidoUniversidadCostesIndirectos.get(AnualidadGasto_.conceptoGasto)
            .get(ConceptoGasto_.costesIndirectos))));

    // Total presupuesto socio
    Subquery<BigDecimal> sqTotalPresupuestoSocio = cq.subquery(BigDecimal.class);
    Root<ProyectoSocio> rootTotalPresupuestoSocio = sqTotalPresupuestoSocio.from(ProyectoSocio.class);
    sqTotalPresupuestoSocio.select(cb.sum(rootTotalPresupuestoSocio.get(ProyectoSocio_.importePresupuesto)));
    sqTotalPresupuestoSocio.where(cb.equal(rootTotalPresupuestoSocio.get(ProyectoSocio_.proyectoId), proyectoId));

    // Total concedido socio
    Subquery<BigDecimal> sqTotalConcedidoSocio = cq.subquery(BigDecimal.class);
    Root<ProyectoSocio> rootTotalConcedidoSocio = sqTotalConcedidoSocio.from(ProyectoSocio.class);
    sqTotalConcedidoSocio.select(cb.sum(rootTotalConcedidoSocio.get(ProyectoSocio_.importeConcedido)));
    sqTotalConcedidoSocio.where(cb.equal(rootTotalConcedidoSocio.get(ProyectoSocio_.proyectoId), proyectoId));

    cq.where(cb.equal(root.get(Proyecto_.id), proyectoId));

    // Define DTO projection
    cq.multiselect(
        // Total presupuesto universidad No CostesIndirectos
        cb.coalesce(sqlTotalPresupuestoUniversidad.getSelection(), new BigDecimal(0)),
        // Total presupuesto universidad Costes Indirectos
        cb.coalesce(sqlTotalPresupuestoUniversidadCostesIndirectos.getSelection(), new BigDecimal(0)),
        // Total presupuesto socio
        cb.coalesce(sqTotalPresupuestoSocio.getSelection(), new BigDecimal(0)),
        // Total concedido universidad
        cb.coalesce(sqlTotalConcedidoUniversidad.getSelection(), new BigDecimal(0)),
        // Total concedido universidad Costes Indirectos
        cb.coalesce(sqlTotalConcedidoUniversidadCostesIndirectos.getSelection(), new BigDecimal(0)),
        // Total concedido socio
        cb.coalesce(sqTotalConcedidoSocio.getSelection(), new BigDecimal(0)));
    // Execute query
    final TypedQuery<ProyectoPresupuestoTotales> q = entityManager.createQuery(cq);

    final ProyectoPresupuestoTotales result = q.getSingleResult();

    log.debug("ProyectoPresupuestoTotales getTotales(Long proyectoId) - end");
    return result;
  }

  @Override
  public List<Long> findIds(Specification<Proyecto> specification) {
    log.debug("List<Long> findIds(Specification<Proyecto> specification) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Proyecto> root = cq.from(Proyecto.class);

    cq.select(root.get(Proyecto_.id)).distinct(true).where(specification.toPredicate(root, cq, cb));

    log.debug("List<Long> findIds(Specification<Proyecto> specification) - end");

    return entityManager.createQuery(cq).getResultList();
  }

  /**
   * Devuelve una lista de {@link ProyectoDto} que se incorporarán a la baremación
   * de producción científica
   * 
   * @param fechaInicioBaremacion fecha inicio de baremación
   * @param fechaFinBaremacion    fecha fin de baremación
   * 
   * @return Lista de {@link ProyectoDto}
   */
  public List<ProyectoDto> findProyectosProduccionCientifica(Instant fechaInicioBaremacion,
      Instant fechaFinBaremacion) {

    log.debug("findProyectosProduccionCientifica(fechaInicioBaremacion, fechaFinBaremacion) : {} - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<ProyectoDto> cq = cb.createQuery(ProyectoDto.class);

    Root<Proyecto> root = cq.from(Proyecto.class);

    Join<Proyecto, Convocatoria> joinConvocatoria = root.join(Proyecto_.convocatoria, JoinType.LEFT);
    Join<Proyecto, EstadoProyecto> joinEstado = root.join(Proyecto_.estado);
    Join<Proyecto, ModeloEjecucion> joinModeloEjecucion = root.join(Proyecto_.modeloEjecucion);

    cq.multiselect(
        root.get(Proyecto_.id),
        root.get(Proyecto_.titulo),
        root.get(Proyecto_.fechaInicio),
        root.get(Proyecto_.fechaFin),
        root.get(Proyecto_.fechaFinDefinitiva),
        root.get(Proyecto_.clasificacionCVN),
        root.get(Proyecto_.importeConcedido),
        root.get(Proyecto_.totalImporteConcedido),
        root.get(Proyecto_.ambitoGeografico).get(TipoAmbitoGeografico_.id),
        joinConvocatoria.get(Convocatoria_.excelencia));

    Predicate predicateModeloEjecucionExternoFalse = cb.equal(joinModeloEjecucion.get(ModeloEjecucion_.externo),
        Boolean.FALSE);
    Predicate predicateProyectoIsActivo = cb.equal(root.get(Proyecto_.activo), Boolean.TRUE);

    Predicate predicateEstadoConcedido = cb.equal(joinEstado.get(EstadoProyecto_.estado), Estado.CONCEDIDO);

    Predicate predicateClasificacionCVN = cb.or(
        cb.equal(root.get(Proyecto_.clasificacionCVN), ClasificacionCVN.COMPETITIVOS),
        cb.equal(root.get(Proyecto_.clasificacionCVN), ClasificacionCVN.NO_COMPETITIVOS));

    Predicate predicateFechasBaremacion = cb.and(
        cb.lessThanOrEqualTo(root.get(Proyecto_.fechaInicio), fechaFinBaremacion),
        cb.greaterThanOrEqualTo(cb.coalesce(root.get(Proyecto_.fechaFinDefinitiva), root.get(Proyecto_.fechaFin)),
            fechaInicioBaremacion));

    cq.where(cb.and(
        predicateProyectoIsActivo,
        predicateModeloEjecucionExternoFalse,
        predicateClasificacionCVN,
        predicateEstadoConcedido,
        predicateFechasBaremacion));

    TypedQuery<ProyectoDto> typedQuery = entityManager.createQuery(cq);
    List<ProyectoDto> result = typedQuery.getResultList();

    log.debug("findProyectosProduccionCientifica(fechaInicioBaremacion, fechaFinBaremacion) : {} - end");

    return result;
  }
}
