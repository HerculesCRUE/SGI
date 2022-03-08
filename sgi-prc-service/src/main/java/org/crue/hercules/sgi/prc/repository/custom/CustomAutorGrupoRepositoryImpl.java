package org.crue.hercules.sgi.prc.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.model.AutorGrupo_;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link AutorGrupoRepository}.
 */
@Slf4j
@Component
public class CustomAutorGrupoRepositoryImpl implements CustomAutorGrupoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByAutorId(long autorId) {
    log.debug("deleteInBulkByAutorId(long autorId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<AutorGrupo> query = cb.createCriteriaDelete(AutorGrupo.class);

    // Define FROM AutorGrupo clause
    Root<AutorGrupo> root = query.from(AutorGrupo.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(AutorGrupo_.autorId), autorId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByAutorId(long autorId) - end");
    return returnValue;
  }
}
