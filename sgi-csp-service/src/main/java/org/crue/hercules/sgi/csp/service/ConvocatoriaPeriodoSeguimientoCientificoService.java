package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar
 * {@link ConvocatoriaPeriodoSeguimientoCientifico}.
 */

public interface ConvocatoriaPeriodoSeguimientoCientificoService {

  /**
   * Actualiza el listado de {@link ConvocatoriaPeriodoSeguimientoCientifico} de
   * la {@link Convocatoria} con el listado
   * convocatoriaPeriodoSeguimientoCientificos añadiendo, editando o eliminando
   * los elementos segun proceda.
   *
   * @param convocatoriaId                            Id de la
   *                                                  {@link Convocatoria}.
   * @param convocatoriaPeriodoSeguimientoCientificos lista con los nuevos
   *                                                  {@link ConvocatoriaPeriodoSeguimientoCientifico}
   *                                                  a guardar.
   * @return la entidad {@link ConvocatoriaPeriodoSeguimientoCientifico}
   *         persistida.
   */
  List<ConvocatoriaPeriodoSeguimientoCientifico> updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(
      Long convocatoriaId, List<ConvocatoriaPeriodoSeguimientoCientifico> convocatoriaPeriodoSeguimientoCientificos);

  /**
   * Obtiene una entidad {@link ConvocatoriaPeriodoSeguimientoCientifico} por id.
   * 
   * @param id Identificador de la entidad
   *           {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   * @return ConvocatoriaPeriodoSeguimientoCientifico la entidad
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   */
  ConvocatoriaPeriodoSeguimientoCientifico findById(final Long id);

  /**
   * Obtiene todas las entidades {@link ConvocatoriaPeriodoSeguimientoCientifico}
   * para una {@link Convocatoria}.
   * 
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          información del filtro.
   * @param paging         información de paginación.
   * @return el listado de entidades
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico} paginadas y
   *         filtradas.
   */
  Page<ConvocatoriaPeriodoSeguimientoCientifico> findAllByConvocatoria(Long convocatoriaId, String query,
      Pageable paging);
}
