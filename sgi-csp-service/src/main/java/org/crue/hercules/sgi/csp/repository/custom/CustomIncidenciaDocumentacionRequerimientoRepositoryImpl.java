package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.IncidenciaDocumentacionRequerimiento;
import org.crue.hercules.sgi.csp.model.IncidenciaDocumentacionRequerimiento_;
import org.crue.hercules.sgi.csp.repository.IncidenciaDocumentacionRequerimientoRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para
 * {@link IncidenciaDocumentacionRequerimientoRepository}.
 */
@Slf4j
@Component
public class CustomIncidenciaDocumentacionRequerimientoRepositoryImpl
    implements CustomIncidenciaDocumentacionRequerimientoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByRequerimientoJustificacionId(long requerimientoJustificacionId) {
    log.debug("deleteInBulkByRequerimientoJustificacionId(long requerimientoJustificacionId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<IncidenciaDocumentacionRequerimiento> query = cb
        .createCriteriaDelete(IncidenciaDocumentacionRequerimiento.class);

    // Define FROM IncidenciaDocumentacionRequerimiento clause
    Root<IncidenciaDocumentacionRequerimiento> root = query.from(IncidenciaDocumentacionRequerimiento.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(IncidenciaDocumentacionRequerimiento_.requerimientoJustificacionId),
        requerimientoJustificacionId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByRequerimientoJustificacionId(long requerimientoJustificacionId) - end");
    return returnValue;
  }
}
