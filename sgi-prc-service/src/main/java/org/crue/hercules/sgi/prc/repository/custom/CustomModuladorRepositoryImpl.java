package org.crue.hercules.sgi.prc.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.model.Modulador;
import org.crue.hercules.sgi.prc.model.Modulador_;
import org.crue.hercules.sgi.prc.repository.ModuladorRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link ModuladorRepository}.
 */
@Slf4j
@Component
public class CustomModuladorRepositoryImpl implements CustomModuladorRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByConvocatoriaBaremacionId(Long convocatoriaBaremacionId) {
    log.debug("deleteInBulkByConvocatoriaBaremacionId(long convocatoriaBaremacionId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<Modulador> query = cb.createCriteriaDelete(Modulador.class);

    // Define FROM Modulador clause
    Root<Modulador> root = query.from(Modulador.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(Modulador_.convocatoriaBaremacionId), convocatoriaBaremacionId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByConvocatoriaBaremacionId(long convocatoriaBaremacionId) - end");
    return returnValue;
  }
}
