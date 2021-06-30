package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPartida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoPartida}.
 */

public interface ProyectoPartidaService {

  /**
   * Guarda la entidad {@link ProyectoPartida}.
   * 
   * @param proyectoPartida la entidad {@link ProyectoPartida} a guardar.
   * @return la entidad {@link ProyectoPartida} persistida.
   */
  ProyectoPartida create(ProyectoPartida proyectoPartida);

  /**
   * Actualiza la entidad {@link ProyectoPartida}.
   * 
   * @param proyectoPartidaActualizar la entidad {@link ProyectoPartida} a
   *                                  guardar.
   * @return la entidad {@link ProyectoPartida} persistida.
   */
  ProyectoPartida update(ProyectoPartida proyectoPartidaActualizar);

  /**
   * Elimina la {@link ProyectoPartida}.
   *
   * @param id Id del {@link ProyectoPartida}.
   */
  void delete(Long id);

  /**
   * Comprueba la existencia del {@link ProyectoPartida} por id.
   *
   * @param id el id de la entidad {@link ProyectoPartida}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene {@link ProyectoPartida} por su id.
   *
   * @param id el id de la entidad {@link ProyectoPartida}.
   * @return la entidad {@link ProyectoPartida}.
   */
  ProyectoPartida findById(Long id);

  /**
   * Obtiene las {@link ProyectoPartida} para un {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoPartida} de la {@link Proyecto}
   *         paginadas.
   */
  Page<ProyectoPartida> findAllByProyecto(Long proyectoId, String query, Pageable pageable);

}
