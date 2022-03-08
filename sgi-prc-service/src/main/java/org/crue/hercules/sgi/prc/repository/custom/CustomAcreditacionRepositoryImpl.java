package org.crue.hercules.sgi.prc.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.model.Acreditacion;
import org.crue.hercules.sgi.prc.model.Acreditacion_;
import org.crue.hercules.sgi.prc.repository.AcreditacionRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link AcreditacionRepository}.
 */
@Slf4j
@Component
public class CustomAcreditacionRepositoryImpl implements CustomAcreditacionRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByProduccionCientificaId(long produccionCientificaId) {
    log.debug("deleteInBulkByProduccionCientificaId(long produccionCientificaId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<Acreditacion> query = cb.createCriteriaDelete(Acreditacion.class);

    // Define FROM Acreditacion clause
    Root<Acreditacion> root = query.from(Acreditacion.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(Acreditacion_.produccionCientificaId), produccionCientificaId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByProduccionCientificaId(long produccionCientificaId) - end");
    return returnValue;
  }
}
