package org.crue.hercules.sgi.prc.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.model.PuntuacionGrupo;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupo_;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link PuntuacionGrupoRepository}.
 */
@Slf4j
@Component
public class CustomPuntuacionGrupoRepositoryImpl implements CustomPuntuacionGrupoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByConvocatoriaBaremacionId(Long convocatoriaBaremacionId) {
    log.debug("deleteInBulkByConvocatoriaBaremacionId(long convocatoriaBaremacionId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<PuntuacionGrupo> query = cb.createCriteriaDelete(PuntuacionGrupo.class);

    // Define FROM PuntuacionGrupo clause
    Root<PuntuacionGrupo> root = query.from(PuntuacionGrupo.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(PuntuacionGrupo_.convocatoriaBaremacionId), convocatoriaBaremacionId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByConvocatoriaBaremacionId(long convocatoriaBaremacionId) - end");
    return returnValue;
  }
}
