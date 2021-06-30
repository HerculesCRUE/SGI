package org.crue.hercules.sgi.csp.repository.custom;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

/**
 * Spring Data JPA repository para {@link SolicitudProyectoSocio}.
 */
@Slf4j
@Component
public class CustomSolicitudProyectoSocioRepositoryImpl implements CustomSolicitudProyectoSocioRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Indica si {@link SolicitudProyectoSocio} tiene
   * {@link SolicitudProyectoSocioPeriodoJustificacion},
   * {@link SolicitudProyectoSocioPeriodoPago} y/o
   * {@link SolicitudProyectoSocioEquipo} relacionadas.
   *
   * @param id Id de la {@link SolicitudProyectoSocio}.
   * @return True si tiene {@link SolicitudProyectoSocioPeriodoJustificacion},
   *         {@link SolicitudProyectoSocioPeriodoPago} y/o
   *         {@link SolicitudProyectoSocioEquipo} relacionadas. En caso contrario
   *         false
   */
  @Override
  public Boolean vinculaciones(Long id) {
    log.debug("vinculaciones(Long id) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<SolicitudProyectoSocio> root = cq.from(SolicitudProyectoSocio.class);

    Subquery<Long> queryJustificacion = cq.subquery(Long.class);
    Root<SolicitudProyectoSocioPeriodoJustificacion> rootJustificacion = queryJustificacion
        .from(SolicitudProyectoSocioPeriodoJustificacion.class);
    Path<Long> pathJustificacion = rootJustificacion
        .get(SolicitudProyectoSocioPeriodoJustificacion_.solicitudProyectoSocio).get(SolicitudProyectoSocio_.id);
    Predicate existsQueryJustificacion = cb.exists(queryJustificacion.select(pathJustificacion)
        .where(cb.equal(pathJustificacion, root.get(SolicitudProyectoSocio_.id))));

    Subquery<Long> queryPago = cq.subquery(Long.class);
    Root<SolicitudProyectoSocioPeriodoPago> rootPago = queryPago.from(SolicitudProyectoSocioPeriodoPago.class);
    Path<Long> pathPago = rootPago.get(SolicitudProyectoSocioPeriodoPago_.solicitudProyectoSocio)
        .get(SolicitudProyectoSocio_.id);
    Predicate existsQueryPago = cb
        .exists(queryPago.select(pathPago).where(cb.equal(pathPago, root.get(SolicitudProyectoSocio_.id))));

    Subquery<Long> queryEquipo = cq.subquery(Long.class);
    Root<SolicitudProyectoSocioEquipo> rootEquipo = queryEquipo.from(SolicitudProyectoSocioEquipo.class);
    Path<Long> pathEquipo = rootEquipo.get(SolicitudProyectoSocioEquipo_.solicitudProyectoSocio)
        .get(SolicitudProyectoSocio_.id);
    Predicate existsQueryEquipo = cb
        .exists(queryEquipo.select(pathEquipo).where(cb.equal(pathEquipo, root.get(SolicitudProyectoSocio_.id))));

    Predicate solicitudProyectoSocio = cb.equal(root.get(SolicitudProyectoSocio_.id), id);
    Predicate vinculaciones = cb.or(existsQueryEquipo, existsQueryPago, existsQueryJustificacion);
    Predicate finalPredicate = cb.and(solicitudProyectoSocio, vinculaciones);
    cq.select(root.get(SolicitudProyectoSocio_.id)).where(finalPredicate);

    Boolean returnValue = entityManager.createQuery(cq).getResultList().size() > 0;

    log.debug("vinculaciones(Long id) - end");
    return returnValue;
  }

}
