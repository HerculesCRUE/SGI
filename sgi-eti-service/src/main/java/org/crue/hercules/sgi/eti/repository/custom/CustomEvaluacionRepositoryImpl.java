package org.crue.hercules.sgi.eti.repository.custom;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.CargoComite_;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Comentario_;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Comite_;
import org.crue.hercules.sgi.eti.model.ConflictoInteres;
import org.crue.hercules.sgi.eti.model.ConflictoInteres_;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo_;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.EstadoMemoria_;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva_;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluacion_;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Evaluador_;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.Retrospectiva_;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.Tarea_;
import org.crue.hercules.sgi.eti.model.TipoComentario_;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria_;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion_;
import org.crue.hercules.sgi.eti.repository.specification.EvaluadorSpecifications;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link Evaluacion}.
 */
@Component
@Slf4j
public class CustomEvaluacionRepositoryImpl implements CustomEvaluacionRepository {

  private static final Long ID_COMITE_CEEA = 2L;

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtener todas las entidades {@link EvaluacionWithNumComentario} paginadas
   * asociadas a una memoria y anteriores a la evaluaci??n recibida.
   *
   * @param idMemoria        id de la memoria.
   * @param idEvaluacion     id de la evaluaci??n.
   * @param idTipoComentario id del tipo de comentario.
   * @param idTipoEvaluacion id del tipo de evaluaci??n.
   * @param pageable         la informaci??n de la paginaci??n.
   * @return la lista de entidades {@link EvaluacionWithNumComentario} paginadas
   *         y/o filtradas.
   */
  public Page<EvaluacionWithNumComentario> findEvaluacionesAnterioresByMemoria(Long idMemoria, Long idEvaluacion,
      Long idTipoComentario, Long idTipoEvaluacion, Pageable pageable) {
    log.debug("findEvaluacionesAnterioresByMemoria : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<EvaluacionWithNumComentario> cq = cb.createQuery(EvaluacionWithNumComentario.class);

    // Define FROM clause
    Root<Evaluacion> root = cq.from(Evaluacion.class);

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Evaluacion> rootCount = countQuery.from(Evaluacion.class);
    countQuery.select(cb.count(rootCount));

    cq.multiselect(root.alias("evaluacion"), getNumComentarios(root, cb, cq, idTipoComentario).alias("numComentarios"));

    if (idTipoEvaluacion != null) {
      cq.where(cb.equal(root.get(Evaluacion_.memoria).get(Memoria_.id), idMemoria),
          cb.notEqual(root.get(Evaluacion_.id), idEvaluacion), cb.isTrue(root.get(Evaluacion_.activo)),
          cb.equal(root.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), idTipoEvaluacion));
    } else {
      cq.where(cb.equal(root.get(Evaluacion_.memoria).get(Memoria_.id), idMemoria),
          cb.notEqual(root.get(Evaluacion_.id), idEvaluacion), cb.isTrue(root.get(Evaluacion_.activo)));
    }

    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    // N??mero de registros totales para la paginaci??n
    if (idTipoEvaluacion != null) {
      countQuery.where(cb.equal(rootCount.get(Evaluacion_.memoria).get(Memoria_.id), idMemoria),
          cb.notEqual(rootCount.get(Evaluacion_.id), idEvaluacion), cb.isTrue(rootCount.get(Evaluacion_.activo)),
          cb.equal(rootCount.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), idTipoEvaluacion));
    } else {
      countQuery.where(cb.equal(rootCount.get(Evaluacion_.memoria).get(Memoria_.id), idMemoria),
          cb.notEqual(rootCount.get(Evaluacion_.id), idEvaluacion), cb.isTrue(rootCount.get(Evaluacion_.activo)));
    }

    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<EvaluacionWithNumComentario> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<EvaluacionWithNumComentario> result = typedQuery.getResultList();

    Page<EvaluacionWithNumComentario> returnValue = new PageImpl<>(result, pageable, count);

