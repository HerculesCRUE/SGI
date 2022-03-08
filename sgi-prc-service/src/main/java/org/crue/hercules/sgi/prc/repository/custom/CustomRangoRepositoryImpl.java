package org.crue.hercules.sgi.prc.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.model.Rango;
import org.crue.hercules.sgi.prc.model.Rango_;
import org.crue.hercules.sgi.prc.repository.RangoRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link RangoRepository}.
 */
@Slf4j
@Component
public class CustomRangoRepositoryImpl implements CustomRangoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByConvocatoriaBaremacionId(Long convocatoriaBaremacionId) {
    log.debug("deleteInBulkByConvocatoriaBaremacionId(long convocatoriaBaremacionId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<Rango> query = cb.createCriteriaDelete(Rango.class);

    // Define FROM Rango clause
    Root<Rango> root = query.from(Rango.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(Rango_.convocatoriaBaremacionId), convocatoriaBaremacionId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByConvocatoriaBaremacionId(long convocatoriaBaremacionId) - end");
    return returnValue;
  }
}
