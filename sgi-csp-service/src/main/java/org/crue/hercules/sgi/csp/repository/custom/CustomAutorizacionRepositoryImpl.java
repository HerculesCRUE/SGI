package org.crue.hercules.sgi.csp.repository.custom;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.dto.AutorizacionWithFirstEstado;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.Autorizacion_;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion_;
import org.crue.hercules.sgi.csp.util.CriteriaQueryUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link Autorizacion}.
 */
@Slf4j
@Component
public class CustomAutorizacionRepositoryImpl implements CustomAutorizacionRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  private static final String FECHA_FIRST_ESTADO = "fechaFirstEstado";

  @Override
  public List<Long> findIds(Specification<Autorizacion> specification) {
    log.debug("List<Long> findIds(Specification<Autorizacion> specification) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Autorizacion> root = cq.from(Autorizacion.class);

    cq.select(root.get(Autorizacion_.id)).distinct(true).where(specification.toPredicate(root, cq, cb));

    log.debug("List<Long> findIds(Specification<Autorizacion> specification) - end");

    return entityManager.createQuery(cq).getResultList();
  }

  @Override
  public Page<AutorizacionWithFirstEstado> findAllAutorizacionWithFirstEstado(
      Specification<Autorizacion> specification, Pageable pageable) {
    log.debug(
        "findAllAutorizacionWithFirstEstado(Specification<Autorizacion> specification, Pageable pageable) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<AutorizacionWithFirstEstado> cq = cb.createQuery(AutorizacionWithFirstEstado.class);

    // Define FROM clause
    Root<Autorizacion> root = cq.from(Autorizacion.class);

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Autorizacion> rootCount = countQuery.from(Autorizacion.class);
    countQuery.select(cb.count(rootCount));

    List<Predicate> listPredicates = new ArrayList<>();
    List<Predicate> listPredicatesCount = new ArrayList<>();

    if (specification != null) {
      listPredicates.add(specification.toPredicate(root, cq, cb));
      listPredicatesCount.add(specification.toPredicate(rootCount, countQuery, cb));
    }

    cq.multiselect(root.get(Autorizacion_.id).alias(Autorizacion_.ID),
        root.get(Autorizacion_.observaciones).alias(Autorizacion_.OBSERVACIONES),
        root.get(Autorizacion_.responsableRef).alias(Autorizacion_.RESPONSABLE_REF),
        root.get(Autorizacion_.tituloProyecto).alias(Autorizacion_.TITULO_PROYECTO),
        root.get(Autorizacion_.entidadRef).alias(Autorizacion_.ENTIDAD_REF),
        root.get(Autorizacion_.solicitanteRef).alias(Autorizacion_.SOLICITANTE_REF),
        root.get(Autorizacion_.horasDedicacion).alias(Autorizacion_.HORAS_DEDICACION),
        root.get(Autorizacion_.datosResponsable).alias(Autorizacion_.DATOS_RESPONSABLE),
        root.get(Autorizacion_.datosEntidad).alias(Autorizacion_.DATOS_ENTIDAD),
        root.get(Autorizacion_.datosConvocatoria).alias(Autorizacion_.DATOS_CONVOCATORIA),
        root.get(Autorizacion_.convocatoriaId).alias(Autorizacion_.CONVOCATORIA_ID),
        root.get(Autorizacion_.estado).get(EstadoAutorizacion_.id).alias("estadoId"),
        getFirstEstado(root, cb, cq).alias(FECHA_FIRST_ESTADO));

    String[] selectionNames = new String[] { FECHA_FIRST_ESTADO };

    // Order
    cq.orderBy(CriteriaQueryUtils.toOrders(pageable.getSort(), root, cb, cq, selectionNames));

    // Número de registros totales para la paginación
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    cq.where(listPredicates.toArray(new Predicate[] {}));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<AutorizacionWithFirstEstado> query = entityManager.createQuery(cq);
    List<AutorizacionWithFirstEstado> results = query.getResultList();
    Page<AutorizacionWithFirstEstado> returnValue = new PageImpl<>(results, pageable, count);

    log.debug(
        "findAllAutorizacionWithFirstEstado(Specification<Autorizacion> specification, Pageable pageable) - start");
    return returnValue;
  }

  private Subquery<Instant> getFirstEstado(Root<Autorizacion> root, CriteriaBuilder cb,
      CriteriaQuery<AutorizacionWithFirstEstado> cq) {

    log.debug(
        "getFirstEstado(Root<Autorizacion> root, CriteriaBuilder cb,CriteriaQuery<AutorizacionWithFirstEstado> cq) - start");

    Subquery<Instant> queryFirstEstado = cq.subquery(Instant.class);
    Root<EstadoAutorizacion> subqRoot = queryFirstEstado.from(EstadoAutorizacion.class);
    queryFirstEstado.select(cb.least(subqRoot.get(EstadoAutorizacion_.fecha)))
        .where(cb.equal(subqRoot.get(EstadoAutorizacion_.autorizacionId), root.get(Autorizacion_.id)));

    log.debug(
        "getFirstEstado(Root<Autorizacion> root, CriteriaBuilder cb,CriteriaQuery<AutorizacionWithFirstEstado> cq) - end");

    return queryFirstEstado;
  }

}
