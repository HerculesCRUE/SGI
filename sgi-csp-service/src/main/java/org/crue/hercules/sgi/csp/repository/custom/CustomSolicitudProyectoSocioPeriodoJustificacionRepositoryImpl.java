package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link ConvocatoriaPeriodoJustificacion}.
 */
@Slf4j
@Component
public class CustomSolicitudProyectoSocioPeriodoJustificacionRepositoryImpl
    implements CustomSolicitudProyectoSocioPeriodoJustificacionRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Elimina todas los SolicitudProyectoSocioPeriodoJustificacion cuyo
   * solicitudProyectoSocioId coincide con el indicado.
   * 
   * @param solicitudProyectoSocioId el identificador de la Convocatoria cuyos
   *                                 periodos de justificación se desean eliminar
   * @return el número de registros eliminados
   */
  @Override
  public int deleteInBulkBySolicitudProyectoSocioId(long solicitudProyectoSocioId) {
    log.debug("deleteInBulkBySolicitudProyectoSocioId(long solicitudProyectoSocioId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<SolicitudProyectoSocioPeriodoJustificacion> query = cb
        .createCriteriaDelete(SolicitudProyectoSocioPeriodoJustificacion.class);

    // Define FROM SolicitudProyectoSocioPeriodoJustificacion clause
    Root<SolicitudProyectoSocioPeriodoJustificacion> root = query
        .from(SolicitudProyectoSocioPeriodoJustificacion.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(SolicitudProyectoSocioPeriodoJustificacion_.SOLICITUD_PROYECTO_SOCIO_ID),
        solicitudProyectoSocioId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkBySolicitudProyectoSocioId(long solicitudProyectoSocioId) - end");
    return returnValue;
  }
}
