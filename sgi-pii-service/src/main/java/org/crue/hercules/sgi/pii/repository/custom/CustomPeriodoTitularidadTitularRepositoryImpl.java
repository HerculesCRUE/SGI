package org.crue.hercules.sgi.pii.repository.custom;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.pii.dto.SolicitudProteccionOutput.Invencion;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidad;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidadTitular;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidadTitular_;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidad_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link PeriodoTitularidadTitular}.
 */
@Slf4j
@Component
public class CustomPeriodoTitularidadTitularRepositoryImpl implements CustomPeriodoTitularidadTitularRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Devuelve una lista de {@link PeriodoTitularidadTitular} que pertenecen a la
   * universidad a fecha 31 de diciembre de una determinada {@link Invencion}
   * 
   * @param invencionId     id de {@link Invencion}
   * @param fechaBaremacion fecha de baremación a 31 de diciembre
   * @param universidadId   id universidad
   * 
   * @return Lista de {@link PeriodoTitularidadTitular}
   */
  @Override
  public PeriodoTitularidadTitular findPeriodoTitularidadTitularesInFechaBaremacion(Long invencionId,
      Instant fechaBaremacion, String universidadId) {

    PeriodoTitularidadTitular result = null;

    log.debug(
        "findPeriodoTitularidadTitularesInFechaBaremacion(invencionId, fechaBaremacion, universidadId) : {} - start");
    try {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();

      CriteriaQuery<PeriodoTitularidadTitular> cq = cb.createQuery(PeriodoTitularidadTitular.class);

      Root<PeriodoTitularidadTitular> root = cq.from(PeriodoTitularidadTitular.class);

      cq.select(root);

      Join<PeriodoTitularidadTitular, PeriodoTitularidad> joinPeriodoTitularidad = root
          .join(PeriodoTitularidadTitular_.periodoTitularidad);

      Predicate predicateTitularUniversidad = cb
          .equal(root.get(PeriodoTitularidadTitular_.titularRef), universidadId);

      Predicate predicateInvencion = cb
          .equal(joinPeriodoTitularidad.get(PeriodoTitularidad_.invencionId), invencionId);

      Predicate predicateParticipacionGreaterThanZero = cb
          .greaterThan(root.get(PeriodoTitularidadTitular_.participacion), BigDecimal.ZERO);

      Predicate predicatePeriodoTitularidadInFechaBaremacion = cb.and(
          cb.lessThanOrEqualTo(joinPeriodoTitularidad.get(PeriodoTitularidad_.fechaInicio), fechaBaremacion),
          cb.and(cb.or(cb.isNull(joinPeriodoTitularidad.get(PeriodoTitularidad_.fechaFin)),
              cb.greaterThanOrEqualTo(joinPeriodoTitularidad.get(PeriodoTitularidad_.fechaFin), fechaBaremacion))));

      cq.where(cb.and(
          predicateInvencion,
          predicateTitularUniversidad,
          predicateParticipacionGreaterThanZero,
          predicatePeriodoTitularidadInFechaBaremacion));

      TypedQuery<PeriodoTitularidadTitular> typedQuery = entityManager.createQuery(cq);
      typedQuery.setMaxResults(1);
      result = typedQuery.getSingleResult();
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    log.debug(
        "findPeriodoTitularidadTitularesProduccionCientifica(invencionId, fechaBaremacion, universidadId) : {} - end");

    return result;
  }

}
