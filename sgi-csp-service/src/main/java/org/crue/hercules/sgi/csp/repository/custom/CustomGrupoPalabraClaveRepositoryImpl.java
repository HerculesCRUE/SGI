package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoPalabraClave;
import org.crue.hercules.sgi.csp.model.GrupoPalabraClave_;
import org.crue.hercules.sgi.csp.repository.GrupoPalabraClaveRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link GrupoPalabraClaveRepository}.
 */
@Slf4j
@Component
public class CustomGrupoPalabraClaveRepositoryImpl implements CustomGrupoPalabraClaveRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Elimina todos los {@link GrupoPalabraClave} cuyo grupoId
   * coincide con el indicado.
   * 
   * @param grupoId el identificador del {@link Grupo} cuyas palabras claves se
   *                desean eliminar
   * @return el número de registros eliminados
   */
  @Override
  public int deleteInBulkByGrupoId(long grupoId) {
    log.debug("deleteInBulkByGrupoId(long grupoId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<GrupoPalabraClave> query = cb.createCriteriaDelete(GrupoPalabraClave.class);

    // Define FROM GrupoPalabraClave clause
    Root<GrupoPalabraClave> root = query.from(GrupoPalabraClave.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(GrupoPalabraClave_.grupoId), grupoId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByGrupoId(long grupoId) - end");
    return returnValue;
  }

}
