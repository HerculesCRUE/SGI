package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.ProyectoPalabraClave;
import org.crue.hercules.sgi.csp.model.ProyectoPalabraClave_;
import org.crue.hercules.sgi.csp.repository.ProyectoPalabraClaveRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link ProyectoPalabraClaveRepository}.
 */
@Slf4j
@Component
public class CustomProyectoPalabraClaveRepositoryImpl implements CustomProyectoPalabraClaveRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByProyectoId(long proyectoId) {
    log.debug("deleteInBulkByProyectoId(long proyectoId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<ProyectoPalabraClave> query = cb.createCriteriaDelete(ProyectoPalabraClave.class);

    // Define FROM ProyectoPalabraClave clause
    Root<ProyectoPalabraClave> root = query.from(ProyectoPalabraClave.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(ProyectoPalabraClave_.proyectoId), proyectoId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByProyectoId(long proyectoId) - end");
    return returnValue;
  }
}
