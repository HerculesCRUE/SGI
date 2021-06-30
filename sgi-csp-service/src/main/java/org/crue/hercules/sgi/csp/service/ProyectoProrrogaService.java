package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoProrroga}.
 */

public interface ProyectoProrrogaService {

  /**
   * Guarda la entidad {@link ProyectoProrroga}.
   * 
   * @param ProyectoProrroga la entidad {@link ProyectoProrroga} a guardar.
   * @return ProyectoProrroga la entidad {@link ProyectoProrroga} persistida.
   */
  ProyectoProrroga create(ProyectoProrroga ProyectoProrroga);

  /**
   * Actualiza la entidad {@link ProyectoProrroga}.
   * 
   * @param ProyectoProrrogaActualizar la entidad {@link ProyectoProrroga} a
   *                                   guardar.
   * @return ProyectoProrroga la entidad {@link ProyectoProrroga} persistida.
   */
  ProyectoProrroga update(ProyectoProrroga ProyectoProrrogaActualizar);

  /**
   * Elimina la {@link ProyectoProrroga}.
   *
   * @param id Id del {@link ProyectoProrroga}.
   */
  void delete(Long id);

  /**
   * Comprueba la existencia del {@link ProyectoProrroga} por id.
   *
   * @param id el id de la entidad {@link ProyectoProrroga}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene {@link ProyectoProrroga} por su id.
   *
   * @param id el id de la entidad {@link ProyectoProrroga}.
   * @return la entidad {@link ProyectoProrroga}.
   */
  ProyectoProrroga findById(Long id);

  /**
   * Obtiene las {@link ProyectoProrroga} para una {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoProrroga} de la {@link Proyecto}
   *         paginadas.
   */
  Page<ProyectoProrroga> findAllByProyecto(Long proyectoId, String query, Pageable pageable);

  /**
   * Indica si existen {@link ProyectoProrroga} de un {@link Proyecto}
   * 
   * @param proyectoId identificador de la {@link Proyecto}
   * @return si existe la entidad {@link ProyectoProrroga}
   */
  boolean existsByProyecto(Long proyectoId);

}
