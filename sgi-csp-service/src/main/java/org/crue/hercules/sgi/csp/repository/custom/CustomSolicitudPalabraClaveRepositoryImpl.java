package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.SolicitudPalabraClave;
import org.crue.hercules.sgi.csp.model.SolicitudPalabraClave_;
import org.crue.hercules.sgi.csp.repository.SolicitudPalabraClaveRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link SolicitudPalabraClaveRepository}.
 */
@Slf4j
@Component
public class CustomSolicitudPalabraClaveRepositoryImpl implements CustomSolicitudPalabraClaveRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkBySolicitudId(long solicitudId) {
    log.debug("deleteInBulkBySolicitudId(long solicitudId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<SolicitudPalabraClave> query = cb.createCriteriaDelete(SolicitudPalabraClave.class);

    // Define FROM SolicitudPalabraClave clause
    Root<SolicitudPalabraClave> root = query.from(SolicitudPalabraClave.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(SolicitudPalabraClave_.solicitudId), solicitudId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkBySolicitudId(long solicitudId) - end");
    return returnValue;
  }
}
