package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoProyectoSge}.
 */

public interface ProyectoProyectoSgeService {

  /**
   * Guarda la entidad {@link ProyectoProyectoSge}.
   * 
   * @param proyectoProyectoSge la entidad {@link ProyectoProyectoSge} a guardar.
   * @return la entidad {@link ProyectoProyectoSge} persistida.
   */
  ProyectoProyectoSge create(ProyectoProyectoSge proyectoProyectoSge);

  /**
   * Elimina el {@link ProyectoProyectoSge}.
   *
   * @param id Id del {@link ProyectoProyectoSge}.
   */
  void delete(Long id);

  /**
   * Comprueba la existencia del {@link ProyectoProyectoSge} por id.
   *
   * @param id el id de la entidad {@link ProyectoProyectoSge}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene {@link ProyectoProyectoSge} por su id.
   *
   * @param id el id de la entidad {@link ProyectoProyectoSge}.
   * @return la entidad {@link ProyectoProyectoSge}.
   */
  ProyectoProyectoSge findById(Long id);

  /**
   * Obtiene las {@link ProyectoProyectoSge} para un {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoProyectoSge} del
   *         {@link Proyecto} paginadas.
   */
  Page<ProyectoProyectoSge> findAllByProyecto(Long proyectoId, String query, Pageable pageable);

  /**
   * Obtiene todos los {@link ProyectoProyectoSge}.
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link ProyectoProyectoSge} paginadas.
   */
  Page<ProyectoProyectoSge> findAll(String query, Pageable pageable);

}
