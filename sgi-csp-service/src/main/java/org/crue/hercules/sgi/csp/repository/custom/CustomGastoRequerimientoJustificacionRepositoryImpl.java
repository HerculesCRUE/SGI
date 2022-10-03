package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.GastoRequerimientoJustificacion;
import org.crue.hercules.sgi.csp.model.GastoRequerimientoJustificacion_;
import org.crue.hercules.sgi.csp.repository.GastoRequerimientoJustificacionRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para
 * {@link GastoRequerimientoJustificacionRepository}.
 */
@Slf4j
@Component
public class CustomGastoRequerimientoJustificacionRepositoryImpl
    implements CustomGastoRequerimientoJustificacionRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByRequerimientoJustificacionId(long requerimientoJustificacionId) {
    log.debug("deleteInBulkByRequerimientoJustificacionId(long requerimientoJustificacionId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<GastoRequerimientoJustificacion> query = cb
        .createCriteriaDelete(GastoRequerimientoJustificacion.class);

    // Define FROM GastoRequerimientoJustificacion clause
    Root<GastoRequerimientoJustificacion> root = query.from(GastoRequerimientoJustificacion.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(GastoRequerimientoJustificacion_.requerimientoJustificacionId),
        requerimientoJustificacionId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByRequerimientoJustificacionId(long requerimientoJustificacionId) - end");
    return returnValue;
  }
}
