package org.crue.hercules.sgi.prc.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;
import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem_;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link PuntuacionBaremoItemRepository}.
 */
@Slf4j
@Component
public class CustomPuntuacionBaremoItemRepositoryImpl implements CustomPuntuacionBaremoItemRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByProduccionCientificaId(long produccionCientificaId) {
    log.debug("deleteInBulkByProduccionCientificaId(long produccionCientificaId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<PuntuacionBaremoItem> query = cb.createCriteriaDelete(PuntuacionBaremoItem.class);

    // Define FROM PuntuacionBaremoItem clause
    Root<PuntuacionBaremoItem> root = query.from(PuntuacionBaremoItem.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(PuntuacionBaremoItem_.produccionCientificaId), produccionCientificaId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByProduccionCientificaId(long produccionCientificaId) - end");
    return returnValue;
  }

  @Override
  public int deleteInBulkByProduccionCientificaIdAndBaremoId(Long produccionCientificaId, Long baremoId) {
    log.debug("deleteInBulkByProduccionCientificaIdAndBaremoId(produccionCientificaId, baremoId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<PuntuacionBaremoItem> query = cb.createCriteriaDelete(PuntuacionBaremoItem.class);

    // Define FROM PuntuacionBaremoItem clause
    Root<PuntuacionBaremoItem> root = query.from(PuntuacionBaremoItem.class);

    // Set WHERE restrictions
    query.where(cb.and(cb.equal(root.get(PuntuacionBaremoItem_.produccionCientificaId), produccionCientificaId),
        cb.equal(root.get(PuntuacionBaremoItem_.baremoId), baremoId)));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByProduccionCientificaIdAndBaremoId(produccionCientificaId, baremoId) - end");
    return returnValue;
  }
}
