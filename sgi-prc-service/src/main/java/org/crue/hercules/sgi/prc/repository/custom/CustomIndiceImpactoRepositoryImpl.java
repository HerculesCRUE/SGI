package org.crue.hercules.sgi.prc.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.IndiceImpacto_;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link IndiceImpactoRepository}.
 */
@Slf4j
@Component
public class CustomIndiceImpactoRepositoryImpl implements CustomIndiceImpactoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByProduccionCientificaId(long produccionCientificaId) {
    log.debug("deleteInBulkByProduccionCientificaId(long produccionCientificaId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<IndiceImpacto> query = cb.createCriteriaDelete(IndiceImpacto.class);

    // Define FROM IndiceImpacto clause
    Root<IndiceImpacto> root = query.from(IndiceImpacto.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(IndiceImpacto_.produccionCientificaId), produccionCientificaId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByProduccionCientificaId(long produccionCientificaId) - end");
    return returnValue;
  }
}