    log.debug("findEvaluacionesAnterioresByMemoria : {} - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link Evaluacion} paginadas y/o filtradas
   * asociadas a determinados tipos de seguimiento final
   * 
   * @param query    la informaci??n del filtro.
   * @param pageable la informaci??n de la paginaci??n.
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  public Page<Evaluacion> findByEvaluacionesEnSeguimientoFinal(String query, Pageable pageable) {

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluacion> cq = cb.createQuery(Evaluacion.class);

    // Define FROM clause
    Root<Evaluacion> root = cq.from(Evaluacion.class);

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Evaluacion> rootCount = countQuery.from(Evaluacion.class);

    List<Predicate> listPredicatesFinal = getPredicatesEvaluacionesEnSeguimientoFinal(root, cb, cq, null);
    List<Predicate> listPredicatesFinalCount = getPredicatesEvaluacionesEnSeguimientoFinal(rootCount, cb, cq, null);

    List<Predicate> listPredicatesAnual = getPredicatesEvaluacionesEnSeguimientoAnual(root, cb, cq, null);
    List<Predicate> listPredicatesAnualCount = getPredicatesEvaluacionesEnSeguimientoAnual(rootCount, cb, cq, null);

    // Where
    if (query != null) {
      Specification<Evaluacion> spec = SgiRSQLJPASupport.toSpecification(query);
      listPredicatesFinal.add(spec.toPredicate(root, cq, cb));
      listPredicatesFinalCount.add(spec.toPredicate(rootCount, cq, cb));

      listPredicatesAnual.add(spec.toPredicate(root, cq, cb));
      listPredicatesAnualCount.add(spec.toPredicate(rootCount, cq, cb));
    }

    Predicate predicateFinal = cb.and(listPredicatesFinal.toArray(new Predicate[] {}));
    Predicate predicateAnual = cb.and(listPredicatesAnual.toArray(new Predicate[] {}));

    // Filtros
    cq.where(cb.or(predicateFinal, predicateAnual));

    // Ordenaci??n
    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    // N??mero de registros totales para la paginaci??n
    Predicate predicateFinalCount = cb.and(listPredicatesFinalCount.toArray(new Predicate[] {}));
    Predicate predicateAnualCount = cb.and(listPredicatesAnualCount.toArray(new Predicate[] {}));
    countQuery.where(cb.or(predicateFinalCount, predicateAnualCount));
    countQuery.select(cb.count(rootCount));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    // Paginaci??n
    TypedQuery<Evaluacion> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<Evaluacion> result = typedQuery.getResultList();

    return new PageImpl<>(result, pageable, count);
  }

  /**
   * Obtener todas las entidades {@link Evaluacion} paginadas y/o filtradas.
   *
   * @param query    la informaci??n del filtro.
   * @param pageable la informaci??n de la paginaci??n.
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  public Page<Evaluacion> findAllByMemoriaAndRetrospectivaEnEvaluacion(String query, Pageable pageable) {
    log.debug("findAllByMemoriaAndRetrospectivaEnEvaluacion(String query, Pageable pageable) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluacion> cq = cb.createQuery(Evaluacion.class);

    // Define FROM clause
    Root<Evaluacion> root = cq.from(Evaluacion.class);

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Evaluacion> rootCount = countQuery.from(Evaluacion.class);
    countQuery.select(cb.count(rootCount));

    Predicate predicateContent = this.getByMemoriaAndRetrospectivaEnEvaluacionPredicate(query, root, cq, cb);
    Predicate predicateCount = this.getByMemoriaAndRetrospectivaEnEvaluacionPredicate(query, rootCount, countQuery, cb);

    // Filtros
    cq.where(predicateContent);

    countQuery.where(predicateCount);

    // Ordenaci??n
    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    // Paginaci??n
    TypedQuery<Evaluacion> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    // N??mero de registros totales para la paginaci??n
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    List<Evaluacion> result = typedQuery.getResultList();
    Page<Evaluacion> returnValue = new PageImpl<>(result, pageable, count);

    log.debug("findAllByMemoriaAndRetrospectivaEnEvaluacion(String query, Pageable pageable) - end");

    return returnValue;

  }

  private Predicate getByMemoriaAndRetrospectivaEnEvaluacionPredicate(String query, Root<Evaluacion> root,
      CriteriaQuery<?> cq, CriteriaBuilder cb) {
    Predicate predicate = cb.and(cb.or(
        cb.and(cb.equal(root.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), 1L),
            cb.in(root.get(Evaluacion_.memoria).get(Memoria_.id)).value(getIdsMemoriasRestropectivas(cb, cq, root))),
        cb.and(cb.equal(root.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), 2L),
            cb.in(root.get(Evaluacion_.memoria).get(Memoria_.id)).value(getIdsMemoriasEstadoActual(cb, cq, root)))));

    predicate = cb.and(predicate, cb.equal(root.get(Evaluacion_.activo), Boolean.TRUE));

    // Where
    if (query != null) {
      Specification<Evaluacion> spec = SgiRSQLJPASupport.toSpecification(query);
      predicate = cb.and(predicate, spec.toPredicate(root, cq, cb));
    }

    return predicate;
  }

  /**
   * Devuelve una subconsulta con el listado de Memorias con estado 4 o 5 donde
   * adem??s la versi??n de la memoria sea igual a la versi??n del listado de
   * evaluaci??n.
   * 
   * @return Subquery<Long> Listado de Memorias con estado En Evaluacion o En
   *         secretar??a revisi??n m??nima
   */
  private Subquery<Long> getIdsMemoriasEstadoActual(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<Evaluacion> root) {

    log.debug("getIdsMemoriasEstadoActual : {} - start");

    Subquery<Long> queryMemoriasEstadoActual = cq.subquery(Long.class);
    Root<Memoria> subqRoot = queryMemoriasEstadoActual.from(Memoria.class);
    queryMemoriasEstadoActual.select(subqRoot.get(Memoria_.id))
        .where(cb.and(subqRoot.get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id).in(Arrays.asList(4L, 5L)),
            cb.equal(root.get(Evaluacion_.version), subqRoot.get(Memoria_.version))));

    log.debug("getIdsMemoriasEstadoActual : {} - end");

    return queryMemoriasEstadoActual;
  }

