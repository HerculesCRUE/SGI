package org.crue.hercules.sgi.pii.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.pii.model.InvencionAreaConocimiento;
import org.crue.hercules.sgi.pii.model.InvencionAreaConocimiento_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link InvencionAreaConocimiento}.
 */
@Slf4j
@Component
public class CustomInvencionAreaConocimientoRepositoryImpl implements CustomInvencionAreaConocimientoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByInvencionId(long invencionId) {
    log.debug("deleteInBulkByInvencionId(long invencionId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<InvencionAreaConocimiento> query = cb.createCriteriaDelete(InvencionAreaConocimiento.class);

    // Define FROM InvencionAreaConocimiento clause
    Root<InvencionAreaConocimiento> root = query.from(InvencionAreaConocimiento.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(InvencionAreaConocimiento_.INVENCION_ID), invencionId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByInvencionId(long invencionId) - end");
    return returnValue;
  }
}
