package org.crue.hercules.sgi.eti.repository.custom;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.data.domain.Sort;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.eti.dto.ActaWithNumEvaluaciones;
import org.crue.hercules.sgi.eti.dto.MemoriaEvaluada;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Acta_;
import org.crue.hercules.sgi.eti.model.Comite_;
import org.crue.hercules.sgi.eti.model.ConflictoInteres;
import org.crue.hercules.sgi.eti.model.ConflictoInteres_;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.Dictamen_;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo_;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluacion_;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Evaluador_;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion_;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion_;
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
   * @param specActa   Acta Specification.
   * @param pageable   la información de la paginación.
   * @param personaRef referencia de la persona.
   * @return la lista de {@link ActaWithNumEvaluaciones} paginadas y/o filtradas.
   */
  public Page<ActaWithNumEvaluaciones> findAllActaWithNumEvaluaciones(Specification<Acta> specActa, Pageable pageable,
      String personaRef) {
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

    List<Predicate> listPredicates = new ArrayList<>();
    List<Predicate> listPredicatesCount = new ArrayList<>();

    if (personaRef != null) {
      Subquery<Long> queryComites = cq.subquery(Long.class);
      Root<Evaluador> subqRootEvaluadores = queryComites.from(Evaluador.class);

      queryComites.select(subqRootEvaluadores.get(Evaluador_.comite).get(Comite_.id)).where(
          cb.in(subqRootEvaluadores.get(Evaluador_.personaRef)).value(personaRef),
          cb.equal(subqRootEvaluadores.get(Evaluador_.activo), Boolean.TRUE),
          cb.or(cb.isNull(subqRootEvaluadores.get(Evaluador_.fechaBaja)),
              cb.greaterThan(subqRootEvaluadores.get(Evaluador_.fechaBaja), Instant.now())));

      listPredicates.add(cb.and(cb.equal(root.get(Acta_.activo), Boolean.TRUE), cb.in(
          root.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.comite).get(
              Comite_.id))
          .value(queryComites)));
      listPredicatesCount.add(cb.and(cb.equal(rootCount.get(Acta_.activo), Boolean.TRUE), cb.in(
          rootCount.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.comite).get(
              Comite_.id))
          .value(queryComites)));
    } else {
      listPredicates.add(cb.and(cb.equal(root.get(Acta_.activo), Boolean.TRUE)));
      listPredicatesCount.add(cb.and(cb.equal(rootCount.get(Acta_.activo), Boolean.TRUE)));
    }

    // Where
    if (specActa != null) {
      listPredicates.add(specActa.toPredicate(root, cq, cb));
      listPredicatesCount.add(specActa.toPredicate(rootCount, cq, cb));
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
        root.get(Acta_.estadoActual).alias("estadoActa"),
        root.get(Acta_.documentoRef).alias("documentoRef"),
        root.get(Acta_.transaccionRef).alias("transaccionRef"));

    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    // Número de registros totales para la paginación
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<ActaWithNumEvaluaciones> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<ActaWithNumEvaluaciones> result = typedQuery.getResultList();
    Page<ActaWithNumEvaluaciones> returnValue = new PageImpl<>(result, pageable, count);

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

    Subquery<Long> queryNumEvaluaciones = cq.subquery(Long.class);
    Root<Evaluacion> subqRoot = queryNumEvaluaciones.from(Evaluacion.class);
    queryNumEvaluaciones.select(cb.countDistinct(subqRoot.get(Evaluacion_.id)))
        .where(cb.and(
            cb.equal(subqRoot.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.id),
                root.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.id)),
            (iniciales) ? cb.equal(subqRoot.get(Evaluacion_.version), 1)
                : cb.greaterThan(subqRoot.get(Evaluacion_.version), 1),
            cb.equal(subqRoot.get(Evaluacion_.esRevMinima), false),
            cb.equal(subqRoot.get(Evaluacion_.activo), true)));

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
            cb.equal(subqRoot.get(Evaluacion_.esRevMinima), false),
            cb.equal(subqRoot.get(Evaluacion_.activo), true)));
    log.debug(
        "getNumEvaluacionesNoEvaluadas(Root<Acta> root, CriteriaBuilder cb, CriteriaQuery<ActaWithNumEvaluaciones> cq) - end");
    return queryNumEvaluaciones;
  }

  /**
   * Devuelve el número de evaluaciones nuevas asociadas a un {@link Acta}
   *
   * @param idActa Id de {@link Acta}.
   * @return número de evaluaciones nuevas
   */
  @Override
  public Long countEvaluacionesNuevas(Long idActa) {

    log.debug("countEvaluacionesNuevas(Long idActa) - start");
    Long returnValue = null;

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Evaluacion> rootCount = countQuery.from(Evaluacion.class);
    Root<Acta> rootActa = countQuery.from(Acta.class);
    rootCount.join(Evaluacion_.memoria);

    countQuery.select(cb.countDistinct(rootCount.get(Evaluacion_.id)))
        .where(cb.and(
            cb.equal(rootCount.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.id),
                rootActa.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.id)),
            cb.equal(rootActa.get(Acta_.id), idActa), cb.isNotNull(rootCount.get(Evaluacion_.dictamen)),
            cb.equal(rootCount.get(Evaluacion_.version), 1)));

    returnValue = entityManager.createQuery(countQuery).getSingleResult();

    log.debug("countEvaluacionesNuevas(Long idActa) - end");
    return returnValue;
  }

  /**
   * Devuelve el número de evaluaciones de revisión sin las de revisión mínima
   *
   * @param idActa Id de {@link Acta}.
   * @return número de evaluaciones
   */
  @Override
  public Long countEvaluacionesRevisionSinMinima(Long idActa) {

    log.debug("countEvaluacionesRevisionSinMinima(Long idActa) - start");
    Long returnValue = null;

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Evaluacion> rootCount = countQuery.from(Evaluacion.class);
    rootCount.join(Evaluacion_.memoria);

    Root<Acta> rootActa = countQuery.from(Acta.class);
    countQuery.select(cb.countDistinct(rootCount.get(Evaluacion_.id)))
        .where(cb.and(
            cb.equal(rootCount.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.id),
                rootActa.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.id)),
            cb.equal(rootActa.get(Acta_.id), idActa), cb.isNotNull(rootCount.get(Evaluacion_.dictamen)),
            cb.greaterThan(rootCount.get(Evaluacion_.version), 1),
            cb.equal(rootCount.get(Evaluacion_.esRevMinima), false)));

    returnValue = entityManager.createQuery(countQuery).getSingleResult();

    log.debug("countEvaluacionesRevisionSinMinima(Long idActa) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista de {@link MemoriaEvaluada} sin las de revisión mínima para
   * una determinada {@link Acta}
   * 
   * @param idActa Id de {@link Acta}.
   * @return lista de memorias evaluadas
   */
  @Override
  public List<MemoriaEvaluada> findAllMemoriasEvaluadasSinRevMinimaByActaId(Long idActa) {
    log.debug("findAllMemoriasEvaluadasSinRevMinimaByActaId : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<MemoriaEvaluada> cq = cb.createQuery(MemoriaEvaluada.class);

    // Define FROM clause
    Root<Evaluacion> root = cq.from(Evaluacion.class);
    Root<Acta> rootActa = cq.from(Acta.class);

    Join<Evaluacion, Memoria> joinMemoria = root.join(Evaluacion_.memoria, JoinType.LEFT);
    Join<Memoria, PeticionEvaluacion> joinPeticionEvaluacion = joinMemoria.join(Memoria_.peticionEvaluacion,
        JoinType.LEFT);

    cq.multiselect(
        joinMemoria.get(Memoria_.id),
        root.get(Evaluacion_.id),
        joinMemoria.get(Memoria_.numReferencia), joinPeticionEvaluacion.get(PeticionEvaluacion_.personaRef),
        root.get(Evaluacion_.dictamen).get(Dictamen_.nombre), root.get(Evaluacion_.version),
        root.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.nombre),
        joinPeticionEvaluacion.get(PeticionEvaluacion_.titulo));

    // Where
    cq.where(
        cb.equal(root.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.id),
            rootActa.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.id)),
        cb.equal(rootActa.get(Acta_.id), idActa), cb.isNotNull(root.get(Evaluacion_.dictamen)),
        cb.equal(root.get(Evaluacion_.esRevMinima), false));

    List<Order> orders = QueryUtils.toOrders(Sort.by(Sort.Direction.ASC, Memoria_.NUM_REFERENCIA), joinMemoria, cb);
    cq.orderBy(orders);

    TypedQuery<MemoriaEvaluada> typedQuery = entityManager.createQuery(cq);

    List<MemoriaEvaluada> result = typedQuery.getResultList();

    log.debug("findAllMemoriasEvaluadasSinRevMinimaByActaId : {} - end");
    return result;
  }

  /**
   * Recupera las evaluaciones del tipo memoria en estado 'En evaluacion' (id = 4)
   * o 'En secretaria revisión minima'(id = 5), o tipo retrospectiva, memoria que
   * requiere retrospectiva y el estado de la RETROSPECTIVA es 'En evaluacion' (id
   * = 4).
   * 
   * @param personaRef Identificador del {@link Evaluador}
   * @return true/false si existen datos
   */
  @Override
  public Boolean hasAssignedActasByEvaluador(String personaRef) {
    log.debug("hasAssignedActasByEvaluador(String personaRef) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<Long> cq = cb.createQuery(Long.class);

    // Define FROM clause
    Root<Acta> root = cq.from(Acta.class);

    Root<Evaluacion> rootEvaluacion = cq.from(Evaluacion.class);

    Subquery<String> queryPersonaRefEquipoTrabajo = cq.subquery(String.class);
    Root<EquipoTrabajo> subqRootEquipoTrabajo = queryPersonaRefEquipoTrabajo.from(EquipoTrabajo.class);

    queryPersonaRefEquipoTrabajo.select(subqRootEquipoTrabajo.get(EquipoTrabajo_.personaRef)).where(
        cb.equal(subqRootEquipoTrabajo.get(EquipoTrabajo_.peticionEvaluacion).get(PeticionEvaluacion_.id),
            rootEvaluacion.get(Evaluacion_.memoria).get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id)));

    Subquery<Long> queryConflictosInteres = cq.subquery(Long.class);
    Root<ConflictoInteres> subqRootConflictosInteres = queryConflictosInteres.from(ConflictoInteres.class);

    queryConflictosInteres.select(subqRootConflictosInteres.get(ConflictoInteres_.evaluador).get(Evaluador_.id)).where(
        cb.in(subqRootConflictosInteres.get(ConflictoInteres_.personaConflictoRef))
            .value(queryPersonaRefEquipoTrabajo));

    Subquery<String> queryEvaluadoresComite = cq.subquery(String.class);
    Root<Evaluador> subqRootEvaluadores = queryEvaluadoresComite.from(Evaluador.class);

    Predicate predicateEvaluacion = cb.and(
        cb.equal(subqRootEvaluadores.get(Evaluador_.comite).get(Comite_.id),
            rootEvaluacion.get(Evaluacion_.memoria).get(Memoria_.comite).get(Comite_.id)),
        cb.in(subqRootEvaluadores.get(Evaluador_.personaRef)).value(personaRef),
        cb.equal(subqRootEvaluadores.get(Evaluador_.activo), Boolean.TRUE),
        cb.not(subqRootEvaluadores.get(Evaluador_.id).in(queryConflictosInteres)));

    queryEvaluadoresComite.select(subqRootEvaluadores.get(Evaluador_.comite).get(Comite_.comite)).where(
        predicateEvaluacion,
        cb.or(cb.isNull(subqRootEvaluadores.get(Evaluador_.fechaBaja)),
            cb.greaterThan(subqRootEvaluadores.get(Evaluador_.fechaBaja), Instant.now())));

    cq.select(root.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.id))
        .where(cb.and(cb.equal(root.get(Acta_.activo), Boolean.TRUE),
            cb.equal(rootEvaluacion.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.id),
                root.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.id)),
            cb.in(root.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.comite).get(Comite_.comite))
                .value(queryEvaluadoresComite)));

    TypedQuery<Long> typedQuery = entityManager.createQuery(cq);

    List<Long> result = typedQuery.getResultList();

    log.debug("hasAssignedActasByEvaluador(String personaRef) - end");
    return !result.isEmpty();
  }
}