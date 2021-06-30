package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.TipoActividadNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoActividad}.
 */
public interface TipoActividadService {
  /**
   * Guardar {@link TipoActividad}.
   *
   * @param tipoActividad la entidad {@link TipoActividad} a guardar.
   * @return la entidad {@link TipoActividad} persistida.
   */
  TipoActividad create(TipoActividad tipoActividad);

  /**
   * Actualizar {@link TipoActividad}.
   *
   * @param tipoActividad la entidad {@link TipoActividad} a actualizar.
   * @return la entidad {@link TipoActividad} persistida.
   */
  TipoActividad update(TipoActividad tipoActividad);

  /**
   * Obtener todas las entidades {@link TipoActividad} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoActividad} paginadas y/o filtradas.
   */
  Page<TipoActividad> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link TipoActividad} por id.
   *
   * @param id el id de la entidad {@link TipoActividad}.
   * @return la entidad {@link TipoActividad}.
   */
  TipoActividad findById(Long id);

  /**
   * Elimina el {@link TipoActividad} por id.
   *
   * @param id el id de la entidad {@link TipoActividad}.
   */
  void delete(Long id) throws TipoActividadNotFoundException;

  /**
   * Elimina todos los {@link TipoActividad}.
   */
  void deleteAll();

}