  /**
   * Recupera las evaluaciones del tipo memoria en estado 'En evaluacion' (id = 4)
   * o 'En secretaria revisi??n minima'(id = 5), o tipo retrospectiva, memoria que
   * requiere retrospectiva y el estado de la RETROSPECTIVA es 'En evaluacion' (id
   * = 4).
   * 
   * @param personaRef Identificador del {@link Evaluacion}
   * @param query      filtro de b??squeda.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  @Override
  public Page<Evaluacion> findByEvaluador(String personaRef, String query, Pageable pageable) {
    log.debug("findByEvaluador(String personaRef, String query, Pageable pageable) - start");

    // Create query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluacion> cq = cb.createQuery(Evaluacion.class);

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Evaluacion> rootCount = countQuery.from(Evaluacion.class);
    countQuery.select(cb.count(rootCount));

    // Define FROM clause
    Root<Evaluacion> rootEvaluacion = cq.from(Evaluacion.class);

    List<Predicate> listPredicates = getPredicatesByEvaluador(rootEvaluacion, cb, cq, personaRef);

    List<Predicate> listPredicatesCount = getPredicatesByEvaluador(rootCount, cb, cq, personaRef);

    // Where
    if (query != null) {
      Specification<Evaluacion> spec = SgiRSQLJPASupport.toSpecification(query);
      listPredicates.add(spec.toPredicate(rootEvaluacion, cq, cb));
      listPredicatesCount.add(spec.toPredicate(rootCount, cq, cb));
    }

    // Filtros
    cq.where(cb.and(listPredicates.toArray(new Predicate[] {})),
        cb.equal(rootEvaluacion.get(Evaluacion_.activo), Boolean.TRUE));

    // Ordenaci??n
    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), rootEvaluacion, cb);
    cq.orderBy(orders);

    // N??mero de registros totales para la paginaci??n
    countQuery.where(cb.and(listPredicatesCount.toArray(new Predicate[] {})),
        cb.equal(rootCount.get(Evaluacion_.activo), Boolean.TRUE));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    // Paginaci??n
    TypedQuery<Evaluacion> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }
    List<Evaluacion> result = typedQuery.getResultList();
    Page<Evaluacion> returnValue = new PageImpl<>(result, pageable, count);

    log.debug("findByEvaluador(String personaRef, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Evaluacion}, en estado "En evaluaci??n
   * seguimiento anual" (id = 11), "En evaluaci??n seguimiento final" (id = 12) o
   * "En secretar??a seguimiento final aclaraciones" (id = 13), paginadas asociadas
   * a un evaluador.
   * 
   * @param personaRef Persona Ref del {@link Evaluador}
   * @param query      filtro de b??squeda.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  @Override
  public Page<Evaluacion> findEvaluacionesEnSeguimientosByEvaluador(String personaRef, String query,
      Pageable pageable) {
    log.debug("findEvaluacionesEnSeguimientosByEvaluador(String personaRef, String query, Pageable pageable) - start");

    // Create query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluacion> cq = cb.createQuery(Evaluacion.class);

    // Define FROM clause
    Root<Evaluacion> rootEvaluacion = cq.from(Evaluacion.class);

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Evaluacion> rootCount = countQuery.from(Evaluacion.class);
    countQuery.select(cb.count(rootCount));

    // Evaluaciones en seguimiento
    List<Predicate> listPredicatesAnual = getPredicatesEvaluacionesEnSeguimientoAnual(rootEvaluacion, cb, cq,
        personaRef);
    List<Predicate> listPredicatesAnualCount = getPredicatesEvaluacionesEnSeguimientoAnual(rootCount, cb, cq,
        personaRef);

    List<Predicate> listPredicatesFinal = getPredicatesEvaluacionesEnSeguimientoFinal(rootEvaluacion, cb, cq,
        personaRef);
    List<Predicate> listPredicatesFinalCount = getPredicatesEvaluacionesEnSeguimientoFinal(rootCount, cb, cq,
        personaRef);

    // Where
    if (query != null) {
      Specification<Evaluacion> spec = SgiRSQLJPASupport.toSpecification(query);
      listPredicatesFinal.add(spec.toPredicate(rootEvaluacion, cq, cb));
      listPredicatesFinalCount.add(spec.toPredicate(rootCount, cq, cb));

      listPredicatesAnual.add(spec.toPredicate(rootEvaluacion, cq, cb));
      listPredicatesAnualCount.add(spec.toPredicate(rootCount, cq, cb));
    }

    Predicate predicateFinal = cb.and(listPredicatesFinal.toArray(new Predicate[] {}));
    Predicate predicateAnual = cb.and(listPredicatesAnual.toArray(new Predicate[] {}));

    // Filtros
    cq.where(cb.or(predicateFinal, predicateAnual));

    // Ordenaci??n
    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), rootEvaluacion, cb);
    cq.orderBy(orders);

    // N??mero de registros totales para la paginaci??n
    Predicate predicateFinalCount = cb.and(listPredicatesFinalCount.toArray(new Predicate[] {}));
    Predicate predicateAnualCount = cb.and(listPredicatesAnualCount.toArray(new Predicate[] {}));
    countQuery.where(cb.or(predicateFinalCount, predicateAnualCount));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    // Paginaci??n
    TypedQuery<Evaluacion> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }
    List<Evaluacion> result = typedQuery.getResultList();
    Page<Evaluacion> returnValue = new PageImpl<>(result, pageable, count);

    log.debug("findEvaluacionesEnSeguimientosByEvaluador(String personaRef, String query, Pageable pageable) - end");
    return returnValue;
  }

  private List<Predicate> getPredicatesEvaluacionesEnSeguimientoAnual(Root<Evaluacion> rootEvaluacion,
      CriteriaBuilder cb, CriteriaQuery<Evaluacion> cq, String personaRef) {
    return getPredicatesEvaluacionesEnSeguimientoAnualByEvaluadorAndEvaluacion(rootEvaluacion, cb, cq, personaRef,
        null);
  }

  /**
   * Crea el predicate necesario para recuperar las evaluaciones de tipo memoria
   * que se encuentran en seguimiento:
   * 
   * "En evaluaci??n seguimiento anual" (id = 11), "En evaluaci??n seguimiento
   * final" (id = 12) o "En secretar??a seguimiento final aclaraciones" (id = 13)
   * 
   * @param idEvaluacion   identificador de la {@link Evaluacion}
   * @param rootEvaluacion
   * @param cb
   * @return lista con los predicates necesarios
   */
  private List<Predicate> getPredicatesEvaluacionesEnSeguimientoAnualByEvaluadorAndEvaluacion(
      Root<Evaluacion> rootEvaluacion, CriteriaBuilder cb, CriteriaQuery<Evaluacion> cq, String personaRef,
      Long idEvaluacion) {

    log.debug("getPredicatesEvaluacionesEnSeguimientoAnualByEvaluadorAndEvaluacion : {} - start");
    List<Predicate> listPredicates = new ArrayList<>();

    listPredicates.add(rootEvaluacion.get(Evaluacion_.memoria).get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id)
        .in(Arrays.asList(Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_ANUAL)));

