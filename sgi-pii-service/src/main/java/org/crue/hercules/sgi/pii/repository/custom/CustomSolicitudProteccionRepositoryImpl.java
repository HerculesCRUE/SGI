package org.crue.hercules.sgi.pii.repository.custom;

import java.time.Instant;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.crue.hercules.sgi.pii.dto.InvencionDto.SolicitudProteccionDto;
import org.crue.hercules.sgi.pii.dto.SolicitudProteccionOutput.Invencion;
import org.crue.hercules.sgi.pii.model.Invencion_;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion_;
import org.crue.hercules.sgi.pii.model.ViaProteccion_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link SolicitudProteccion}.
 */
@Slf4j
@Component
public class CustomSolicitudProteccionRepositoryImpl implements CustomSolicitudProteccionRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Devuelve una lista de {@link SolicitudProteccionDto} que están en el rango de
   * baremación
   * 
   * @param invencionId           id de {@link Invencion}
   * @param fechaInicioBaremacion fecha inicio de baremación
   * @param fechaFinBaremacion    fecha fin de baremación
   * 
   * @return Lista de {@link SolicitudProteccionDto}
   */
  @Override
  public List<SolicitudProteccionDto> findSolicitudProteccionInRangoBaremacion(Long invencionId,
      Instant fechaInicioBaremacion,
      Instant fechaFinBaremacion) {
    log.debug("findSolicitudProteccionInRangoBaremacion(fechaInicioBaremacion, fechaFinBaremacion) : {} - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<SolicitudProteccionDto> cq = cb.createQuery(SolicitudProteccionDto.class);

    Root<SolicitudProteccion> root = cq.from(SolicitudProteccion.class);

    cq.multiselect(
        root.get(SolicitudProteccion_.id),
        root.get(SolicitudProteccion_.fechaConcesion),
        root.get(SolicitudProteccion_.viaProteccion).get(ViaProteccion_.id));

    Predicate predicateInvencion = cb
        .equal(root.get(SolicitudProteccion_.invencion).get(Invencion_.id), invencionId);

    Predicate predicateIsActiva = cb.equal(root.get(Activable_.activo), Boolean.TRUE);

    Path<Instant> fechaConcesion = root.get(SolicitudProteccion_.fechaConcesion);
    Predicate predicateInFechaBaremacion = cb.and(cb.lessThanOrEqualTo(fechaConcesion, fechaFinBaremacion),
        cb.greaterThanOrEqualTo(fechaConcesion, fechaInicioBaremacion));

    cq.where(cb.and(predicateIsActiva, predicateInvencion, predicateInFechaBaremacion));

    cq.orderBy(cb.asc(fechaConcesion));

    TypedQuery<SolicitudProteccionDto> typedQuery = entityManager.createQuery(cq);
    List<SolicitudProteccionDto> result = typedQuery.getResultList();

    log.debug("findSolicitudProteccionInRangoBaremacion(fechaInicioBaremacion, fechaFinBaremacion) : {} - end");

    return result;
  }

}
