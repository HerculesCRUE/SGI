package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.RequisitoEquipoNivelAcademico;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoNivelAcademico_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link RequisitoEquipoNivelAcademico}.
 */
@Slf4j
@Component
public class CustomRequisitoEquipoNivelAcademicoRepositoryImpl
    implements CustomRequisitoEquipoNivelAcademicoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Elimina todos los RequisitoEquipoNivelAcademico cuyo requisitoEquipoId
   * coincide con el indicado.
   * 
   * @param requisitoEquipoId el identificador del RequisitoEquipo cuyos niveles
   *                          se desean eliminar
   * @return el número de registros eliminados
   */
  @Override
  public int deleteInBulkByRequisitoEquipoId(long requisitoEquipoId) {
    log.debug("deleteInBulkByRequisitoEquipoId(long requisitoEquipoId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<RequisitoEquipoNivelAcademico> query = cb.createCriteriaDelete(RequisitoEquipoNivelAcademico.class);

    // Define FROM RequisitoEquipoNivelAcademico clause
    Root<RequisitoEquipoNivelAcademico> root = query.from(RequisitoEquipoNivelAcademico.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(RequisitoEquipoNivelAcademico_.REQUISITO_EQUIPO_ID), requisitoEquipoId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByRequisitoEquipoId(long requisitoEquipoId) - end");
    return returnValue;
  }
}
