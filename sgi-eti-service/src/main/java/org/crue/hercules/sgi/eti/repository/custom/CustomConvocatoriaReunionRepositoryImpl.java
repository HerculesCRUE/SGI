package org.crue.hercules.sgi.eti.repository.custom;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.eti.dto.ConvocatoriaReunionDatosGenerales;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Acta_;
import org.crue.hercules.sgi.eti.model.Comite_;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.Dictamen_;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluacion_;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa_;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion_;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link ConvocatoriaReunion}.
 */
@Slf4j
@Component
public class CustomConvocatoriaReunionRepositoryImpl implements CustomConvocatoriaReunionRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obteniene la entidad {@link ConvocatoriaReunionDatosGenerales} que contiene
   * la convocatoria con el identificador proporcionado, un campo que nos indica
   * el número de evaluaciones activas que no son revisión mínima y otro para
   * indicar si tiene Acta.
   *
   * @param idConvocatoria id de la convocatoria.
   * 
   * @return la {@link ConvocatoriaReunionDatosGenerales}
   */
  @Override
  public Optional<ConvocatoriaReunionDatosGenerales> findByIdWithDatosGenerales(Long idConvocatoria) {
    log.debug("findByIdWithDatosGenerales(Long idConvocatoria) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq = cb.createQuery(ConvocatoriaReunionDatosGenerales.class);

    // Define FROM clause
    Root<ConvocatoriaReunion> root = cq.from(ConvocatoriaReunion.class);

    // Where
    if (idConvocatoria != null) {
      cq.where(cb.equal(root.get(ConvocatoriaReunion_.id), idConvocatoria));
      // Execute query
      cq.multiselect(root, getNumEvaluacionesActivasNoRevMin(root, cb, cq, idConvocatoria).alias("numEvaluaciones"),
          getActaConvocatoria(root, cb, cq, idConvocatoria).alias("idActa"));
    }

    TypedQuery<ConvocatoriaReunionDatosGenerales> typedQuery = entityManager.createQuery(cq);
    ConvocatoriaReunionDatosGenerales result = null;

    try {
      result = typedQuery.getSingleResult();
    } catch (NoResultException nre) {
      return Optional.empty();
    }

    log.debug("findByIdWithDatosGenerales(Long idConvocatoria) - start");

    return Optional.of(result);
  }

  /**
   * Devuelve una lista de convocatorias de reunión que no tengan acta
   *
   * @return la lista de convocatorias de reunión
   */
  @Override
  public List<ConvocatoriaReunion> findConvocatoriasReunionSinActa() {
    log.debug("findConvocatoriasReunionSinActa() - start");

    // Create Query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ConvocatoriaReunion> cq = cb.createQuery(ConvocatoriaReunion.class);

    // Definir FROM clause
    Root<ConvocatoriaReunion> root = cq.from(ConvocatoriaReunion.class);

    List<Predicate> listPredicates = new ArrayList<Predicate>();

    listPredicates.add(cb.equal(root.get(ConvocatoriaReunion_.activo), Boolean.TRUE));
    listPredicates.add(root.get(ConvocatoriaReunion_.id).in(getConvocatoriasActa(cb, cq, root)).not());

    // Filtros
    cq.where(listPredicates.toArray(new Predicate[] {}));

    TypedQuery<ConvocatoriaReunion> typedQuery = entityManager.createQuery(cq);
    List<ConvocatoriaReunion> result = typedQuery.getResultList();

    log.debug("findConvocatoriasReunionSinActa() - end");

    return result;

  }

  /**
   * Devuelve una lista de convocatorias de reunión que no tengan acta en estado
   * finalizada
   *
   * @return la lista de convocatorias de reunión
   */
  @Override
  public Optional<ConvocatoriaReunion> findFirstConvocatoriaReunionSinActaFinalizadaByComiteOrderByFechaEvaluacionAsc(
      Long idComite) {
    log.debug("findFirstConvocatoriaReunionSinActaFinalizadaByComiteOrderByFechaEvaluacionAsc() - start");

    // Create Query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ConvocatoriaReunion> cq = cb.createQuery(ConvocatoriaReunion.class);

    // Definir FROM clause
    Root<ConvocatoriaReunion> root = cq.from(ConvocatoriaReunion.class);

    List<Predicate> listPredicates = new ArrayList<Predicate>();

    listPredicates.add(cb.equal(root.get(ConvocatoriaReunion_.activo), Boolean.TRUE));
    listPredicates.add(root.get(ConvocatoriaReunion_.id).in(getConvocatoriasActaFinalizada(cb, cq, root)).not());
    listPredicates.add(cb.equal(root.get(ConvocatoriaReunion_.comite).get(Comite_.id), idComite));
    listPredicates.add(root.get(ConvocatoriaReunion_.tipoConvocatoriaReunion).get(TipoConvocatoriaReunion_.id).in(Arrays
        .asList(Constantes.TIPO_CONVOCATORIA_REUNION_ORDINARIA, Constantes.TIPO_CONVOCATORIA_REUNION_EXTRAORDINARIA)));
    listPredicates.add(cb.greaterThan(root.get(ConvocatoriaReunion_.fechaLimite), Instant.now()));

    // Filtros
    cq.where(listPredicates.toArray(new Predicate[] {}));
    List<Order> orders = QueryUtils.toOrders(Sort.by(Sort.Direction.ASC, ConvocatoriaReunion_.FECHA_EVALUACION), root,
        cb);
    cq.orderBy(orders);

    TypedQuery<ConvocatoriaReunion> typedQuery = entityManager.createQuery(cq);
    Optional<ConvocatoriaReunion> result = typedQuery.getResultList().stream().findFirst();

    log.debug("findFirstConvocatoriaReunionSinActaFinalizadaByComiteOrderByFechaEvaluacionAsc() - end");

    return result;

  }

  /**
   * Subquery para obtener el número de {@link Evaluacion} de una
   * {@link ConvocatoriaReunion} activas y que no son revisión mínima
   * 
   * @param root
   * @param cb
   * @param cq
   * @param idConvocatoria
   * @return Subquery<Long>
   */
  private Subquery<Long> getNumEvaluacionesActivasNoRevMin(Root<ConvocatoriaReunion> root, CriteriaBuilder cb,
      CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) {

    log.debug(
        "getNumEvaluacionesActivasNoRevMin(Root<ConvocatoriaReunion> root, CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) - start");

    Subquery<Long> queryNumEvaluaciones = cq.subquery(Long.class);
    Root<Evaluacion> subqRoot = queryNumEvaluaciones.from(Evaluacion.class);
    queryNumEvaluaciones.select(cb.countDistinct(subqRoot.get(Evaluacion_.id)))
        .where(cb.and(
            cb.equal(subqRoot.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.id), idConvocatoria),
            cb.and(cb.isTrue(subqRoot.get(Evaluacion_.activo)), cb.isFalse(subqRoot.get(Evaluacion_.esRevMinima)))));

    log.debug(
        "getNumEvaluacionesActivasNoRevMin(Root<ConvocatoriaReunion> root, CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) - end");

    return queryNumEvaluaciones;
  }

  /**
   * Subquery para obtener el {@link Acta} de la {@link ConvocatoriaReunion}
   * 
   * @param root
   * @param cb
   * @param cq
   * @param idConvocatoria
   * @return Subquery<Long>
   */
  private Subquery<Long> getActaConvocatoria(Root<ConvocatoriaReunion> root, CriteriaBuilder cb,
      CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) {

    log.debug(
        "getActaConvocatoria(Root<ConvocatoriaReunion> root, CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) - start");

    Subquery<Long> queryGetActa = cq.subquery(Long.class);
    Root<Acta> subqRoot = queryGetActa.from(Acta.class);
    queryGetActa.select(subqRoot.get(Acta_.id))
        .where(cb.and(cb.equal(subqRoot.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.id), idConvocatoria),
            cb.isTrue(subqRoot.get(Acta_.activo))));
    log.debug(
        "getActaConvocatoria(Root<ConvocatoriaReunion> root, CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) - end");

    return queryGetActa;
  }

  /**
   * Devuelve una subconsulta con el listado de Convocatorias de Reunión que
   * tienen actas finalizadas asociadas y que además esté activa
   * 
   * @param cb   Criteria builder
   * @param cq   criteria query
   * @param root root a ConvocatoriaReunion
   * 
   * @return Subquery<Long> Listado de Convocatorias de Reunión que tienen acta
   *         finalizada
   * 
   */
  private Subquery<Long> getConvocatoriasActaFinalizada(CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunion> cq,
      Root<ConvocatoriaReunion> root) {

    log.debug("getConvocatoriasActaFinalizada : {} - start");

    Subquery<Long> queryActasConvocatoria = cq.subquery(Long.class);
    Root<Acta> subqRoot = queryActasConvocatoria.from(Acta.class);
    queryActasConvocatoria.select(subqRoot.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.id))
        .where(cb.and(
            cb.equal(subqRoot.get(Acta_.estadoActual).get(TipoEstadoActa_.id), Constantes.TIPO_ESTADO_ACTA_FINALIZADA),
            cb.equal(subqRoot.get(Acta_.activo), Boolean.TRUE)));

    log.debug("getConvocatoriasActaFinalizada : {} - end");

    return queryActasConvocatoria;
  }

  /**
   * Devuelve una subconsulta con el listado de Convocatorias de Reunión que no
   * tienen acta asociada y que además esté activa
   * 
   * @param cb   Criteria builder
   * @param cq   criteria query
   * @param root root a ConvocatoriaReunion
   * 
   * @return Subquery<Long> Listado de Convocatorias de Reunión que no tienen acta
   *         asociada
   */
  private Subquery<Long> getConvocatoriasActa(CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunion> cq,
      Root<ConvocatoriaReunion> root) {

    log.debug("getConvocatoriasActa : {} - start");

    Subquery<Long> queryActasConvocatoria = cq.subquery(Long.class);
    Root<Acta> subqRoot = queryActasConvocatoria.from(Acta.class);
    queryActasConvocatoria.select(subqRoot.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.id))
        .where(cb.equal(subqRoot.get(Acta_.activo), Boolean.TRUE));

    log.debug("getConvocatoriasActa : {} - end");

    return queryActasConvocatoria;
  }

  /**
   * Retorna la fecha convocatoria y acta (codigo convocatoria) de la evaluación
   * de tipo memoria de la memoria original con dictamen Favorable
   * 
   * @param idEvaluacion Id de la {@link Evaluacion}
   * @param idDictamen   Id del {@link Dictamen}
   * 
   * @return ConvocatoriaReunion
   */
  @Override
  public ConvocatoriaReunion findConvocatoriaUltimaEvaluacionTipoMemoria(Long idEvaluacion, Long idDictamen) {
    log.debug("findConvocatoriaUltimaEvaluacionTipoMemoria(idEvaluacion, idDictamen) - start");
    ConvocatoriaReunion result = null;

    try {
      // Crete query
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();

      CriteriaQuery<ConvocatoriaReunion> cq = cb.createQuery(ConvocatoriaReunion.class);

      // Define FROM clause
      Root<Evaluacion> root = cq.from(Evaluacion.class);
      Join<Evaluacion, ConvocatoriaReunion> joinConvocatoriaReunion = root.join(Evaluacion_.convocatoriaReunion);

      cq.multiselect(joinConvocatoriaReunion.get(ConvocatoriaReunion_.id),
          joinConvocatoriaReunion.get(ConvocatoriaReunion_.fechaEvaluacion),
          joinConvocatoriaReunion.get(ConvocatoriaReunion_.anio),
          joinConvocatoriaReunion.get(ConvocatoriaReunion_.numeroActa));

      Subquery<Long> sqMemoriaOriginal = cq.subquery(Long.class);
      Root<Evaluacion> subqRoot = sqMemoriaOriginal.from(Evaluacion.class);
      Join<Evaluacion, Memoria> joinMemoria = subqRoot.join(Evaluacion_.memoria);

      List<Predicate> sqPredicates = new ArrayList<>();
      Predicate pSqIdTipoEvaluacionEq = cb.equal(subqRoot.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), 2L);
      Predicate pSqIdEvaluacionEq = cb.equal(subqRoot.get(Evaluacion_.id), idEvaluacion);
      sqPredicates.add(pSqIdTipoEvaluacionEq);
      sqPredicates.add(pSqIdEvaluacionEq);

      sqMemoriaOriginal.select(joinMemoria.get(Memoria_.memoriaOriginal).get(Memoria_.id));
      sqMemoriaOriginal.where(sqPredicates.toArray(new Predicate[] {}));

      List<Predicate> predicates = new ArrayList<>();
      Predicate pIdDictamenEq = cb.equal(root.get(Evaluacion_.dictamen).get(Dictamen_.id), idDictamen);
      Predicate pIdTipoEvaluacionEq = cb.equal(root.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), 2L);

      predicates.add(pIdDictamenEq);
      predicates.add(pIdTipoEvaluacionEq);

      Predicate pMemoriaOriginal = cb.equal(root.get(Evaluacion_.memoria).get(Memoria_.id), sqMemoriaOriginal);
      predicates.add(pMemoriaOriginal);

      // Where
      cq.where(predicates.toArray(new Predicate[] {}));

      TypedQuery<ConvocatoriaReunion> typedQuery = entityManager.createQuery(cq);
      typedQuery.setMaxResults(1);

      result = typedQuery.getSingleResult();

    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return result;
  }

}
