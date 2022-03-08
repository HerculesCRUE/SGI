package org.crue.hercules.sgi.prc.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.Autor_;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link AutorRepository}.
 */
@Slf4j
@Component
public class CustomAutorRepositoryImpl implements CustomAutorRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByProduccionCientificaId(long produccionCientificaId) {
    log.debug("deleteInBulkByProduccionCientificaId(long produccionCientificaId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<Autor> query = cb.createCriteriaDelete(Autor.class);

    // Define FROM Autor clause
    Root<Autor> root = query.from(Autor.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(Autor_.produccionCientificaId), produccionCientificaId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByProduccionCientificaId(long produccionCientificaId) - end");
    return returnValue;
  }
}
