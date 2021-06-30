package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConvocatoriaEntidadGestora}.
 */

public interface ConvocatoriaEntidadGestoraService {

  /**
   * Guarda la entidad {@link ConvocatoriaEntidadGestora}.
   * 
   * @param convocatoriaEntidadGestora la entidad
   *                                   {@link ConvocatoriaEntidadGestora} a
   *                                   guardar.
   * @return ConvocatoriaEntidadGestora la entidad
   *         {@link ConvocatoriaEntidadGestora} persistida.
   */
  ConvocatoriaEntidadGestora create(ConvocatoriaEntidadGestora convocatoriaEntidadGestora);

  /**
   * Actualiza la entidad {@link ConvocatoriaEntidadGestora}.
   * 
   * @param convocatoriaEntidadGestora la entidad
   *                                   {@link ConvocatoriaEntidadGestora} a
   *                                   guardar.
   * @return ConvocatoriaEntidadGestora la entidad
   *         {@link ConvocatoriaEntidadGestora} persistida.
   */
  ConvocatoriaEntidadGestora update(ConvocatoriaEntidadGestora convocatoriaEntidadGestora);

  /**
   * Elimina la {@link ConvocatoriaEntidadGestora}.
   *
   * @param id Id del {@link ConvocatoriaEntidadGestora}.
   */
  void delete(Long id);

  /**
   * Obtiene las {@link ConvocatoriaEntidadGestora} para una {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaEntidadGestora} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaEntidadGestora> findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable);

}
