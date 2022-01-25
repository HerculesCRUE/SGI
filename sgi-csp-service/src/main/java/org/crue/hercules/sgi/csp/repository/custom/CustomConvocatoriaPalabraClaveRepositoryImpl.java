package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.ConvocatoriaPalabraClave;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPalabraClave_;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPalabraClaveRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link ConvocatoriaPalabraClaveRepository}.
 */
@Slf4j
@Component
public class CustomConvocatoriaPalabraClaveRepositoryImpl implements CustomConvocatoriaPalabraClaveRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByConvocatoriaId(long convocatoriaId) {
    log.debug("deleteInBulkByConvocatoriaId(long convocatoriaId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<ConvocatoriaPalabraClave> query = cb.createCriteriaDelete(ConvocatoriaPalabraClave.class);

    // Define FROM ConvocatoriaPalabraClave clause
    Root<ConvocatoriaPalabraClave> root = query.from(ConvocatoriaPalabraClave.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(ConvocatoriaPalabraClave_.convocatoriaId), convocatoriaId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByConvocatoriaId(long convocatoriaId) - end");
    return returnValue;
  }
}
