package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link RequisitoIPNivelAcademico}.
 */
@Slf4j
@Component
public class CustomRequisitoIPNivelAcademicoRepositoryImpl implements CustomRequisitoIPNivelAcademicoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Elimina todos los RequisitoIPNivelAcademico cuyo requisitoIPId coincide con
   * el indicado.
   * 
   * @param requisitoIPId el identificador del RequisitoIP cuyos niveles se desean
   *                      eliminar
   * @return el número de registros eliminados
   */
  @Override
  public int deleteInBulkByRequisitoIPId(long requisitoIPId) {
    log.debug("deleteInBulkByRequisitoIPId(long requisitoIPId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<RequisitoIPNivelAcademico> query = cb.createCriteriaDelete(RequisitoIPNivelAcademico.class);

    // Define FROM RequisitoIPNivelAcademico clause
    Root<RequisitoIPNivelAcademico> root = query.from(RequisitoIPNivelAcademico.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(RequisitoIPNivelAcademico_.REQUISITO_IP_ID), requisitoIPId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByRequisitoIPId(long requisitoIPId) - end");
    return returnValue;
  }
}
