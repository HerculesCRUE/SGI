package org.crue.hercules.sgi.pii.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.pii.model.InvencionPalabraClave;
import org.crue.hercules.sgi.pii.model.InvencionPalabraClave_;
import org.crue.hercules.sgi.pii.repository.InvencionPalabraClaveRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link InvencionPalabraClaveRepository}.
 */
@Slf4j
@Component
public class CustomInvencionPalabraClaveRepositoryImpl implements CustomInvencionPalabraClaveRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByInvencionId(long invencionId) {
    log.debug("deleteInBulkByInvencionId(long invencionId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<InvencionPalabraClave> query = cb.createCriteriaDelete(InvencionPalabraClave.class);

    // Define FROM InvencionPalabraClave clause
    Root<InvencionPalabraClave> root = query.from(InvencionPalabraClave.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(InvencionPalabraClave_.invencionId), invencionId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByInvencionId(long invencionId) - end");
    return returnValue;
  }
}
