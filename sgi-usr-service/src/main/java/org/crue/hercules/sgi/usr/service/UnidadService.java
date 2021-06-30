package org.crue.hercules.sgi.usr.service;

import org.crue.hercules.sgi.usr.model.Unidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Unidad}.
 */
public interface UnidadService {

  /**
   * Obtener todas las entidades {@link Unidad} activas paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Unidad} paginadas y/o filtradas.
   */
  Page<Unidad> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link Unidad} por su id.
   *
   * @param id el id de la entidad {@link Unidad}.
   * @return la entidad {@link Unidad}.
   */
  Unidad findById(Long id);

  /**
   * Recupera una lista de paginada de {@link Unidad} restringidas por los
   * permisos del usuario logueado.
   * 
   * @param query  datos de búsqueda
   * 
   * @param paging datos de la paginación
   * @return listado paginado de {@link Unidad}
   */
  Page<Unidad> findAllRestringidos(String query, Pageable paging);

}
