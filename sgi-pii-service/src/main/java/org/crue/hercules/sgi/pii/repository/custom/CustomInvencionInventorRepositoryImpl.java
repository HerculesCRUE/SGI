package org.crue.hercules.sgi.pii.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.pii.model.InvencionInventor;
import org.crue.hercules.sgi.pii.model.InvencionInventor_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link InvencionInventor}.
 */
@Slf4j
@Component
public class CustomInvencionInventorRepositoryImpl implements CustomInvencionInventorRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Boolean inventoresBelongsToInvencion(Long invencionId, List<Long> invencionInventores) {
    log.debug("allItemsBelongsToInvencion(Long invencionId, List<Long> invencionInventores) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<InvencionInventor> query = cb.createQuery(InvencionInventor.class);
    Root<InvencionInventor> root = query.from(InvencionInventor.class);
    Predicate selectByInvencion = cb.equal(root.get(InvencionInventor_.INVENCION_ID), invencionId);
    Predicate listedInvencionInventor = root.get(InvencionInventor_.ID).in(invencionInventores);
    query.where(cb.and(selectByInvencion, listedInvencionInventor));
    Integer cant = entityManager.createQuery(query).getResultList().size();

    log.debug("allItemsBelongsToInvencion(Long invencionId, List<Long> invencionInventores) - end");
    return cant == invencionInventores.size();
  }

}
