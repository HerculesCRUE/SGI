package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.AlegacionRequerimiento;
import org.crue.hercules.sgi.csp.model.AlegacionRequerimiento_;
import org.crue.hercules.sgi.csp.repository.AlegacionRequerimientoRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para
 * {@link AlegacionRequerimientoRepository}.
 */
@Slf4j
@Component
public class CustomAlegacionRequerimientoRepositoryImpl implements CustomAlegacionRequerimientoRepository {
  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByRequerimientoJustificacionId(long requerimientoJustificacionId) {
    log.debug("deleteInBulkByRequerimientoJustificacionId(long requerimientoJustificacionId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<AlegacionRequerimiento> query = cb
        .createCriteriaDelete(AlegacionRequerimiento.class);

    // Define FROM AlegacionRequerimiento clause
    Root<AlegacionRequerimiento> root = query.from(AlegacionRequerimiento.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(AlegacionRequerimiento_.requerimientoJustificacionId),
        requerimientoJustificacionId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByRequerimientoJustificacionId(long requerimientoJustificacionId) - end");
    return returnValue;
  }
}
