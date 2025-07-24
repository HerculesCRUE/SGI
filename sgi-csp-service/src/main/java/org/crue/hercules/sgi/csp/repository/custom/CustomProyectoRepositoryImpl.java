package org.crue.hercules.sgi.csp.repository.custom;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo_;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocio_;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico_;
import org.crue.hercules.sgi.csp.util.CriteriaQueryUtils;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link Proyecto}.
 */
@Slf4j
@Component
public class CustomProyectoRepositoryImpl implements CustomProyectoRepository {

  private static final String SELECTION_NAME_SEPARATOR = ".";
  private static final String SELECTION_NAME_TITULO = Proyecto_.TITULO + SELECTION_NAME_SEPARATOR
      + ProyectoTitulo_.VALUE;

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
  @Override
  public Optional<ModeloEjecucion> getModeloEjecucion(Long id) {
    log.debug("getModeloEjecucion(Long id) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ModeloEjecucion> cq = cb.createQuery(ModeloEjecucion.class);
    Root<Proyecto> root = cq.from(Proyecto.class);

    Predicate finalPredicate = cb.equal(root.get(Proyecto_.id), id);
    cq.select(root.get(Proyecto_.modeloEjecucion)).where(finalPredicate);

    Optional<ModeloEjecucion> returnValue = entityManager.createQuery(cq).getResultList().stream().findFirst();

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
  @Override
  public Optional<Boolean> getPermitePaquetesTrabajo(Long proyectoId) {
    log.debug("getPermitePaquetesTrabajo(Long id) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
    Root<Proyecto> root = cq.from(Proyecto.class);

    Predicate finalPredicate = cb.equal(root.get(Proyecto_.id), proyectoId);
    cq.select(root.get(Proyecto_.permitePaquetesTrabajo)).where(finalPredicate);

    Optional<Boolean> returnValue = entityManager.createQuery(cq).getResultList().stream().findFirst();

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
  @Override
  public List<ProyectoDto> findProyectosProduccionCientifica(Instant fechaInicioBaremacion,
      Instant fechaFinBaremacion) {

    log.debug("findProyectosProduccionCientifica(fechaInicioBaremacion, fechaFinBaremacion) : {} - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);

    Root<Proyecto> root = cq.from(Proyecto.class);

    Join<Proyecto, Convocatoria> joinConvocatoria = root.join(Proyecto_.convocatoria, JoinType.LEFT);
    Join<Proyecto, EstadoProyecto> joinEstado = root.join(Proyecto_.estado);
    Join<Proyecto, ModeloEjecucion> joinModeloEjecucion = root.join(Proyecto_.modeloEjecucion);
    SetJoin<Proyecto, ProyectoTitulo> joinProyectoTitulo = root.join(Proyecto_.titulo, JoinType.LEFT);

    cq.multiselect(
        root.get(Proyecto_.id).alias(Proyecto_.ID),
        joinProyectoTitulo.alias(Proyecto_.TITULO),
        root.get(Proyecto_.fechaInicio).alias(Proyecto_.FECHA_INICIO),
        root.get(Proyecto_.fechaFin).alias(Proyecto_.FECHA_FIN),
        root.get(Proyecto_.fechaFinDefinitiva).alias(Proyecto_.FECHA_FIN_DEFINITIVA),
        root.get(Proyecto_.modeloEjecucion).get(ModeloEjecucion_.contrato).alias(ModeloEjecucion_.CONTRATO),
        root.get(Proyecto_.totalImporteConcedido).alias(Proyecto_.TOTAL_IMPORTE_CONCEDIDO),
        root.get(Proyecto_.importeConcedidoCostesIndirectos).alias(Proyecto_.IMPORTE_CONCEDIDO_COSTES_INDIRECTOS),
        root.get(Proyecto_.ambitoGeografico).get(TipoAmbitoGeografico_.id).alias(Proyecto_.AMBITO_GEOGRAFICO),
        joinConvocatoria.get(Convocatoria_.excelencia).alias(Convocatoria_.EXCELENCIA));

    Predicate predicateModeloEjecucionExternoFalse = cb.equal(joinModeloEjecucion.get(ModeloEjecucion_.externo),
        Boolean.FALSE);

    Predicate predicateProyectoIsActivo = cb.equal(root.get(Proyecto_.activo), Boolean.TRUE);

    Predicate predicateEstadoConcedido = cb.equal(joinEstado.get(EstadoProyecto_.estado), Estado.CONCEDIDO);

    Predicate predicateFechasBaremacion = cb.and(
        cb.lessThanOrEqualTo(root.get(Proyecto_.fechaInicio), fechaFinBaremacion),
        cb.greaterThanOrEqualTo(cb.coalesce(root.get(Proyecto_.fechaFinDefinitiva), root.get(Proyecto_.fechaFin)),
            fechaInicioBaremacion));

    cq.where(cb.and(
        predicateProyectoIsActivo,
        predicateModeloEjecucionExternoFalse,
        predicateEstadoConcedido,
        predicateFechasBaremacion));

    TypedQuery<Tuple> typedQuery = entityManager.createQuery(cq);
    List<Tuple> proyectoTuples = typedQuery.getResultList();

    Map<Long, ProyectoDto> proyectoTuplesMap = new HashMap<>();
    Map<Long, HashSet<ProyectoTitulo>> tituloTuplesMap = new HashMap<>();
    proyectoTuples.forEach(tuple -> {
      Long proyectoId = tuple.get(Proyecto_.ID, Long.class);

      ProyectoDto proyecto = proyectoTuplesMap.computeIfAbsent(proyectoId,
          key -> ProyectoDto.builder()
              .id(proyectoId)
              .fechaInicio(tuple.get(Proyecto_.FECHA_INICIO, Instant.class))
              .fechaFin(tuple.get(Proyecto_.FECHA_FIN, Instant.class))
              .fechaFinDefinitiva(tuple.get(Proyecto_.FECHA_FIN_DEFINITIVA, Instant.class))
              .contrato(tuple.get(ModeloEjecucion_.CONTRATO, Boolean.class))
              .totalImporteConcedido(tuple.get(Proyecto_.TOTAL_IMPORTE_CONCEDIDO, BigDecimal.class))
              .importeConcedidoCostesIndirectos(
                  tuple.get(Proyecto_.IMPORTE_CONCEDIDO_COSTES_INDIRECTOS, BigDecimal.class))
              .ambitoGeograficoId(tuple.get(Proyecto_.AMBITO_GEOGRAFICO, Long.class))
              .convocatoriaExcelencia(tuple.get(Convocatoria_.EXCELENCIA, Boolean.class))
              .build());

      tituloTuplesMap.computeIfAbsent(proyectoId, key -> new HashSet<>());
      tituloTuplesMap.get(proyectoId).add(tuple.get(Proyecto_.TITULO, ProyectoTitulo.class));
      proyecto.setTitulo(I18nHelper.getFieldValue(tituloTuplesMap.get(proyectoId)));

    });

    List<ProyectoDto> result = proyectoTuplesMap.values().stream().toList();

    log.debug("findProyectosProduccionCientifica(fechaInicioBaremacion, fechaFinBaremacion) : {} - end");

    return result;
  }

  /**
   * Obtiene el numero de {@link Proyecto} de una lista de personas del tipo de
   * {@link ClasificacionCVN} en los que forma parte del equipo de proyecto con un
   * rol principal
   *
   * @param personasRef       Lista de id de las personas.
   * @param clasificacionCvn  la clasificacion.
   * @param rolPrincipal      Flag para tener en cuenta solo las participaciones
   *                          como miembto con un rol principal
   * @param exludedProyectoId Excluye el {@link Proyecto} de la consulta
   * @return el numero de {@link Proyecto}.
   */
  @Override
  public Long countProyectosClasificacionCvnPersonas(
      List<String> personasRef, ClasificacionCVN clasificacionCvn,
      boolean rolPrincipal, Long exludedProyectoId) {
    log.debug(
        "countProyectosClasificacionCvnPersona(List<String> personaRef, ClasificacionCVN clasificacionCvn, boolean rolPrincipal, Long exludedProyectoId) - start");
    Long returnValue = countProyectosClasificacionCvnPersonas(personasRef, clasificacionCvn, rolPrincipal,
        exludedProyectoId, null);
    log.debug(
        "countProyectosClasificacionCvnPersona(List<String> personaRef, ClasificacionCVN clasificacionCvn, boolean rolPrincipal, Long exludedProyectoId) - end");
    return returnValue;
  }

  /**
   * Obtiene el numero de {@link Proyecto} de una lista de personas del tipo de
   * {@link ClasificacionCVN} en los que forma parte del equipo de proyecto con un
   * rol principal en la fecha indicada
   *
   * @param personasRef       Lista de id de las personas.
   * @param clasificacionCvn  la clasificacion.
   * @param rolPrincipal      Flag para tener en cuenta solo las participaciones
   *                          como miembto con un rol principal
   * @param exludedProyectoId Excluye el {@link Proyecto} de la consulta
   * @param fecha             fecha.
   * @return el numero de {@link Proyecto}.
   */
  @Override
  public Long countProyectosClasificacionCvnPersonas(List<String> personasRef, ClasificacionCVN clasificacionCvn,
      boolean rolPrincipal, Long exludedProyectoId, Instant fecha) {
    log.debug(
        "countProyectosClasificacionCvnPersona(List<String> personaRef, ClasificacionCVN clasificacionCvn, boolean rolPrincipal, Long exludedProyectoId, Instant fecha) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Proyecto> root = cq.from(Proyecto.class);
    Join<Proyecto, ProyectoEquipo> joinProyectoEquipo = root.join(Proyecto_.equipo);
    Join<ProyectoEquipo, RolProyecto> joinRolProyecto = joinProyectoEquipo.join(ProyectoEquipo_.rolProyecto);

    List<Predicate> listPredicates = new ArrayList<>();
    listPredicates.add(cb.equal(root.get(Proyecto_.clasificacionCVN), clasificacionCvn));
    listPredicates.add(joinProyectoEquipo.get(ProyectoEquipo_.personaRef).in(personasRef));

    if (rolPrincipal) {
      listPredicates.add(cb.isTrue(joinRolProyecto.get(RolProyecto_.rolPrincipal)));
    } else {
      listPredicates.add(cb.isFalse(joinRolProyecto.get(RolProyecto_.rolPrincipal)));
    }

    if (exludedProyectoId != null) {
      listPredicates.add(cb.equal(root.get(Proyecto_.id), exludedProyectoId).not());
    }

    if (fecha != null) {
      Predicate fechaInicioMiembroEquipoLess = cb.or(
          cb.lessThanOrEqualTo(joinProyectoEquipo.get(ProyectoEquipo_.fechaInicio), fecha),
          cb.and(
              cb.isNull(joinProyectoEquipo.get(ProyectoEquipo_.fechaInicio)),
              cb.lessThanOrEqualTo(root.get(Proyecto_.fechaInicio), fecha)));

      Predicate fechaFinMiembroEquipoGreater = cb.or(
          cb.greaterThanOrEqualTo(joinProyectoEquipo.get(ProyectoEquipo_.fechaFin), fecha),
          cb.and(
              cb.isNull(joinProyectoEquipo.get(ProyectoEquipo_.fechaFin)),
              cb.or(
                  cb.and(
                      cb.isNull(root.get(Proyecto_.fechaFinDefinitiva)),
                      cb.greaterThanOrEqualTo(root.get(Proyecto_.fechaFin), fecha)),
                  cb.greaterThanOrEqualTo(root.get(Proyecto_.fechaFinDefinitiva), fecha))));

      listPredicates.add(
          cb.and(
              fechaInicioMiembroEquipoLess,
              fechaFinMiembroEquipoGreater));
    }

    cq.select(cb.countDistinct(root.get(Proyecto_.id)));
    cq.where(listPredicates.toArray(new Predicate[] {}));

    // Execute query
    Long returnValue = entityManager.createQuery(cq).getResultList()
        .stream().findFirst().orElse(0L);

    log.debug(
        "countProyectosClasificacionCvnPersona(List<String> personaRef, ClasificacionCVN clasificacionCvn, boolean rolPrincipal, Long exludedProyectoId, Instant fecha) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Proyecto} sin duplicados y
   * ordenable por el titulo.
   * 
   * @param specs    condiciones que deben cumplir.
   * @param pageable la información de la paginación.
   * @return la lista de {@link Proyecto} paginadas y/o filtradas.
   */
  @Override
  public Page<Proyecto> findAllDistinct(Specification<Proyecto> specs, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> cq = cb.createTupleQuery();

    // Define FROM clause
    Root<Proyecto> root = cq.from(Proyecto.class);

    // Si se ordena por el titulo del proyecto se hace un subquery para obtener el
    // titulo en el idioma actual para poder hacer la ordenacion, si no se ordena
    // por el titulo no es necesaria la subquery
    boolean sortingByTituloProyecto = pageable.getSort().get()
        .anyMatch(sort -> sort.getProperty().equals(SELECTION_NAME_TITULO));

    Expression<String> proyectoTituloExpression;
    if (sortingByTituloProyecto) {
      Subquery<String> subqueryTitulo = cq.subquery(String.class);
      Root<Proyecto> subRoot = subqueryTitulo.correlate(root);
      Join<Proyecto, ProyectoTitulo> joinProyectoTitulo = subRoot.join(Proyecto_.titulo);

      subqueryTitulo.select(joinProyectoTitulo.get(ProyectoTitulo_.value))
          .where(cb.equal(joinProyectoTitulo.get(ProyectoTitulo_.lang), SgiLocaleContextHolder.getLanguage()));

      proyectoTituloExpression = subqueryTitulo;
    } else {
      proyectoTituloExpression = cb.literal("");
    }

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Proyecto> rootCount = countQuery.from(Proyecto.class);
    countQuery.select(cb.countDistinct(rootCount));

    List<Predicate> listPredicates = new ArrayList<>();
    List<Predicate> listPredicatesCount = new ArrayList<>();

    // Where
    if (specs != null) {
      listPredicates.add(specs.toPredicate(root, cq, cb));
      listPredicatesCount.add(specs.toPredicate(rootCount, cq, cb));
    }

    cq.where(listPredicates.toArray(new Predicate[] {}));

    cq.distinct(true).multiselect(
        root,
        proyectoTituloExpression.alias(SELECTION_NAME_TITULO));

    String[] selectionNames = new String[] {
        SELECTION_NAME_TITULO
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

    List<Proyecto> result = typedQuery.getResultList().stream().map(a -> (Proyecto) a.get(0)).toList();
    Page<Proyecto> returnValue = new PageImpl<>(result, pageable, count);

    log.debug("findAll(String query, Pageable pageable) - end");

    return returnValue;
  }

}
