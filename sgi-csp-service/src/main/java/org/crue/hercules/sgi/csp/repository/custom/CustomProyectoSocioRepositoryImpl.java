package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo_;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion_;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago_;
import org.crue.hercules.sgi.csp.model.ProyectoSocio_;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link Proyecto}.
 */
@Slf4j
@Component
public class CustomProyectoSocioRepositoryImpl implements CustomProyectoSocioRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Indica si {@link ProyectoSocio} tiene {@link ProyectoSocioEquipo},
   * {@link ProyectoSocioPeriodoPago},
   * {@link ProyectoSocioPeriodoJustificacionDocumento} y/o
   * {@link ProyectoSocioPeriodoJustificacion} relacionadas.
   *
   * @param id Id de la {@link ProyectoSocio}.
   * @return True si tiene {@link ProyectoSocioEquipo},
   *         {@link ProyectoSocioPeriodoPago},
   *         {@link ProyectoSocioPeriodoJustificacionDocumento} y/o
   *         {@link ProyectoSocioPeriodoJustificacion} relacionadas. En caso
   *         contrario false
   */
  @Override
  public Boolean vinculaciones(Long id) {
    log.debug("vinculaciones(Long id) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<ProyectoSocio> root = cq.from(ProyectoSocio.class);

    Subquery<Long> queryEquipo = cq.subquery(Long.class);
    Root<ProyectoSocioEquipo> equipoRoot = queryEquipo.from(ProyectoSocioEquipo.class);
    Path<Long> pathEquipo = equipoRoot.get(ProyectoSocioEquipo_.proyectoSocio).get(ProyectoSocio_.id);
    Predicate existsQueryEquipo = cb
        .exists(queryEquipo.select(pathEquipo).where(cb.equal(pathEquipo, root.get(ProyectoSocio_.id))));

    Subquery<Long> queryPeriodoPago = cq.subquery(Long.class);
    Root<ProyectoSocioPeriodoPago> periodoPagoRoot = queryPeriodoPago.from(ProyectoSocioPeriodoPago.class);
    Path<Long> pathPeriodoPago = periodoPagoRoot.get(ProyectoSocioPeriodoPago_.proyectoSocio).get(ProyectoSocio_.id);
    Predicate existsQueryPeriodoPago = cb
        .exists(queryPeriodoPago.select(pathPeriodoPago).where(cb.equal(pathPeriodoPago, root.get(ProyectoSocio_.id))));

    Subquery<Long> queryDocumento = cq.subquery(Long.class);
    Root<ProyectoSocioPeriodoJustificacionDocumento> documentoRoot = queryDocumento
        .from(ProyectoSocioPeriodoJustificacionDocumento.class);
    Path<Long> pathDocumento = documentoRoot
        .get(ProyectoSocioPeriodoJustificacionDocumento_.proyectoSocioPeriodoJustificacion)
        .get(ProyectoSocioPeriodoJustificacion_.proyectoSocio).get(ProyectoSocio_.id);
    Predicate existsQueryDocumento = cb
        .exists(queryDocumento.select(pathDocumento).where(cb.equal(pathDocumento, root.get(ProyectoSocio_.id))));

    Subquery<Long> queryJustificacion = cq.subquery(Long.class);
    Root<ProyectoSocioPeriodoJustificacion> justificacionRoot = queryJustificacion
        .from(ProyectoSocioPeriodoJustificacion.class);
    Path<Long> pathJustificacion = justificacionRoot.get(ProyectoSocioPeriodoJustificacion_.proyectoSocio)
        .get(ProyectoSocio_.id);
    Predicate existsQueryJustificacion = cb.exists(
        queryJustificacion.select(pathJustificacion).where(cb.equal(pathJustificacion, root.get(ProyectoSocio_.id))));

    Predicate proyectoSocio = cb.equal(root.get(ProyectoSocio_.id), id);
    Predicate vinculaciones = cb.or(existsQueryEquipo, existsQueryPeriodoPago, existsQueryDocumento,
        existsQueryJustificacion);
    Predicate finalPredicate = cb.and(proyectoSocio, vinculaciones);
    cq.select(root.get(ProyectoSocio_.id)).where(finalPredicate);

    Boolean returnValue = entityManager.createQuery(cq).getResultList().size() > 0;

    log.debug("vinculaciones(Long id) - start");
    return returnValue;
  }

}
