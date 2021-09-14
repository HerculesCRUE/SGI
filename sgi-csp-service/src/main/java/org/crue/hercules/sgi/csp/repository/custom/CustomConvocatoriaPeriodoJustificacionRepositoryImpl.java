package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link ConvocatoriaPeriodoJustificacion}.
 */
@Slf4j
@Component
public class CustomConvocatoriaPeriodoJustificacionRepositoryImpl
    implements CustomConvocatoriaPeriodoJustificacionRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Elimina todas los ConvocatoriaPeriodoJustificacion cuyo convocatoriaId
   * coincide con el indicado.
   * 
   * @param convocatoriaId el identificador de la Convocatoria cuyos periodos de
   *                       justificación se desean eliminar
   * @return el número de registros eliminados
   */
  @Override
  public int deleteInBulkByConvocatoriaId(long convocatoriaId) {
    log.debug("deleteInBulkByConvocatoriaId(long convocatoriaId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<ConvocatoriaPeriodoJustificacion> query = cb
        .createCriteriaDelete(ConvocatoriaPeriodoJustificacion.class);

    // Define FROM ConvocatoriaPeriodoJustificacion clause
    Root<ConvocatoriaPeriodoJustificacion> root = query.from(ConvocatoriaPeriodoJustificacion.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(ConvocatoriaPeriodoJustificacion_.CONVOCATORIA_ID), convocatoriaId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByConvocatoriaId(long convocatoriaId) - end");
    return returnValue;
  }
}
