package org.crue.hercules.sgi.eti.repository.custom;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Comite_;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva_;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluacion_;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion_;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.Retrospectiva_;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria_;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.repository.predicate.MemoriaPredicateBuilder;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository para {@link Memoria}.
 */
@Slf4j
@Component
public class CustomMemoriaRepositoryImpl implements CustomMemoriaRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param idConvocatoriaReunion Identificador del {@link ConvocatoriaReunion}
   * @return lista de memorias asignables a la convocatoria.
   */
  @Override
  public List<Memoria> findAllMemoriasAsignablesConvocatoria(Long idConvocatoriaReunion) {
    log.debug("findAllMemoriasAsignablesConvocatoria(Long idConvocatoriaReunion) - start");
    final List<Predicate> predicates = new ArrayList<>();

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Memoria> cq = cb.createQuery(Memoria.class);

    // Fecha limite convocatoria
    Subquery<Instant> sqFechaLimiteConvocatoria = cq.subquery(Instant.class);
    Root<ConvocatoriaReunion> convocatoriaFechaLimiteRoot = sqFechaLimiteConvocatoria.from(ConvocatoriaReunion.class);
    sqFechaLimiteConvocatoria.select(convocatoriaFechaLimiteRoot.get(ConvocatoriaReunion_.fechaLimite));
    sqFechaLimiteConvocatoria
        .where(cb.equal(convocatoriaFechaLimiteRoot.get(ConvocatoriaReunion_.id), idConvocatoriaReunion));

    // Comite de la convocatoria si la convocatoria es de tipo ordinaria o
    // extraordinaria
    Subquery<Long> sqComiteConvocatoriaOrdinariaExtraordinaria = cq.subquery(Long.class);
    Root<ConvocatoriaReunion> convocatoriasOrdExtraordRoot = sqComiteConvocatoriaOrdinariaExtraordinaria
        .from(ConvocatoriaReunion.class);
    Join<ConvocatoriaReunion, Comite> joinConvocatoriaReunionOrdExtraordComite = convocatoriasOrdExtraordRoot
        .join(ConvocatoriaReunion_.comite);
    Join<ConvocatoriaReunion, TipoConvocatoriaReunion> joinConvocatoriaReunionOrdExtraordTipo = convocatoriasOrdExtraordRoot
        .join(ConvocatoriaReunion_.tipoConvocatoriaReunion);

    sqComiteConvocatoriaOrdinariaExtraordinaria.select(joinConvocatoriaReunionOrdExtraordComite.get(Comite_.id));
    sqComiteConvocatoriaOrdinariaExtraordinaria
        .where(
            cb.and(cb.equal(convocatoriasOrdExtraordRoot.get(ConvocatoriaReunion_.id), idConvocatoriaReunion),
                joinConvocatoriaReunionOrdExtraordTipo.get(TipoConvocatoriaReunion_.id)
                    .in(Arrays.asList(TipoConvocatoriaReunion.Tipo.ORDINARIA.getId(),
                        TipoConvocatoriaReunion.Tipo.EXTRAORDINARIA.getId()))));

    // Comite de la convocatoria si la convocatoria es de tipo seguimiento
    Subquery<Long> sqComiteConvocatoriaSeguimiento = cq.subquery(Long.class);
    Root<ConvocatoriaReunion> convocatoriaSeguimientoRoot = sqComiteConvocatoriaSeguimiento
        .from(ConvocatoriaReunion.class);
    Join<ConvocatoriaReunion, Comite> joinConvocatoriaSeguimientoComite = convocatoriaSeguimientoRoot
        .join(ConvocatoriaReunion_.comite);
    Join<ConvocatoriaReunion, TipoConvocatoriaReunion> joinConvocatoriaSeguimientoTipo = convocatoriaSeguimientoRoot
        .join(ConvocatoriaReunion_.tipoConvocatoriaReunion);

    sqComiteConvocatoriaSeguimiento.select(joinConvocatoriaSeguimientoComite.get(Comite_.id));
    sqComiteConvocatoriaSeguimiento
        .where(cb.and(cb.equal(convocatoriaSeguimientoRoot.get(ConvocatoriaReunion_.id), idConvocatoriaReunion),
            cb.equal(joinConvocatoriaSeguimientoTipo.get(TipoConvocatoriaReunion_.id),
                TipoConvocatoriaReunion.Tipo.SEGUIMIENTO.getId())));

    // Define FROM clause
    Root<Memoria> root = cq.from(Memoria.class);
    Join<Memoria, Comite> joinMemoriaComite = root.join(Memoria_.comite);
    Join<Memoria, TipoEstadoMemoria> joinMemoriaTipoEstado = root.join(Memoria_.estadoActual);
    Join<Memoria, Retrospectiva> joinMemoriaRetrospectiva = root.join(Memoria_.retrospectiva, JoinType.LEFT);

    // Memorias convocatoria ordinaria o extraordinaria
    Predicate comiteConvocatoriaReunionOrdExtraord = cb.equal(joinMemoriaComite.get(Comite_.id),
        sqComiteConvocatoriaOrdinariaExtraordinaria);
    Predicate memoriasSecretaria = cb.equal(joinMemoriaTipoEstado.get(TipoEstadoMemoria_.id),
        TipoEstadoMemoria.Tipo.EN_SECRETARIA.getId());
    Predicate fechaEnvioMenorFechaLimite = cb.lessThanOrEqualTo(root.get(Memoria_.fechaEnvioSecretaria),
        sqFechaLimiteConvocatoria);
    Predicate retrospectivaSecretaria = cb.equal(
        joinMemoriaRetrospectiva.get(Retrospectiva_.estadoRetrospectiva).get(EstadoRetrospectiva_.id),
        EstadoRetrospectiva.Tipo.EN_SECRETARIA.getId());

    Predicate memoriasConvocatoriaOrdinariaExtraordinaria = cb.and(comiteConvocatoriaReunionOrdExtraord,
        cb.or(cb.and(memoriasSecretaria, fechaEnvioMenorFechaLimite), retrospectivaSecretaria));

    // Memorias convocatoria seguimiento
    Predicate comiteConvocatoriaReunionSeguimiento = cb.equal(joinMemoriaComite.get(Comite_.id),
        sqComiteConvocatoriaSeguimiento);
    Predicate memoriasSecretariaSeguimientoAnualYFinal = joinMemoriaTipoEstado.get(TipoEstadoMemoria_.id)
        .in(Arrays.asList(
            TipoEstadoMemoria.Tipo.EN_SECRETARIA_SEGUIMIENTO_ANUAL.getId(),
            TipoEstadoMemoria.Tipo.EN_SECRETARIA_SEGUIMIENTO_FINAL.getId()));
    Predicate memoriasConvocatoriaSeguimiento = cb.and(comiteConvocatoriaReunionSeguimiento,
        cb.and(memoriasSecretariaSeguimientoAnualYFinal, fechaEnvioMenorFechaLimite));

    // Memorias activos
    Predicate memoriasActivas = cb.equal(root.get(Memoria_.activo), Boolean.TRUE);
    // Memorias desde petición/solicitud de evaluación
    Predicate memoriasDesdeSolicitudEvaluacion = cb
        .isNull(root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.solicitudConvocatoriaRef));
    // Memorias desde solicitud convocatoria
    Predicate memoriasDesdeSolicitudConvocatoria = cb.and(
        cb.isNotNull(root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.solicitudConvocatoriaRef)),
        cb.or(
            root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.estadoFinanciacion)
                .in(Arrays.asList(PeticionEvaluacion.EstadoFinanciacion.CONCEDIDO,
                    PeticionEvaluacion.EstadoFinanciacion.SOLICITADO)),
            cb.and(
                cb.equal(root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.estadoFinanciacion),
                    PeticionEvaluacion.EstadoFinanciacion.DENEGADO),
                cb.equal(root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.tieneFondosPropios),
                    Boolean.TRUE))));

    // WHERE
    predicates
        .add(cb.and(memoriasActivas, cb.or(memoriasDesdeSolicitudEvaluacion, memoriasDesdeSolicitudConvocatoria)));
    predicates.add(cb.or(memoriasConvocatoriaOrdinariaExtraordinaria, memoriasConvocatoriaSeguimiento));

    // Join all restrictions
    cq.where(cb.and(predicates.toArray(new Predicate[] {})));

    // Execute query
    List<Order> orders = QueryUtils.toOrders(Sort.by(Sort.Direction.ASC, Memoria_.TITULO), root, cb);
    cq.orderBy(orders);

    TypedQuery<Memoria> typedQuery = entityManager.createQuery(cq);

    List<Memoria> result = typedQuery.getResultList();

    log.debug("findAllMemoriasAsignablesConvocatoria(Long idConvocatoriaReunion) - end");

    return result;
  }

  /**
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param idPeticionEvaluacion Identificador del {@link PeticionEvaluacion}
   * @return lista de memorias a partir de una petición de evaluación asignables a
   *         la convocatoria.
   */
  @Override
  public List<Memoria> findAllMemoriasAsignablesPeticionEvaluacion(Long idPeticionEvaluacion) {
    log.debug("findAllMemoriasAsignablesPeticionEvaluacion(Long idPeticionEvaluacion) - start");
    final List<Predicate> predicates = new ArrayList<>();

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Memoria> cq = cb.createQuery(Memoria.class);

    // Define FROM clause
    Root<Memoria> root = cq.from(Memoria.class);
    Join<Memoria, TipoEstadoMemoria> joinMemoriaTipoEstado = root.join(Memoria_.estadoActual);
    Join<Memoria, Retrospectiva> joinMemoriaRetrospectiva = root.join(Memoria_.retrospectiva, JoinType.LEFT);

    Predicate memoriasSecretaria = cb.equal(joinMemoriaTipoEstado.get(TipoEstadoMemoria_.id),
        TipoEstadoMemoria.Tipo.EN_SECRETARIA);
    Predicate retrospectivaSecretaria = cb.equal(
        joinMemoriaRetrospectiva.get(Retrospectiva_.estadoRetrospectiva).get(EstadoRetrospectiva_.id),
        EstadoRetrospectiva.Tipo.EN_SECRETARIA);
    Predicate revMinima = cb.equal(joinMemoriaTipoEstado.get(TipoEstadoMemoria_.id),
        TipoEstadoMemoria.Tipo.EN_SECRETARIA_REVISION_MINIMA);
    Predicate memoriasSecretariaSeguimientoAnualYFinal = joinMemoriaTipoEstado.get(TipoEstadoMemoria_.id)
        .in(Arrays.asList(TipoEstadoMemoria.Tipo.EN_SECRETARIA_SEGUIMIENTO_ANUAL,
            TipoEstadoMemoria.Tipo.EN_SECRETARIA_SEGUIMIENTO_FINAL));
    Predicate memoriasPredicate = cb.or(memoriasSecretaria, retrospectivaSecretaria, revMinima,
        memoriasSecretariaSeguimientoAnualYFinal);

    // Memorias activos
    Predicate memoriasActivas = cb.and(cb.equal(root.get(Memoria_.activo), Boolean.TRUE),
        cb.equal(root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id), idPeticionEvaluacion));

    // WHERE
    predicates
        .add(cb.and(memoriasActivas, memoriasPredicate));

    // Join all restrictions
    cq.where(cb.and(predicates.toArray(new Predicate[] {})));

    // Execute query
    List<Order> orders = QueryUtils.toOrders(Sort.by(Sort.Direction.ASC, Memoria_.TITULO), root, cb);
    cq.orderBy(orders);

    TypedQuery<Memoria> typedQuery = entityManager.createQuery(cq);

    List<Memoria> result = typedQuery.getResultList();

    log.debug("findAllMemoriasAsignablesPeticionEvaluacion(Long idPeticionEvaluacion) - end");

    return result;
  }

  /**
   * Devuelve las memorias de una petición evaluación con su fecha límite y de
   * evaluación.
   * 
   * @param idPeticionEvaluacion Identificador {@link PeticionEvaluacion}
   * @return lista de memorias de {@link PeticionEvaluacion}
   */
  @Override
  public List<MemoriaPeticionEvaluacion> findMemoriasEvaluacion(Long idPeticionEvaluacion, String personaRefConsulta) {
    log.debug("findMemoriasEvaluacion(Long idPeticionEvaluacion, String personaRefConsulta) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<MemoriaPeticionEvaluacion> cq = cb.createQuery(MemoriaPeticionEvaluacion.class);
    Root<Memoria> root = cq.from(Memoria.class);
    Join<Memoria, Retrospectiva> joinMemoriaRetrospectiva = root.join(Memoria_.retrospectiva, JoinType.LEFT);

    Expression<Instant> defaultDate = cb.nullLiteral(Instant.class);

    cq.multiselect(root.get(Memoria_.id), root.get(Memoria_.personaRef), root.get(Memoria_.numReferencia),
        root.get(Memoria_.titulo),
        root.get(Memoria_.comite), root.get(Memoria_.estadoActual), defaultDate, defaultDate,
        cb.selectCase().when(cb.isNotNull(isResponsable(root, cb, cq, personaRefConsulta)), true).otherwise(false)
            .alias("isResponsable"),
        root.get(Memoria_.activo),
        root.get(Memoria_.requiereRetrospectiva), joinMemoriaRetrospectiva.alias("retrospectiva"),
        root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.personaRef),
        root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.tutorRef),
        root.get(Memoria_.version)).distinct(true);

    cq.where(cb.equal(root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id), idPeticionEvaluacion),
        cb.isTrue(root.get(Memoria_.activo)));

    TypedQuery<MemoriaPeticionEvaluacion> typedQuery = entityManager.createQuery(cq);

    List<MemoriaPeticionEvaluacion> result = typedQuery.getResultList().stream().map(this::resolveMemoriaDates)
        .collect(Collectors.toList());

    log.debug("findMemoriasEvaluacion(Long idPeticionEvaluacion, String personaRefConsulta) - end");
    return result;
  }

  /**
   * Devuelve todas las memorias con la fecha límite y de evaluación.
   * 
   * @param specs              datos de búsqueda.
   * @param pageable           información de paginación
   * @param personaRefConsulta responsable
   * @return lista de memorias de {@link PeticionEvaluacion}
   */
  @Override
  public Page<MemoriaPeticionEvaluacion> findAllMemoriasEvaluaciones(Specification<Memoria> specs, Pageable pageable,
      String personaRefConsulta) {
    log.debug("findAllMemoriasEvaluaciones( Pageable pageable) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<MemoriaPeticionEvaluacion> cq = cb.createQuery(MemoriaPeticionEvaluacion.class);
    Root<Memoria> root = cq.from(Memoria.class);

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Memoria> rootCount = countQuery.from(Memoria.class);
    countQuery.select(cb.count(rootCount));
    Join<Memoria, Retrospectiva> joinMemoriaRetrospectiva = root.join(Memoria_.retrospectiva, JoinType.LEFT);

    List<Predicate> predicates = new ArrayList<>();
    List<Predicate> predicatesCount = new ArrayList<>();

    if (ObjectUtils.isNotEmpty(personaRefConsulta)) {
      Predicate predicateMemoria = cb.in(root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id))
          .value(getIdsPeticionEvaluacionMemoria(cb, cq, personaRefConsulta));
      Predicate predicateMemoriaCount = cb.in(rootCount.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id))
          .value(getIdsPeticionEvaluacionMemoria(cb, cq, personaRefConsulta));

      Predicate predicatePersonaRefPeticion = cb
          .equal(root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.personaRef), personaRefConsulta);
      Predicate predicatePersonaRefPeticionCount = cb
          .equal(rootCount.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.personaRef), personaRefConsulta);
      Predicate predicatePersonaRefMemoria = cb.equal(root.get(Memoria_.personaRef), personaRefConsulta);
      Predicate predicatePersonaRefMemoriaCount = cb.equal(rootCount.get(Memoria_.personaRef), personaRefConsulta);
      predicates.add(cb.or(cb.or(predicatePersonaRefPeticion, predicatePersonaRefMemoria), predicateMemoria));
      predicates.add(cb.isTrue(root.get(Memoria_.activo)));
      predicatesCount
          .add(cb.or(cb.or(predicatePersonaRefPeticionCount, predicatePersonaRefMemoriaCount), predicateMemoriaCount));
      predicatesCount.add(cb.isTrue(rootCount.get(Memoria_.activo)));
    }

    // Where
    if (ObjectUtils.isNotEmpty(specs)) {
      Predicate predicateSpecs = specs.toPredicate(root, cq, cb);
      predicates.add(predicateSpecs);
      Predicate predicateSpecsCount = specs.toPredicate(rootCount, cq, cb);
      predicatesCount.add(predicateSpecsCount);
    }

    Expression<Instant> defaultDate = cb.nullLiteral(Instant.class);

    cq.multiselect(root.get(Memoria_.id), root.get(Memoria_.personaRef), root.get(Memoria_.numReferencia),
        root.get(Memoria_.titulo),
        root.get(Memoria_.comite), root.get(Memoria_.estadoActual), defaultDate, defaultDate,
        cb.selectCase().when(cb.isNotNull(isResponsable(root, cb, cq, personaRefConsulta)), true).otherwise(false)
            .alias("isResponsable"),
        root.get(Memoria_.activo),
        root.get(Memoria_.requiereRetrospectiva), joinMemoriaRetrospectiva.alias("retrospectiva"),
        root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.personaRef),
        root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.tutorRef),
        root.get(Memoria_.version)).distinct(true);

    cq.where(predicates.toArray(new Predicate[] {}));

    countQuery.where(predicatesCount.toArray(new Predicate[] {}));

    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    // Número de registros totales para la paginación
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<MemoriaPeticionEvaluacion> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<MemoriaPeticionEvaluacion> result = typedQuery.getResultList().stream().map(this::resolveMemoriaDates)
        .collect(Collectors.toList());

    Page<MemoriaPeticionEvaluacion> returnValue = new PageImpl<>(result, pageable, count);

    log.debug("findAllMemoriasEvaluaciones( Pageable pageable) - end");
    return returnValue;
  }

  private MemoriaPeticionEvaluacion resolveMemoriaDates(MemoriaPeticionEvaluacion memoria) {
    FechasMemoria fechas = this.getFechaEvaluacionAndFechaLimite(memoria);
    memoria.setFechaEvaluacion(fechas.getFechaEvaluacion());
    memoria.setFechaLimite(fechas.getFechaLimite());
    return memoria;
  }

  private FechasMemoria getFechaEvaluacionAndFechaLimite(MemoriaPeticionEvaluacion memoria) {

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<FechasMemoria> cq = cb.createQuery(FechasMemoria.class);
    Root<Evaluacion> root = cq.from(Evaluacion.class);
    Root<ConvocatoriaReunion> rootConvocatoriaReunion = cq.from(ConvocatoriaReunion.class);

    MemoriaPredicateBuilder memoriaPredicateBuilder = MemoriaPredicateBuilder.builder();

    switch (memoria.getEstadoActual().getTipo()) {
      case COMPLETADA:
      case EN_SECRETARIA:
        if (!memoria.isActivo()) {
          return new FechasMemoria();
        }
        cq.orderBy(cb.asc(rootConvocatoriaReunion.get(ConvocatoriaReunion_.fechaEvaluacion)));
        memoriaPredicateBuilder.filterWithConvocatoriaReunionActiva(cb, rootConvocatoriaReunion);
        memoriaPredicateBuilder.filterWithTipoConvocatoriaReunionOrdinarioOrExtraordinario(cb, rootConvocatoriaReunion);
        memoriaPredicateBuilder.filterWithComiteConvocatoriaReunion(rootConvocatoriaReunion, cb, memoria.getComite());
        memoriaPredicateBuilder.filterWithFechaLimiteConvocatoriaReunionGreatestThanNowConvocatoriaReunion(cb,
            rootConvocatoriaReunion);
        memoriaPredicateBuilder.filterWithActasNotInConvocatoriasFinalizadasConvocatoriaReunion(cb,
            rootConvocatoriaReunion, cq);
        prepareCriteriaWithConvocatoriaReunionUnlinked(cb, cq, rootConvocatoriaReunion, memoriaPredicateBuilder);
        break;
      case EN_EVALUACION:
      case PENDIENTE_CORRECCIONES:
      case NO_PROCEDE_EVALUAR:
      case EN_EVALUACION_REVISION_MINIMA:
      case FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS:
      case FIN_EVALUACION:
        memoriaPredicateBuilder.filterWithTipoEvaluacionEqualsTo(cb, root, TipoEvaluacion.Tipo.MEMORIA.getId());
        memoriaPredicateBuilder.filterWithLastVersion(cb, root, memoria.getId(), cq);
        prepareCriteriaWithConvocatoriaReunionLinked(cb, cq, root, memoriaPredicateBuilder, memoria.getId());
        break;
      case COMPLETADA_SEGUIMIENTO_FINAL:
      case EN_SECRETARIA_SEGUIMIENTO_FINAL:
      case COMPLETADA_SEGUIMIENTO_ANUAL:
      case EN_SECRETARIA_SEGUIMIENTO_ANUAL:
        this.resolveNextEvaluationPredicatesWhereConvocatoriaReunionIsOfTypeSeguimiento(memoria, cb,
            rootConvocatoriaReunion, memoriaPredicateBuilder, cq);
        prepareCriteriaWithConvocatoriaReunionUnlinked(cb, cq, rootConvocatoriaReunion, memoriaPredicateBuilder);
        break;
      case EN_EVALUACION_SEGUIMIENTO_ANUAL:
      case SOLICITUD_MODIFICACION:
      case FIN_EVALUACION_SEGUIMIENTO_ANUAL:
        resolveNextEvaluationPredicatesForAnyConvocatoriaReunionType(memoria, cb, root, memoriaPredicateBuilder,
            TipoEvaluacion.Tipo.SEGUIMIENTO_ANUAL.getId(), cq);
        prepareCriteriaWithConvocatoriaReunionLinked(cb, cq, root, memoriaPredicateBuilder, memoria.getId());
        break;
      case EN_EVALUACION_SEGUIMIENTO_FINAL:
      case EN_SECRETARIA_SEGUIMIENTO_FINAL_ACLARACIONES:
      case EN_ACLARACION_SEGUIMIENTO_FINAL:
      case FIN_EVALUACION_SEGUIMIENTO_FINAL:
        resolveNextEvaluationPredicatesForAnyConvocatoriaReunionType(memoria, cb, root, memoriaPredicateBuilder,
            TipoEvaluacion.Tipo.SEGUIMIENTO_FINAL.getId(), cq);
        prepareCriteriaWithConvocatoriaReunionLinked(cb, cq, root, memoriaPredicateBuilder, memoria.getId());
        break;
      default:
        return new FechasMemoria();
    }

    log.debug("getFechaEvaluacion : {} - end");

    return this.entityManager.createQuery(cq).getResultList().stream().findFirst().orElseGet(FechasMemoria::new);

  }

  private void prepareCriteriaWithConvocatoriaReunionLinked(CriteriaBuilder cb, CriteriaQuery<FechasMemoria> cq,
      Root<Evaluacion> root, MemoriaPredicateBuilder memoriaPredicateBuilder, Long memoriaId) {

    memoriaPredicateBuilder.filterWithMemoryIdPredicate(root, cb, memoriaId);
    memoriaPredicateBuilder.filterWithEvaluacionActiva(cb, root);
    memoriaPredicateBuilder.filterWithMemoriaActiva(cb, root);

    cq.orderBy(cb.asc(root.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.fechaEvaluacion)));

    cq.multiselect(root.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.fechaEvaluacion),
        root.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.fechaLimite))
        .where(memoriaPredicateBuilder.build());
  }

  private void prepareCriteriaWithConvocatoriaReunionUnlinked(CriteriaBuilder cb, CriteriaQuery<FechasMemoria> cq,
      Root<ConvocatoriaReunion> root, MemoriaPredicateBuilder memoriaPredicateBuilder) {

    cq.orderBy(cb.asc(root.get(ConvocatoriaReunion_.fechaEvaluacion)));

    cq.multiselect(root.get(ConvocatoriaReunion_.fechaEvaluacion), root.get(ConvocatoriaReunion_.fechaLimite))
        .where(memoriaPredicateBuilder.build());
  }

  private void resolveNextEvaluationPredicatesForAnyConvocatoriaReunionType(final MemoriaPeticionEvaluacion memoria,
      final CriteriaBuilder cb, final Root<Evaluacion> root, final MemoriaPredicateBuilder memoriaPredicateBuilder,
      final Long tipoEvaluacion, final CriteriaQuery<FechasMemoria> cq) {

    memoriaPredicateBuilder.filterWithTipoEvaluacionEqualsTo(cb, root, tipoEvaluacion);
    memoriaPredicateBuilder.filterWithLastVersion(cb, root, memoria.getId(), cq);
  }

  private void resolveNextEvaluationPredicatesWhereConvocatoriaReunionIsOfTypeSeguimiento(
      final MemoriaPeticionEvaluacion memoria, final CriteriaBuilder cb, final Root<ConvocatoriaReunion> root,
      final MemoriaPredicateBuilder memoriaPredicateBuilder, final CriteriaQuery<FechasMemoria> cq) {

    memoriaPredicateBuilder
        .filterWithAnyTipoConvocatoriaReunionNotJoined(cb, root, Constantes.TIPO_CONVOCATORIA_REUNION_SEGUIMIENTO)
        .filterWithComiteConvocatoriaReunion(root, cb, memoria.getComite())
        .filterWithFechaLimiteConvocatoriaReunionGreatestThanNowConvocatoriaReunion(cb, root)
        .filterWithActasNotInConvocatoriasFinalizadasConvocatoriaReunion(cb, root, cq);
  }

  /**
   * Obtiene las peticiones de evaluación en las que es responsable de memoria
   * 
   * @param root
   * @param cb
   * @param cq
   * @param specsMem
   * @param personaRef
   * @return
   */
  private Subquery<Long> getIdsPeticionEvaluacionMemoria(CriteriaBuilder cb,
      CriteriaQuery<MemoriaPeticionEvaluacion> cq, String personaRef) {

    log.debug(
        "getActaConvocatoria(Root<ConvocatoriaReunion> root, CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) - start");

    Subquery<Long> queryGetIdPeticionEvaluacion = cq.subquery(Long.class);
    Root<Memoria> subqRoot = queryGetIdPeticionEvaluacion.from(Memoria.class);

    List<Predicate> predicates = new ArrayList<>();
    predicates.add(cb.isTrue(subqRoot.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.activo)));
    predicates.add(cb.isTrue(subqRoot.get(Memoria_.activo)));
    if (personaRef != null) {
      predicates.add(cb.equal(subqRoot.get(Memoria_.personaRef), personaRef));
    }

    queryGetIdPeticionEvaluacion.select(subqRoot.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id))
        .where(predicates.toArray(new Predicate[] {}));
    log.debug(
        "getActaConvocatoria(Root<ConvocatoriaReunion> root, CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) - end");

    return queryGetIdPeticionEvaluacion;
  }

  /**
   * Identifica si es responsable de la memoria el usuario de la consulta
   * 
   * @param root               root
   * @param cb                 criteria builder
   * @param cq                 criteria query
   * @param personaRefConsulta usuario de la consulta
   * @return subquery que la persona es responsable
   */
  private Subquery<Memoria> isResponsable(Root<Memoria> root, CriteriaBuilder cb,
      CriteriaQuery<MemoriaPeticionEvaluacion> cq, String personaRefConsulta) {
    log.debug("isResponsable : {} - start");

    Subquery<Memoria> queryResponsable = cq.subquery(Memoria.class);
    Root<Memoria> rootQueryResponsable = queryResponsable.from(Memoria.class);

    queryResponsable.select(rootQueryResponsable).where(
        cb.equal(rootQueryResponsable.get(Memoria_.id), root.get(Memoria_.id)),
        cb.equal(root.get(Memoria_.personaRef), personaRefConsulta != null ? personaRefConsulta : ""));

    log.debug("isResponsable : {} - end");

    return queryResponsable;
  }

  @Override
  public Page<Memoria> findAllMemoriasPeticionEvaluacionModificables(Long idComite, Long idPeticionEvaluacion,
      Pageable pageable) {
    log.debug(
        "findAllMemoriasPeticionEvaluacionModificables(Long idComite, Long idPeticionEvaluacion, Pageable pageable) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Memoria> cq = cb.createQuery(Memoria.class);

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Memoria> rootCount = countQuery.from(Memoria.class);
    countQuery.select(cb.count(rootCount));

    // Define FROM clause
    Root<Memoria> root = cq.from(Memoria.class);

    // Memorias activos
    Predicate memoriasActivas = cb.and(cb.equal(root.get(Memoria_.activo), Boolean.TRUE),
        cb.equal(root.get(Memoria_.comite).get(Comite_.id), idComite),
        cb.equal(root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id), idPeticionEvaluacion),
        cb.equal(root.get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id),
            TipoEstadoMemoria.Tipo.DESFAVORABLE.getId()).not(),
        cb.greaterThanOrEqualTo(root.get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id),
            TipoEstadoMemoria.Tipo.FIN_EVALUACION.getId()));

    Predicate memoriasActivasCount = cb.and(cb.equal(rootCount.get(Memoria_.activo), Boolean.TRUE),
        cb.equal(rootCount.get(Memoria_.comite).get(Comite_.id), idComite),
        cb.equal(rootCount.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id), idPeticionEvaluacion),
        cb.equal(rootCount.get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id),
            TipoEstadoMemoria.Tipo.DESFAVORABLE.getId()).not(),
        cb.greaterThanOrEqualTo(rootCount.get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id),
            TipoEstadoMemoria.Tipo.FIN_EVALUACION.getId()));

    // Join all restrictions
    cq.where(memoriasActivas);

    countQuery.where(memoriasActivasCount);

    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    // Número de registros totales para la paginación
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<Memoria> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<Memoria> result = typedQuery.getResultList();

    Page<Memoria> returnValue = new PageImpl<>(result, pageable, count);

    log.debug(
        "findAllMemoriasPeticionEvaluacionModificables(Long idComite, Long idPeticionEvaluacion, Pageable pageable) - end");

    return returnValue;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class FechasMemoria {
    Instant fechaEvaluacion;
    Instant fechaLimite;
  }
}