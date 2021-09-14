package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link RequisitoIPCategoriaProfesional}.
 */
@Slf4j
@Component
public class CustomRequisitoIPCategoriaProfesionalRepositoryImpl
    implements CustomRequisitoIPCategoriaProfesionalRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Elimina todos las RequisitoIPCategoriaProfesional cuyo requisitoIPId coincide
   * con el indicado.
   * 
   * @param requisitoIPId el identificador del RequisitoIP cuyas categorías se
   *                      desean eliminar
   * @return el número de registros eliminados
   */
  @Override
  public int deleteInBulkByRequisitoIPId(long requisitoIPId) {
    log.debug("deleteInBulkByRequisitoIPId(long requisitoIPId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<RequisitoIPCategoriaProfesional> query = cb
        .createCriteriaDelete(RequisitoIPCategoriaProfesional.class);

    // Define FROM RequisitoIPCategoriaProfesional clause
    Root<RequisitoIPCategoriaProfesional> root = query.from(RequisitoIPCategoriaProfesional.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(RequisitoIPCategoriaProfesional_.REQUISITO_IP_ID), requisitoIPId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByRequisitoIPId(long requisitoIPId) - end");
    return returnValue;
  }
}
