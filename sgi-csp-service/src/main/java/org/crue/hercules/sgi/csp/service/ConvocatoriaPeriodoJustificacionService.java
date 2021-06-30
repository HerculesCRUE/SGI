package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConvocatoriaPeriodoJustificacion}.
 */
public interface ConvocatoriaPeriodoJustificacionService {

  /**
   * Actualiza el listado de {@link ConvocatoriaPeriodoJustificacion} de la
   * {@link Convocatoria} con el listado convocatoriaPeriodoJustificaciones
   * añadiendo, editando o eliminando los elementos segun proceda.
   *
   * @param convocatoriaId                     Id de la {@link Convocatoria}.
   * @param convocatoriaPeriodoJustificaciones lista con los nuevos
   *                                           {@link ConvocatoriaPeriodoJustificacion}
   *                                           a guardar.
   * @return la entidad {@link ConvocatoriaPeriodoJustificacion} persistida.
   */
  List<ConvocatoriaPeriodoJustificacion> updateConvocatoriaPeriodoJustificacionesConvocatoria(Long convocatoriaId,
      List<ConvocatoriaPeriodoJustificacion> convocatoriaPeriodoJustificaciones);

  /**
   * Obtiene {@link ConvocatoriaPeriodoJustificacion} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaPeriodoJustificacion}.
   * @return la entidad {@link ConvocatoriaPeriodoJustificacion}.
   */
  ConvocatoriaPeriodoJustificacion findById(Long id);

  /**
   * Obtiene las {@link ConvocatoriaPeriodoJustificacion} para una
   * {@link Convocatoria}.
   *
   * @param idConvocatoria el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaPeriodoJustificacion} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaPeriodoJustificacion> findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable);

}