    listPredicates.add(rootEvaluacion.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id)
        .in(Arrays.asList(Constantes.TIPO_EVALUACION_SEGUIMIENTO_FINAL)).not());

    listPredicates.add(cb.isTrue(rootEvaluacion.get(Evaluacion_.activo)));
    listPredicates.add(rootEvaluacion.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id)
        .in(Arrays.asList(Constantes.TIPO_EVALUACION_SEGUIMIENTO_ANUAL)));

    listPredicates.add(rootEvaluacion.get(Evaluacion_.memoria).get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id)
        .in(Arrays.asList(Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_FINAL,
            Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_FINAL_ACLARACIONES))
        .not());

    if (personaRef != null) {
      Subquery<String> queryPersonaRefTareas = cq.subquery(String.class);
      Root<Tarea> subqRootTareas = queryPersonaRefTareas.from(Tarea.class);

      queryPersonaRefTareas.select(subqRootTareas.get(Tarea_.equipoTrabajo).get(EquipoTrabajo_.personaRef))
          .where(cb.equal(subqRootTareas.get(Tarea_.memoria).get(Memoria_.id),
              rootEvaluacion.get(Evaluacion_.memoria).get(Memoria_.id)));

      Subquery<Long> queryConflictosInteres = cq.subquery(Long.class);
      Root<ConflictoInteres> subqRootConflictosInteres = queryConflictosInteres.from(ConflictoInteres.class);

      queryConflictosInteres.select(subqRootConflictosInteres.get(ConflictoInteres_.evaluador).get(Evaluador_.id))
          .where(
              cb.in(subqRootConflictosInteres.get(ConflictoInteres_.personaConflictoRef)).value(queryPersonaRefTareas));

      Subquery<String> queryEvaluadores = cq.subquery(String.class);
      Root<Evaluador> subqRootEvaluadores = queryEvaluadores.from(Evaluador.class);

      Predicate predicateEvaluacion = null;
      if (idEvaluacion != null) {
        predicateEvaluacion = cb.and(
            cb.equal(subqRootEvaluadores.get(Evaluador_.comite).get(Comite_.id),
                rootEvaluacion.get(Evaluacion_.memoria).get(Memoria_.comite).get(Comite_.id)),
            cb.in(subqRootEvaluadores.get(Evaluador_.personaRef)).value(personaRef),
            cb.equal(subqRootEvaluadores.get(Evaluador_.activo), Boolean.TRUE),
            cb.not(subqRootEvaluadores.get(Evaluador_.id).in(queryConflictosInteres)),
            cb.equal(rootEvaluacion.get(Evaluacion_.id), idEvaluacion));
      } else {
        predicateEvaluacion = cb.and(
            cb.equal(subqRootEvaluadores.get(Evaluador_.comite).get(Comite_.id),
                rootEvaluacion.get(Evaluacion_.memoria).get(Memoria_.comite).get(Comite_.id)),
            cb.in(subqRootEvaluadores.get(Evaluador_.personaRef)).value(personaRef),
            cb.equal(subqRootEvaluadores.get(Evaluador_.activo), Boolean.TRUE),
            cb.not(subqRootEvaluadores.get(Evaluador_.id).in(queryConflictosInteres)));
      }

      queryEvaluadores.select(subqRootEvaluadores.get(Evaluador_.comite).get(Comite_.comite)).where(predicateEvaluacion,
          cb.or(cb.isNull(subqRootEvaluadores.get(Evaluador_.fechaBaja)),
              cb.greaterThan(subqRootEvaluadores.get(Evaluador_.fechaBaja), Instant.now())));

      listPredicates
          .add(cb.or(
              cb.in(queryEvaluadores).value(rootEvaluacion.get(Evaluacion_.evaluador1).get(Evaluador_.comite).get(
                  Comite_.comite)),
              cb.in(queryEvaluadores).value(rootEvaluacion.get(Evaluacion_.evaluador2).get(Evaluador_.comite)
                  .get(Comite_.comite))));
    }

    Predicate memoriaVersion = cb.equal(rootEvaluacion.get(Evaluacion_.version),
        rootEvaluacion.get(Evaluacion_.memoria).get(Memoria_.version));
    listPredicates.add(memoriaVersion);

    log.debug("getPredicatesEvaluacionesEnSeguimientoAnualByEvaluadorAndEvaluacion : {} - end");

    return listPredicates;
  }

  private Subquery<Long> getNumComentarios(Root<Evaluacion> root, CriteriaBuilder cb,
      CriteriaQuery<EvaluacionWithNumComentario> cq, Long idTipoComentario) {

    log.debug("getNumComentarios : {} - start");

    Subquery<Long> queryNumComentarios = cq.subquery(Long.class);
    Root<Comentario> subqRoot = queryNumComentarios.from(Comentario.class);
    queryNumComentarios.select(cb.count(subqRoot.get(Comentario_.id)))
        .where(cb.and(cb.equal(subqRoot.get(Comentario_.evaluacion).get(Evaluacion_.id), root.get(Evaluacion_.id)),
            cb.equal(subqRoot.get(Comentario_.tipoComentario).get(TipoComentario_.id), idTipoComentario)));

    log.debug("getNumComentarios : {} - end");
    return queryNumComentarios;
  }

  /**
   * Devuelve una subconsulta con el listado de Memorias con Retrospectivas en
   * estado 4 (En Evaluacion) donde la versi??n de la memoria sea igual a la
   * versi??n del listado de evaluaci??n.
   * 
   * @return Subquery<Long> Listado de Memorias con Retrospectivas en estado En
   *         Evaluacion
   */
  private Subquery<Long> getIdsMemoriasRestropectivas(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<Evaluacion> root) {

    log.debug("getIdsMemoriasRestropectivas : {} - start");

    Subquery<Long> queryMemorias = cq.subquery(Long.class);
    Root<Memoria> subqRoot = queryMemorias.from(Memoria.class);
    queryMemorias.select(subqRoot.get(Memoria_.id)).where(
        cb.and(cb.in(subqRoot.get(Memoria_.retrospectiva).get(Retrospectiva_.id)).value(getRetrospectivas(cb, cq)),
            cb.equal(root.get(Evaluacion_.version), subqRoot.get(Memoria_.version))));

    log.debug("getIdsMemoriasRestropectivas : {} - end");

    return queryMemorias;
  }

  /**
   * Devuelve una subconsulta con el listado de retrospectivas en estado 4 (En
   * Evaluacion)
   * 
   * @return Subquery<Long> Listado de Retrospectivas en estado En Evaluacion
   */

  private Subquery<Long> getRetrospectivas(CriteriaBuilder cb, CriteriaQuery<?> cq) {

    log.debug("getRetrospectivas : {} - start");

    Subquery<Long> queryRetrospectivas = cq.subquery(Long.class);
    Root<Retrospectiva> subqRoot = queryRetrospectivas.from(Retrospectiva.class);
    queryRetrospectivas.select(subqRoot.get(Retrospectiva_.id))
        .where(cb.equal(subqRoot.get(Retrospectiva_.estadoRetrospectiva).get(EstadoRetrospectiva_.id), 4L));

    log.debug("getRetrospectivas : {} - end");

    return queryRetrospectivas;

  }

  private List<Predicate> getPredicatesEvaluacionesEnSeguimientoFinal(Root<Evaluacion> root, CriteriaBuilder cb,
      CriteriaQuery<Evaluacion> cq, String personaRef) {
    return getPredicatesEvaluacionesEnSeguimientoFinalByEvaluadorAndEvaluacion(root, cb, cq, personaRef, null);
  }

  /**
   * Devuelve la lista de predicados para la b??squeda de evaluaciones en
   * seguimiento final.
   * 
   * @param idEvaluacion identificador de la {@link Evaluacion}
   * @param root         root
   * @param cb           Criteria builder
   * @return lista de predicates.
   */
  private List<Predicate> getPredicatesEvaluacionesEnSeguimientoFinalByEvaluadorAndEvaluacion(Root<Evaluacion> root,
      CriteriaBuilder cb, CriteriaQuery<Evaluacion> cq, String personaRef, Long idEvaluacion) {
    log.debug("getPredicatesEvaluacionesEnSeguimientoFinalByEvaluadorAndEvaluacion : {} - start");
    List<Predicate> listPredicates = new ArrayList<>();

    listPredicates.add(root.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id)
        .in(Arrays.asList(Constantes.TIPO_EVALUACION_SEGUIMIENTO_FINAL)));

    listPredicates.add(root.get(Evaluacion_.memoria).get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id)
        .in(Arrays.asList(Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_ANUAL)).not());

    listPredicates.add(cb.isTrue(root.get(Evaluacion_.activo)));
    listPredicates.add(root.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id)
        .in(Arrays.asList(Constantes.TIPO_EVALUACION_SEGUIMIENTO_ANUAL)).not());

    listPredicates.add(root.get(Evaluacion_.memoria).get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id)
        .in(Arrays.asList(Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_FINAL,
            Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_FINAL_ACLARACIONES)));

    if (personaRef != null) {
      Subquery<String> queryPersonaRefTareas = cq.subquery(String.class);
      Root<Tarea> subqRootTareas = queryPersonaRefTareas.from(Tarea.class);

      queryPersonaRefTareas.select(subqRootTareas.get(Tarea_.equipoTrabajo).get(EquipoTrabajo_.personaRef)).where(cb
          .equal(subqRootTareas.get(Tarea_.memoria).get(Memoria_.id), root.get(Evaluacion_.memoria).get(Memoria_.id)));

      Subquery<Long> queryConflictosInteres = cq.subquery(Long.class);
      Root<ConflictoInteres> subqRootConflictosInteres = queryConflictosInteres.from(ConflictoInteres.class);

      queryConflictosInteres.select(subqRootConflictosInteres.get(ConflictoInteres_.evaluador).get(Evaluador_.id))
          .where(
              cb.in(subqRootConflictosInteres.get(ConflictoInteres_.personaConflictoRef)).value(queryPersonaRefTareas));

      Subquery<String> queryEvaluadores = cq.subquery(String.class);
      Root<Evaluador> subqRootEvaluadores = queryEvaluadores.from(Evaluador.class);

      Predicate predicateEvaluacion = null;
      if (idEvaluacion != null) {
        predicateEvaluacion = cb.and(
            cb.equal(subqRootEvaluadores.get(Evaluador_.comite).get(Comite_.id),
                root.get(Evaluacion_.memoria).get(Memoria_.comite).get(Comite_.id)),
            cb.in(subqRootEvaluadores.get(Evaluador_.personaRef)).value(personaRef),
            cb.equal(subqRootEvaluadores.get(Evaluador_.activo), Boolean.TRUE),
            cb.not(subqRootEvaluadores.get(Evaluador_.id).in(queryConflictosInteres)),
            cb.equal(root.get(Evaluacion_.id), idEvaluacion));
      } else {
        predicateEvaluacion = cb.and(
            cb.equal(subqRootEvaluadores.get(Evaluador_.comite).get(Comite_.id),
                root.get(Evaluacion_.memoria).get(Memoria_.comite).get(Comite_.id)),
            cb.in(subqRootEvaluadores.get(Evaluador_.personaRef)).value(personaRef),
            cb.equal(subqRootEvaluadores.get(Evaluador_.activo), Boolean.TRUE),
            cb.not(subqRootEvaluadores.get(Evaluador_.id).in(queryConflictosInteres)));
      }

      queryEvaluadores.select(subqRootEvaluadores.get(Evaluador_.comite).get(Comite_.comite)).where(predicateEvaluacion,
          cb.or(cb.isNull(subqRootEvaluadores.get(Evaluador_.fechaBaja)),
              cb.greaterThan(subqRootEvaluadores.get(Evaluador_.fechaBaja), Instant.now())));

      listPredicates
          .add(cb.or(cb.in(queryEvaluadores).value(root.get(Evaluacion_.evaluador1).get(Evaluador_.comite).get(
              Comite_.comite)),
              cb.in(queryEvaluadores).value(root.get(Evaluacion_.evaluador2).get(Evaluador_.comite)
                  .get(Comite_.comite))));
    }

    listPredicates.add(cb.equal(root.get(Evaluacion_.version), root.get(Evaluacion_.memoria).get(Memoria_.version)));

    listPredicates.add(cb.and(cb.isTrue(root.get(Evaluacion_.activo))));
    log.debug("getPredicatesEvaluacionesEnSeguimientoFinalByEvaluadorAndEvaluacion : {} - end");
    return listPredicates;
  }

  private List<Predicate> getPredicatesByEvaluador(Root<Evaluacion> root, CriteriaBuilder cb,
      CriteriaQuery<Evaluacion> cq, String personaRef) {
    return getPredicatesByEvaluadorAndEvaluacion(root, cb, cq, personaRef, null);
  }

  /**
   * Recupera los predicados para la b??squeda de evaluaciones por evaluador.
   * 
   * @param root       Root
   * @param cb         criteria builder
   * @param cq         criteria query
   * @param personaRef Referencia de persona
   * @return listado de predicados
   */
  private List<Predicate> getPredicatesByEvaluadorAndEvaluacion(Root<Evaluacion> root, CriteriaBuilder cb,
      CriteriaQuery<Evaluacion> cq, String personaRef, Long idEvaluacion) {

    List<Predicate> listPredicates = new ArrayList<>();
    Predicate memoriaVersion = cb.equal(root.get(Evaluacion_.version),
        root.get(Evaluacion_.memoria).get(Memoria_.version));

    Subquery<String> queryPersonaRefTareas = cq.subquery(String.class);
    Root<Tarea> subqRootTareas = queryPersonaRefTareas.from(Tarea.class);

    queryPersonaRefTareas.select(subqRootTareas.get(Tarea_.equipoTrabajo).get(EquipoTrabajo_.personaRef)).where(
        cb.equal(subqRootTareas.get(Tarea_.memoria).get(Memoria_.id), root.get(Evaluacion_.memoria).get(Memoria_.id)));

    Subquery<Long> queryConflictosInteres = cq.subquery(Long.class);
    Root<ConflictoInteres> subqRootConflictosInteres = queryConflictosInteres.from(ConflictoInteres.class);

    queryConflictosInteres.select(subqRootConflictosInteres.get(ConflictoInteres_.evaluador).get(Evaluador_.id)).where(
        cb.in(subqRootConflictosInteres.get(ConflictoInteres_.personaConflictoRef)).value(queryPersonaRefTareas));

    Subquery<String> queryEvaluadores = cq.subquery(String.class);
    Root<Evaluador> subqRootEvaluadores = queryEvaluadores.from(Evaluador.class);

    Predicate predicateEvaluacion = null;
    if (idEvaluacion != null) {
      predicateEvaluacion = cb.and(
          cb.equal(subqRootEvaluadores.get(Evaluador_.comite).get(Comite_.id),
              root.get(Evaluacion_.memoria).get(Memoria_.comite).get(Comite_.id)),
          cb.in(subqRootEvaluadores.get(Evaluador_.personaRef)).value(personaRef),
          cb.equal(subqRootEvaluadores.get(Evaluador_.activo), Boolean.TRUE),
          cb.not(subqRootEvaluadores.get(Evaluador_.id).in(queryConflictosInteres)),
          cb.equal(root.get(Evaluacion_.id), idEvaluacion));
    } else {
      predicateEvaluacion = cb.and(
          cb.equal(subqRootEvaluadores.get(Evaluador_.comite).get(Comite_.id),
              root.get(Evaluacion_.memoria).get(Memoria_.comite).get(Comite_.id)),
          cb.in(subqRootEvaluadores.get(Evaluador_.personaRef)).value(personaRef),
          cb.equal(subqRootEvaluadores.get(Evaluador_.activo), Boolean.TRUE),
          cb.not(subqRootEvaluadores.get(Evaluador_.id).in(queryConflictosInteres)));
    }

    queryEvaluadores.select(subqRootEvaluadores.get(Evaluador_.comite).get(Comite_.comite)).where(predicateEvaluacion,
        cb.or(cb.isNull(subqRootEvaluadores.get(Evaluador_.fechaBaja)),
            cb.greaterThan(subqRootEvaluadores.get(Evaluador_.fechaBaja), Instant.now())));

    // Memoria en estado 'En evaluacion' (id = 4)
    // o 'En secretaria revisi??n minima'(id = 5)

    Predicate memoria = cb.and(cb.equal(root.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), 2L),
        cb.in(root.get(Evaluacion_.memoria).get(Memoria_.id)).value(getIdsMemoriasEstadoActual(cb, cq, root)),
        memoriaVersion,
        cb.or(
            cb.in(queryEvaluadores).value(root.get(Evaluacion_.evaluador1).get(Evaluador_.comite).get(Comite_.comite)),
            cb.in(queryEvaluadores)
                .value(root.get(Evaluacion_.evaluador2).get(Evaluador_.comite).get(Comite_.comite))));

    // Tipo retrospectiva, memoria Requiere retrospectiva y el estado de la
    // RETROSPECTIVA es 'En evaluacion' (id = 4)

    Subquery<Long> queryRetrospectiva = cq.subquery(Long.class);
    Root<Evaluacion> subqRoot = queryRetrospectiva.from(Evaluacion.class);

    Predicate requiereRetrospectiva = cb.isTrue(subqRoot.get(Evaluacion_.memoria).get(Memoria_.requiereRetrospectiva));
    Predicate estadoRetrospectiva = cb.equal(subqRoot.get(Evaluacion_.memoria).get(Memoria_.retrospectiva)
        .get(Retrospectiva_.estadoRetrospectiva).get(EstadoRetrospectiva_.id), 4L);
    Predicate comite = cb.equal(subqRoot.get(Evaluacion_.memoria).get(Memoria_.comite).get(Comite_.id), 2L);
    Predicate tipoEvaluacion = cb.equal(subqRoot.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), 1L);
    Predicate evaluador = cb.or(
        cb.in(queryEvaluadores).value(subqRoot.get(Evaluacion_.evaluador1).get(Evaluador_.comite).get(Comite_.comite)),
        cb.in(queryEvaluadores).value(subqRoot.get(Evaluacion_.evaluador2).get(Evaluador_.comite).get(Comite_.comite)));

    queryRetrospectiva.select(subqRoot.get(Evaluacion_.id))
        .where(cb.and(requiereRetrospectiva, estadoRetrospectiva, comite, tipoEvaluacion,
            evaluador, memoriaVersion));

    Predicate retrospectiva = cb.in(root.get(Evaluacion_.id)).value(queryRetrospectiva);

    listPredicates.add(cb.or(memoria, retrospectiva));

    return listPredicates;
  }

  /**
   * Recupera las evaluaciones del tipo memoria en estado 'En evaluacion' (id = 4)
   * o 'En secretaria revisi??n minima'(id = 5), o tipo retrospectiva, memoria que
   * requiere retrospectiva y el estado de la RETROSPECTIVA es 'En evaluacion' (id
   * = 4).
   * 
   * @param personaRef Identificador del {@link Evaluador}
   * @return true/false si existen datos
   */
  @Override
  public Boolean hasAssignedEvaluacionesByEvaluador(String personaRef) {
    log.debug("hasAssignedEvaluacionesByEvaluador(String personaRef) - start");

    // Create query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluacion> cq = cb.createQuery(Evaluacion.class);

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Evaluacion> rootCount = countQuery.from(Evaluacion.class);
    countQuery.select(cb.count(rootCount));

    List<Predicate> listPredicatesCount = getPredicatesByEvaluador(rootCount, cb, cq, personaRef);

    // N??mero de registros totales para la paginaci??n
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    log.debug("hasAssignedEvaluacionesByEvaluador(String personaRef) - end");
    return count > 0;
  }

  /**
   * Recupera la Evaluaci??n del tipo memoria en estado 'En evaluacion' (id = 4) o
   * 'En secretaria revisi??n minima'(id = 5), o tipo retrospectiva, memoria que
   * requiere retrospectiva y el estado de la RETROSPECTIVA es 'En evaluacion' (id
   * = 4).
   * 
   * @param idEvaluacion identificador de la {@link Evaluacion}
   * @param personaRef   Identificador del {@link Evaluador}
   * @return true/false si existen datos
   */
  @Override
  public Boolean isEvaluacionEvaluableByEvaluador(Long idEvaluacion, String personaRef) {
    log.debug("isEvaluacionEvaluableByEvaluador(Long idEvaluacion, String personaRef) - start");

    // Create query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluacion> cq = cb.createQuery(Evaluacion.class);

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Evaluacion> rootCount = countQuery.from(Evaluacion.class);
    countQuery.select(cb.count(rootCount));

    List<Predicate> listPredicatesCount = getPredicatesByEvaluadorAndEvaluacion(rootCount, cb, cq, personaRef,
        idEvaluacion);

    // N??mero de registros totales para la paginaci??n
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    log.debug("isEvaluacionEvaluableByEvaluador(Long idEvaluacion, String personaRef) - end");
    return count > 0;
  }

  /**
   * Obtiene todas las entidades {@link Evaluacion}, en estado "En evaluaci??n
   * seguimiento anual" (id = 11), "En evaluaci??n seguimiento final" (id = 12) o
   * "En secretar??a seguimiento final aclaraciones" (id = 13), paginadas asociadas
   * a un evaluador.
   * 
   * @param personaRef Persona Ref del {@link Evaluador}
   * @return true/false si existen datos
   */
  @Override
  public Boolean hasAssignedEvaluacionesSeguimientoByEvaluador(String personaRef) {
    log.debug("hasAssignedEvaluacionesSeguimientoByEvaluador(String personaRef) - start");

    // Create query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluacion> cq = cb.createQuery(Evaluacion.class);

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Evaluacion> root = countQuery.from(Evaluacion.class);

    countQuery.select(cb.count(root));
    // Evaluaciones en seguimiento
    List<Predicate> listPredicatesAnualCount = getPredicatesEvaluacionesEnSeguimientoAnual(root, cb, cq, personaRef);
    List<Predicate> listPredicatesFinalCount = getPredicatesEvaluacionesEnSeguimientoFinal(root, cb, cq, personaRef);

    // N??mero de registros totales para la paginaci??n
    Predicate predicateFinalCount = cb.and(listPredicatesFinalCount.toArray(new Predicate[] {}));
    Predicate predicateAnualCount = cb.and(listPredicatesAnualCount.toArray(new Predicate[] {}));
    countQuery.where(cb.or(predicateFinalCount, predicateAnualCount));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    log.debug("hasAssignedEvaluacionesSeguimientoByEvaluador(String personaRef) - end");
    return count > 0;
  }

  /**
   * Obtiene la {@link Evaluacion}, en estado "En evaluaci??n seguimiento anual"
   * (id = 11), "En evaluaci??n seguimiento final" (id = 12) o "En secretar??a
   * seguimiento final aclaraciones" (id = 13), paginadas asociadas a un
   * evaluador.
   * 
   * @param idEvaluacion identificador de la Evaluaci??n en Seguimiento
   * @param personaRef   Persona Ref del {@link Evaluador}
   * @return true/false si existen datos
   */
  @Override
  public Boolean isEvaluacionSeguimientoEvaluableByEvaluador(Long idEvaluacion, String personaRef) {
    log.debug("isEvaluacionSeguimientoEvaluableByEvaluador(Long idEvaluacion, String personaRef) - start");

    // Create query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluacion> cq = cb.createQuery(Evaluacion.class);

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Evaluacion> root = countQuery.from(Evaluacion.class);

    countQuery.select(cb.count(root));
    // Evaluaciones en seguimiento
    List<Predicate> listPredicatesAnualCount = getPredicatesEvaluacionesEnSeguimientoAnualByEvaluadorAndEvaluacion(root,
        cb, cq, personaRef, idEvaluacion);
    List<Predicate> listPredicatesFinalCount = getPredicatesEvaluacionesEnSeguimientoFinalByEvaluadorAndEvaluacion(root,
        cb, cq, personaRef, idEvaluacion);

    // N??mero de registros totales para la paginaci??n
    Predicate predicateFinalCount = cb.and(listPredicatesFinalCount.toArray(new Predicate[] {}));
    Predicate predicateAnualCount = cb.and(listPredicatesAnualCount.toArray(new Predicate[] {}));
    countQuery.where(cb.or(predicateFinalCount, predicateAnualCount));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    log.debug("isEvaluacionSeguimientoEvaluableByEvaluador(Long idEvaluacion, String personaRef) - end");
    return count > 0;
  }

  /**
   * Retorna el identificador de la usuarioRef del presidente
   * 
   * @param idEvaluacion Id de {@link Evaluacion}.
   * @return id del presidente
   */
  @Override
  public String findIdPresidenteByIdEvaluacion(Long idEvaluacion) {
    log.debug("findIdPresidenteByIdEvaluacion(idEvaluacion) - start");
    String idPresidente = null;
    try {
      // Crete query
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();

      CriteriaQuery<String> cq = cb.createQuery(String.class);

      // Define FROM clause
      Root<Evaluador> root = cq.from(Evaluador.class);
      Join<Evaluador, Comite> joinEComite = root.join(Evaluador_.comite);
      Join<Evaluador, CargoComite> joinCargoComite = root.join(Evaluador_.cargoComite);

      Root<Evaluacion> rootEvaluacion = cq.from(Evaluacion.class);
      Join<Evaluacion, ConvocatoriaReunion> joinConvocatoriaReunion = rootEvaluacion
          .join(Evaluacion_.convocatoriaReunion);
      Join<ConvocatoriaReunion, Comite> joinCRComite = joinConvocatoriaReunion.join(ConvocatoriaReunion_.comite);

      cq.multiselect(root.get(Evaluador_.personaRef));

      List<Predicate> predicates = new ArrayList<>();
      Predicate pJoinComite = cb.equal(joinEComite.get(Comite_.id), joinCRComite.get(Comite_.id));
      Predicate pIdEvaluacionEq = cb.equal(rootEvaluacion.get(Evaluacion_.id), idEvaluacion);
      Predicate pComiteCEEA = cb.equal(joinEComite.get(Comite_.id), ID_COMITE_CEEA);
      Predicate pComiteActivo = cb.equal(joinEComite.get(Comite_.activo), true);
      Predicate pEvaluadorActivo = cb.equal(root.get(Evaluador_.activo), true);
      Predicate pCargoComiteActivo = cb.equal(joinCargoComite.get(CargoComite_.activo), true);
      Predicate pCargoComitePresidente = cb.equal(cb.lower(joinCargoComite.get(CargoComite_.nombre)),
          EvaluadorSpecifications.PRESIDENTE);

      Predicate pFechaBajaIsNull = cb.isNull(root.get(Evaluador_.fechaBaja));
      Predicate pFechaAltaLTEFechaEvaluacion = cb.lessThanOrEqualTo(root.get(Evaluador_.fechaAlta),
          joinConvocatoriaReunion.get(ConvocatoriaReunion_.fechaEvaluacion));
      Predicate pAndFechaBajaIsNullAndFechaAlta = cb.and(pFechaBajaIsNull, pFechaAltaLTEFechaEvaluacion);

      Predicate pFechaBajaGTEFechaEvaluacion = cb.greaterThanOrEqualTo(root.get(Evaluador_.fechaBaja),
          joinConvocatoriaReunion.get(ConvocatoriaReunion_.fechaEvaluacion));
      Predicate pAndFechaBajaFechaAlta = cb.and(pFechaBajaGTEFechaEvaluacion, pFechaAltaLTEFechaEvaluacion);

      Predicate pOrFechaBajaFechaAlta = cb.or(pAndFechaBajaIsNullAndFechaAlta, pAndFechaBajaFechaAlta);

      predicates.add(pJoinComite);
      predicates.add(pIdEvaluacionEq);
      predicates.add(pComiteCEEA);
      predicates.add(pComiteActivo);
      predicates.add(pCargoComitePresidente);
      predicates.add(pCargoComiteActivo);
      predicates.add(pEvaluadorActivo);
      predicates.add(pOrFechaBajaFechaAlta);

      // Where
      cq.where(predicates.toArray(new Predicate[] {}));

      TypedQuery<String> typedQuery = entityManager.createQuery(cq);

      idPresidente = typedQuery.getSingleResult();

    } catch (Exception e) {
      log.error(e.getMessage());
    }
    log.debug("findIdPresidenteByIdEvaluacion(idEvaluacion) - end");
    return idPresidente;
  }

  /**
   * Retorna la primera fecha de env??o a secretar??a (hist??rico estado)
   * 
   * @param idEvaluacion Id de {@link Evaluacion}.
   * @return fecha de env??o a secretar??a
   */
  @Override
  public Instant findFirstFechaEnvioSecretariaByIdEvaluacion(Long idEvaluacion) {
    log.debug("findFirstFechaEnvioSecretariaByIdEvaluacion(idEvaluacion) - start");
    Instant fechaEnvioSecretaria = null;
    try {
      // Crete query
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();

      CriteriaQuery<Object> cq = cb.createQuery(Object.class);

      // Define FROM clause
      Root<Evaluacion> rootEvaluacion = cq.from(Evaluacion.class);
      Root<EstadoMemoria> rootEstadoMemoria = cq.from(EstadoMemoria.class);

      cq.select(rootEstadoMemoria.get(EstadoMemoria_.fechaEstado));

      Join<Evaluacion, Memoria> joinMemoria = rootEvaluacion.join(Evaluacion_.memoria);

      List<Predicate> predicates = new ArrayList<>();
      Predicate pJoinEstadoMemoria = cb.equal(rootEstadoMemoria.get(EstadoMemoria_.memoria).get(Memoria_.id),
          joinMemoria.get(Memoria_.id));
      Predicate pIdEvaluacionEq = cb.equal(rootEvaluacion.get(Evaluacion_.id), idEvaluacion);
      Predicate pTipoEstadoMemoriaEq3 = cb
          .equal(rootEstadoMemoria.get(EstadoMemoria_.tipoEstadoMemoria).get(TipoEstadoMemoria_.id), 3L);
      predicates.add(pJoinEstadoMemoria);
      predicates.add(pIdEvaluacionEq);
      predicates.add(pTipoEstadoMemoriaEq3);

      // Where
      cq.where(predicates.toArray(new Predicate[] {}));
      cq.orderBy(cb.asc(rootEstadoMemoria.get(EstadoMemoria_.fechaEstado)));

      TypedQuery<Object> typedQuery = entityManager.createQuery(cq);
      typedQuery.setMaxResults(1);

      fechaEnvioSecretaria = (Instant) typedQuery.getSingleResult();

    } catch (Exception e) {
      log.error(e.getMessage());
    }
    log.debug("findFirstFechaEnvioSecretariaByIdEvaluacion(idEvaluacion) - end");
    return fechaEnvioSecretaria;
  }
}
