package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para
 * {@link ConvocatoriaPeriodoSeguimientoCientifico}.
 */
@Slf4j
@Component
public class CustomConvocatoriaPeriodoSeguimientoCientificoRepositoryImpl
    implements CustomConvocatoriaPeriodoSeguimientoCientificoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Elimina todas los ConvocatoriaPeriodoSeguimientoCientifico cuyo
   * convocatoriaId coincide con el indicado.
   * 
   * @param convocatoriaId el identificador de la Convocatoria cuyos periodos de
   *                       seguimiento científico se desean eliminar
   * @return el número de registros eliminados
   */
  @Override
  public int deleteInBulkByConvocatoriaId(long convocatoriaId) {
    log.debug("deleteInBulkByConvocatoriaId(long convocatoriaId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<ConvocatoriaPeriodoSeguimientoCientifico> query = cb
        .createCriteriaDelete(ConvocatoriaPeriodoSeguimientoCientifico.class);

    // Define FROM ConvocatoriaPeriodoJustificacion clause
    Root<ConvocatoriaPeriodoSeguimientoCientifico> root = query.from(ConvocatoriaPeriodoSeguimientoCientifico.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(ConvocatoriaPeriodoSeguimientoCientifico_.CONVOCATORIA_ID), convocatoriaId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByConvocatoriaId(long convocatoriaId) - end");
    return returnValue;
  }
}
