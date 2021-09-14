package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.RequisitoEquipoCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoCategoriaProfesional_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link RequisitoEquipoCategoriaProfesional}.
 */
@Slf4j
@Component
public class CustomRequisitoEquipoCategoriaProfesionalRepositoryImpl
    implements CustomRequisitoEquipoCategoriaProfesionalRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Elimina todos las RequisitoEquipoCategoriaProfesional cuyo RequisitoEquipoId
   * coincide con el indicado.
   * 
   * @param RequisitoEquipoId el identificador del RequisitoEquipo cuyas
   *                          categorías se desean eliminar
   * @return el número de registros eliminados
   */
  @Override
  public int deleteInBulkByRequisitoEquipoId(long RequisitoEquipoId) {
    log.debug("deleteInBulkByRequisitoEquipoId(long RequisitoEquipoId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<RequisitoEquipoCategoriaProfesional> query = cb
        .createCriteriaDelete(RequisitoEquipoCategoriaProfesional.class);

    // Define FROM RequisitoEquipoCategoriaProfesional clause
    Root<RequisitoEquipoCategoriaProfesional> root = query.from(RequisitoEquipoCategoriaProfesional.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(RequisitoEquipoCategoriaProfesional_.REQUISITO_EQUIPO_ID), RequisitoEquipoId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByRequisitoEquipoId(long RequisitoEquipoId) - end");
    return returnValue;
  }
}
