package org.crue.hercules.sgi.prc.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica_;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link CampoProduccionCientificaRepository}.
 */
@Slf4j
@Component
public class CustomCampoProduccionCientificaRepositoryImpl implements CustomCampoProduccionCientificaRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByProduccionCientificaId(long produccionCientificaId) {
    log.debug("deleteInBulkByProduccionCientificaId(long produccionCientificaId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<CampoProduccionCientifica> query = cb.createCriteriaDelete(CampoProduccionCientifica.class);

    // Define FROM CampoProduccionCientifica clause
    Root<CampoProduccionCientifica> root = query.from(CampoProduccionCientifica.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(CampoProduccionCientifica_.produccionCientificaId), produccionCientificaId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByProduccionCientificaId(long produccionCientificaId) - end");
    return returnValue;
  }
}
