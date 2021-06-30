package org.crue.hercules.sgi.csp.repository.custom;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.dto.SolicitudProyectoPresupuestoTotalConceptoGasto;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoPresupuestoTotales;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link Proyecto}.
 */
@Slf4j
@Component
public class CustomSolicitudProyectoPresupuestoRepositoryImpl implements CustomSolicitudProyectoPresupuestoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtiene el {@link SolicitudProyectoPresupuestoTotales} de la
   * {@link Solicitud}.
   * 
   * @param solicitudId Id de la {@link Solicitud}.
   * @return {@link SolicitudProyectoPresupuestoTotales}.
   */
  @Override
  public SolicitudProyectoPresupuestoTotales getTotales(Long solicitudId) {
    log.debug("SolicitudProyectoPresupuestoTotales getTotales(Long solicitudId) - start");
    // Crete query
    final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    final CriteriaQuery<SolicitudProyectoPresupuestoTotales> cq = cb
        .createQuery(SolicitudProyectoPresupuestoTotales.class);

    // Define FROM Solicitud clause
    Root<Solicitud> root = cq.from(Solicitud.class);

    // Total presupuestado no ajeno
    Subquery<BigDecimal> sqTotalPresupuestadoNoAjeno = cq.subquery(BigDecimal.class);
    Root<SolicitudProyectoPresupuesto> rootTotalPresupuestadoNoAjeno = sqTotalPresupuestadoNoAjeno
        .from(SolicitudProyectoPresupuesto.class);
    Join<SolicitudProyectoPresupuesto, SolicitudProyecto> joinTotalPresupuestadoNoAjenoSolicitudProyecto = rootTotalPresupuestadoNoAjeno
        .join(SolicitudProyectoPresupuesto_.solicitudProyecto);
    joinTotalPresupuestadoNoAjenoSolicitudProyecto.join(SolicitudProyecto_.solicitud);

    sqTotalPresupuestadoNoAjeno
        .select(cb.sum(rootTotalPresupuestadoNoAjeno.get(SolicitudProyectoPresupuesto_.importePresupuestado)));
    sqTotalPresupuestadoNoAjeno.where(
        cb.and(cb.isTrue(rootTotalPresupuestadoNoAjeno.get(SolicitudProyectoPresupuesto_.financiacionAjena)).not(),
            cb.equal(rootTotalPresupuestadoNoAjeno.get(SolicitudProyectoPresupuesto_.solicitudProyecto)
                .get(SolicitudProyecto_.solicitud).get(Solicitud_.id), root.get(Solicitud_.id))));

    // Total solicitado no ajeno
    Subquery<BigDecimal> sqTotalSolicitadoNoAjeno = cq.subquery(BigDecimal.class);
    Root<SolicitudProyectoPresupuesto> rootTotalSolicitadoNoAjeno = sqTotalSolicitadoNoAjeno
        .from(SolicitudProyectoPresupuesto.class);
    Join<SolicitudProyectoPresupuesto, SolicitudProyecto> joinTotalSolicitadoNoAjenoSolicitudProyecto = rootTotalSolicitadoNoAjeno
        .join(SolicitudProyectoPresupuesto_.solicitudProyecto);
    joinTotalSolicitadoNoAjenoSolicitudProyecto.join(SolicitudProyecto_.solicitud);

    sqTotalSolicitadoNoAjeno
        .select(cb.sum(rootTotalSolicitadoNoAjeno.get(SolicitudProyectoPresupuesto_.importeSolicitado)));
    sqTotalSolicitadoNoAjeno
        .where(cb.and(cb.isTrue(rootTotalSolicitadoNoAjeno.get(SolicitudProyectoPresupuesto_.financiacionAjena)).not(),
            cb.equal(rootTotalSolicitadoNoAjeno.get(SolicitudProyectoPresupuesto_.solicitudProyecto)
                .get(SolicitudProyecto_.solicitud).get(Solicitud_.id), root.get(Solicitud_.id))));

    // Total presupuestado ajeno
    Subquery<BigDecimal> sqTotalPresupuestadoAjeno = cq.subquery(BigDecimal.class);
    Root<SolicitudProyectoPresupuesto> rootTotalPresupuestadoAjeno = sqTotalPresupuestadoAjeno
        .from(SolicitudProyectoPresupuesto.class);
    Join<SolicitudProyectoPresupuesto, SolicitudProyecto> joinTotalPresupuestadoAjenoSolicitudProyecto = rootTotalPresupuestadoAjeno
        .join(SolicitudProyectoPresupuesto_.solicitudProyecto);
    joinTotalPresupuestadoAjenoSolicitudProyecto.join(SolicitudProyecto_.solicitud);

    sqTotalPresupuestadoAjeno
        .select(cb.sum(rootTotalPresupuestadoAjeno.get(SolicitudProyectoPresupuesto_.importePresupuestado)));
    sqTotalPresupuestadoAjeno
        .where(cb.and(cb.isTrue(rootTotalPresupuestadoAjeno.get(SolicitudProyectoPresupuesto_.financiacionAjena)),
            cb.equal(rootTotalPresupuestadoAjeno.get(SolicitudProyectoPresupuesto_.solicitudProyecto)
                .get(SolicitudProyecto_.solicitud).get(Solicitud_.id), root.get(Solicitud_.id))));

    // Total solicitado ajeno
    Subquery<BigDecimal> sqTotalSolicitadoAjeno = cq.subquery(BigDecimal.class);
    Root<SolicitudProyectoPresupuesto> rootTotalSolicitadoAjeno = sqTotalSolicitadoAjeno
        .from(SolicitudProyectoPresupuesto.class);
    Join<SolicitudProyectoPresupuesto, SolicitudProyecto> joinTotalSolicitadoAjenoSolicitudProyecto = rootTotalSolicitadoAjeno
        .join(SolicitudProyectoPresupuesto_.solicitudProyecto);
    joinTotalSolicitadoAjenoSolicitudProyecto.join(SolicitudProyecto_.solicitud);

    sqTotalSolicitadoAjeno
        .select(cb.sum(rootTotalSolicitadoAjeno.get(SolicitudProyectoPresupuesto_.importeSolicitado)));
    sqTotalSolicitadoAjeno
        .where(cb.and(cb.isTrue(rootTotalSolicitadoAjeno.get(SolicitudProyectoPresupuesto_.financiacionAjena)),
            cb.equal(rootTotalSolicitadoAjeno.get(SolicitudProyectoPresupuesto_.solicitudProyecto)
                .get(SolicitudProyecto_.solicitud).get(Solicitud_.id), root.get(Solicitud_.id))));

    cq.where(cb.equal(root.get(Solicitud_.id), solicitudId));

    // Define DTO projection
    cq.multiselect(
        // total presupuestado no ajeno
        cb.coalesce(sqTotalPresupuestadoNoAjeno.getSelection(), new BigDecimal(0)),
        // total solicitado no ajeno
        cb.coalesce(sqTotalSolicitadoNoAjeno.getSelection(), new BigDecimal(0)),
        // total presupuestado ajeno
        cb.coalesce(sqTotalPresupuestadoAjeno.getSelection(), new BigDecimal(0)),
        // total solicitado ajeno
        cb.coalesce(sqTotalSolicitadoAjeno.getSelection(), new BigDecimal(0)));
    // Execute query
    final TypedQuery<SolicitudProyectoPresupuestoTotales> q = entityManager.createQuery(cq);

    final SolicitudProyectoPresupuestoTotales result = q.getSingleResult();

    log.debug("SolicitudProyectoPresupuestoTotales getTotales(Long solicitudId) - end");
    return result;
  }

  /**
   * Obtiene los {@link SolicitudProyectoPresupuestoTotalConceptoGasto} de la
   * {@link Solicitud}.
   * 
   * @param solicitudId Id de la {@link Solicitud}.
   * @return lista de {@link SolicitudProyectoPresupuestoTotalConceptoGasto}.
   */
  @Override
  public List<SolicitudProyectoPresupuestoTotalConceptoGasto> getSolicitudProyectoPresupuestoTotalConceptoGastos(
      Long solicitudId) {
    log.debug(
        "SolicitudProyectoPresupuestoTotales getSolicitudProyectoPresupuestoTotalConceptoGastos(Long solicitudId) - start");

    // Crete query
    final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    final CriteriaQuery<SolicitudProyectoPresupuestoTotalConceptoGasto> cq = cb
        .createQuery(SolicitudProyectoPresupuestoTotalConceptoGasto.class);

    // Define FROM SolicitudProyectoPresupuesto clause
    Root<SolicitudProyectoPresupuesto> root = cq.from(SolicitudProyectoPresupuesto.class);
    Join<SolicitudProyectoPresupuesto, SolicitudProyecto> joinSolicitudProyecto = root
        .join(SolicitudProyectoPresupuesto_.solicitudProyecto);
    joinSolicitudProyecto.join(SolicitudProyecto_.solicitud);

    cq.where(cb.equal(
        root.get(SolicitudProyectoPresupuesto_.solicitudProyecto).get(SolicitudProyecto_.solicitud).get(Solicitud_.id),
        solicitudId));
    cq.groupBy(root.get(SolicitudProyectoPresupuesto_.conceptoGasto));

    // Define DTO projection
    cq.multiselect(
        // total convocatoria
        root.get(SolicitudProyectoPresupuesto_.conceptoGasto),
        // total
        cb.sum(root.get(SolicitudProyectoPresupuesto_.importeSolicitado)));

    // Execute query
    final TypedQuery<SolicitudProyectoPresupuestoTotalConceptoGasto> q = entityManager.createQuery(cq);

    final List<SolicitudProyectoPresupuestoTotalConceptoGasto> result = q.getResultList();

    log.debug(
        "SolicitudProyectoPresupuestoTotales getSolicitudProyectoPresupuestoTotalConceptoGastos(Long solicitudId) - start");
    return result;
  }

  /**
   * Devuelve la suma del importe solicitado de las fuentes de financiación que no
   * son ajenas.
   * 
   * @param solicitudId Id de la {@link Solicitud}.
   * @return La suma de los importes solicitados.
   */
  @Override
  public BigDecimal sumImporteSolicitadoBySolicitudIdAndFinanciacionAjenaIsFalse(Long solicitudId) {
    // Crete query
    final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    final CriteriaQuery<BigDecimal> cq = cb.createQuery(BigDecimal.class);

    // Define FROM SolicitudProyectoPresupuesto clause
    Root<SolicitudProyectoPresupuesto> root = cq.from(SolicitudProyectoPresupuesto.class);

    // Define SELECT
    cq.select(
        // sumImporteSolicitado
        cb.sum(root.<BigDecimal>get(SolicitudProyectoPresupuesto_.importeSolicitado)));

    // Define WHERE
    cq.where(cb.and(
        // BySolicitudId
        cb.equal(root.get(SolicitudProyectoPresupuesto_.solicitudProyecto).get(SolicitudProyecto_.solicitud)
            .get(Solicitud_.id), solicitudId),
        // AndFinanciacionAjenaIsFalse
        cb.equal(root.get(SolicitudProyectoPresupuesto_.financiacionAjena), Boolean.FALSE)));

    final TypedQuery<BigDecimal> typedQuery = entityManager.createQuery(cq);
    return typedQuery.getSingleResult();
  }

}
