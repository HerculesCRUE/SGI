package org.crue.hercules.sgi.pii.repository.custom;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.crue.hercules.sgi.pii.dto.InvencionDto;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.Invencion_;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidad;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidadTitular;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidadTitular_;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidad_;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion_;
import org.crue.hercules.sgi.pii.model.TipoProteccion_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link Invencion}.
 */
@Slf4j
@Component
public class CustomInvencionRepositoryImpl implements CustomInvencionRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Devuelve una lista de {@link InvencionDto} que se incorporarán a la
   * baremación
   * de producción científica
   * 
   * @param fechaInicioBaremacion fecha inicio de baremación
   * @param fechaFinBaremacion    fecha fin de baremación
   * @param universidadId         id universidad
   * 
   * @return Lista de {@link InvencionDto}
   */
  @Override
  public List<InvencionDto> findInvencionesProduccionCientifica(Instant fechaInicioBaremacion,
      Instant fechaFinBaremacion, String universidadId) {

    log.debug(
        "findInvencionesProduccionCientifica(fechaInicioBaremacion, fechaFinBaremacion, universidadId) : {} - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<InvencionDto> cq = cb.createQuery(InvencionDto.class);

    Root<Invencion> root = cq.from(Invencion.class);

    cq.multiselect(
        root.get(Invencion_.id),
        root.get(Invencion_.titulo),
        root.get(Invencion_.tipoProteccion).get(TipoProteccion_.id));

    Predicate predicateInvencionIsActiva = cb.equal(root.get(Activable_.activo), Boolean.TRUE);

    Subquery<Long> queryTitularUniversidad = cq.subquery(Long.class);
    Root<PeriodoTitularidadTitular> rootPeriodoTitularidadTitular = queryTitularUniversidad
        .from(PeriodoTitularidadTitular.class);
    Join<PeriodoTitularidadTitular, PeriodoTitularidad> joinPeriodoTitularidad = rootPeriodoTitularidadTitular
        .join(PeriodoTitularidadTitular_.periodoTitularidad);

    Predicate predicatePeriodoTitularidadInFechaBaremacion = cb.and(
        cb.lessThanOrEqualTo(joinPeriodoTitularidad.get(PeriodoTitularidad_.fechaInicio), fechaFinBaremacion),
        cb.and(cb.or(cb.isNull(joinPeriodoTitularidad.get(PeriodoTitularidad_.fechaFin)),
            cb.greaterThanOrEqualTo(joinPeriodoTitularidad.get(PeriodoTitularidad_.fechaFin), fechaInicioBaremacion))));

    Predicate predicateTitularUniversidad = cb
        .equal(rootPeriodoTitularidadTitular.get(PeriodoTitularidadTitular_.titularRef), universidadId);

    Predicate predicateParticipacionGreaterThanZero = cb
        .greaterThan(rootPeriodoTitularidadTitular.get(PeriodoTitularidadTitular_.participacion), BigDecimal.ZERO);

    Predicate existsQueryTitularUniversidad = cb.exists(queryTitularUniversidad
        .select(rootPeriodoTitularidadTitular.get(PeriodoTitularidadTitular_.id))
        .where(cb.and(predicateTitularUniversidad,
            predicateParticipacionGreaterThanZero,
            predicatePeriodoTitularidadInFechaBaremacion)));

    Subquery<Long> querySolicitudProteccion = cq.subquery(Long.class);
    Root<SolicitudProteccion> rootSolicitudProteccion = querySolicitudProteccion
        .from(SolicitudProteccion.class);

    Path<Instant> fechaConcesion = rootSolicitudProteccion.get(SolicitudProteccion_.fechaConcesion);
    Predicate predicateSolicitudProteccionInFechaBaremacion = cb.and(
        cb.lessThanOrEqualTo(fechaConcesion, fechaFinBaremacion),
        cb.greaterThanOrEqualTo(fechaConcesion, fechaInicioBaremacion));

    Predicate existsQuerySolicitudProteccion = cb.exists(querySolicitudProteccion
        .select(rootSolicitudProteccion.get(SolicitudProteccion_.id))
        .where(predicateSolicitudProteccionInFechaBaremacion));

    cq.where(cb.and(
        predicateInvencionIsActiva,
        existsQueryTitularUniversidad,
        existsQuerySolicitudProteccion));

    TypedQuery<InvencionDto> typedQuery = entityManager.createQuery(cq);
    List<InvencionDto> result = typedQuery.getResultList();

    log.debug(
        "findInvencionesProduccionCientifica(fechaInicioBaremacion, fechaFinBaremacion, universidadId) : {} - end");

    return result;
  }

  /**
   * Obtiene los ids de {@link Invencion} que cumplen con la specification
   * recibida.
   * 
   * @param specification condiciones que deben cumplir.
   * @return lista de ids de {@link Invencion}.
   */
  @Override
  public List<Long> findIds(Specification<Invencion> specification) {
    log.debug("findIds(Specification<Invencion> specification) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Invencion> root = cq.from(Invencion.class);

    cq.select(root.get(Invencion_.id)).distinct(true).where(specification.toPredicate(root, cq, cb));

    log.debug("findIds(Specification<Invencion> specification) - end");

    return entityManager.createQuery(cq).getResultList();
  }

}
