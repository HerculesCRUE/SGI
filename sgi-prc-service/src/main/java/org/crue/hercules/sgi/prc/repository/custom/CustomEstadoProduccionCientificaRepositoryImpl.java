package org.crue.hercules.sgi.prc.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica_;
import org.crue.hercules.sgi.prc.repository.EstadoProduccionCientificaRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link EstadoProduccionCientificaRepository}.
 */
@Slf4j
@Component
public class CustomEstadoProduccionCientificaRepositoryImpl implements CustomEstadoProduccionCientificaRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByProduccionCientificaId(long produccionCientificaId) {
    log.debug("deleteInBulkByProduccionCientificaId(long produccionCientificaId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<EstadoProduccionCientifica> query = cb.createCriteriaDelete(EstadoProduccionCientifica.class);

    // Define FROM EstadoProduccionCientifica clause
    Root<EstadoProduccionCientifica> root = query.from(EstadoProduccionCientifica.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(EstadoProduccionCientifica_.produccionCientificaId), produccionCientificaId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByProduccionCientificaId(long produccionCientificaId) - end");
    return returnValue;
  }
}
