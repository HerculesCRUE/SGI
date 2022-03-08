package org.crue.hercules.sgi.prc.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.model.ValorCampo_;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link ValorCampoRepository}.
 */
@Slf4j
@Component
public class CustomValorCampoRepositoryImpl implements CustomValorCampoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByCampoProduccionCientificaId(long campoProduccionCientificaId) {
    log.debug("deleteInBulkByCampoProduccionCientificaId(long campoProduccionCientificaId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<ValorCampo> query = cb.createCriteriaDelete(ValorCampo.class);

    // Define FROM ValorCampo clause
    Root<ValorCampo> root = query.from(ValorCampo.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(ValorCampo_.campoProduccionCientificaId), campoProduccionCientificaId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByCampoProduccionCientificaId(long campoProduccionCientificaId) - end");
    return returnValue;
  }
}
