package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConvocatoriaAreaTematica}.
 */

public interface ConvocatoriaAreaTematicaService {

  /**
   * Guarda la entidad {@link ConvocatoriaAreaTematica}.
   * 
   * @param convocatoriaAreaTematica la entidad {@link ConvocatoriaAreaTematica} a
   *                                 guardar.
   * @return ConvocatoriaAreaTematica la entidad {@link ConvocatoriaAreaTematica}
   *         persistida.
   */
  ConvocatoriaAreaTematica create(ConvocatoriaAreaTematica convocatoriaAreaTematica);

  /**
   * Actualiza la entidad {@link ConvocatoriaAreaTematica}.
   * 
   * @param convocatoriaAreaTematicaActualizar la entidad
   *                                           {@link ConvocatoriaAreaTematica} a
   *                                           guardar.
   * @return ConvocatoriaAreaTematica la entidad {@link ConvocatoriaAreaTematica}
   *         persistida.
   */
  ConvocatoriaAreaTematica update(ConvocatoriaAreaTematica convocatoriaAreaTematicaActualizar);

  /**
   * Elimina la {@link ConvocatoriaAreaTematica}.
   *
   * @param id Id del {@link ConvocatoriaAreaTematica}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ConvocatoriaAreaTematica} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaAreaTematica}.
   * @return la entidad {@link ConvocatoriaAreaTematica}.
   */
  ConvocatoriaAreaTematica findById(Long id);

  /**
   * Obtiene las {@link ConvocatoriaAreaTematica} para una {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaAreaTematica} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaAreaTematica> findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable);

}
