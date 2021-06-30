package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.TipoConvocatoriaReunionNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoConvocatoriaReunion}.
 */
public interface TipoConvocatoriaReunionService {
  /**
   * Guardar {@link TipoConvocatoriaReunion}.
   *
   * @param tipoConvocatoriaReunion la entidad {@link TipoConvocatoriaReunion} a
   *                                guardar.
   * @return la entidad {@link TipoConvocatoriaReunion} persistida.
   */
  TipoConvocatoriaReunion create(TipoConvocatoriaReunion tipoConvocatoriaReunion);

  /**
   * Actualizar {@link TipoConvocatoriaReunion}.
   *
   * @param tipoConvocatoriaReunion la entidad {@link TipoConvocatoriaReunion} a
   *                                actualizar.
   * @return la entidad {@link TipoConvocatoriaReunion} persistida.
   */
  TipoConvocatoriaReunion update(TipoConvocatoriaReunion tipoConvocatoriaReunion);

  /**
   * Obtener todas las entidades {@link TipoConvocatoriaReunion} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoConvocatoriaReunion} paginadas y/o
   *         filtradas.
   */
  Page<TipoConvocatoriaReunion> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link TipoConvocatoriaReunion} por id.
   *
   * @param id el id de la entidad {@link TipoConvocatoriaReunion}.
   * @return la entidad {@link TipoConvocatoriaReunion}.
   */
  TipoConvocatoriaReunion findById(Long id);

  /**
   * Elimina el {@link TipoConvocatoriaReunion} por id.
   *
   * @param id el id de la entidad {@link TipoConvocatoriaReunion}.
   */
  void delete(Long id) throws TipoConvocatoriaReunionNotFoundException;

  /**
   * Elimina todos los {@link TipoConvocatoriaReunion}.
   */
  void deleteAll();

}