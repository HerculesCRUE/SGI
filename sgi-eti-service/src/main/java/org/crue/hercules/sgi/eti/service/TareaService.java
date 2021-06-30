package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.TareaWithIsEliminable;
import org.crue.hercules.sgi.eti.exceptions.TareaNotFoundException;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Tarea}.
 */
public interface TareaService {

  /**
   * Guardar {@link Tarea}.
   *
   * @param tarea la entidad {@link Tarea} a guardar.
   * @return la entidad {@link Tarea} persistida.
   */
  Tarea create(Tarea tarea);

  /**
   * Actualizar {@link Tarea}.
   *
   * @param tarea la entidad {@link Tarea} a actualizar.
   * @return la entidad {@link Tarea} persistida.
   */
  Tarea update(Tarea tarea);

  /**
   * Obtener todas las entidades {@link Tarea} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Tarea} paginadas y/o filtradas.
   */
  Page<Tarea> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link Tarea} por id.
   *
   * @param id el id de la entidad {@link Tarea}.
   * @return la entidad {@link Tarea}.
   */
  Tarea findById(Long id);

  /**
   * Elimina la {@link Tarea} por id.
   *
   * @param id el id de la entidad {@link Tarea}.
   */
  void delete(Long id) throws TareaNotFoundException;

  /**
   * Elimina las {@link Tarea} del {@link EquipoTrabajo}.
   * 
   * @param idEquipoTrabajo el id de la entidad {@link EquipoTrabajo}.
   */
  void deleteByEquipoTrabajo(Long idEquipoTrabajo);

  /**
   * Obtener todas las entidades {@link Tarea} para una determinada
   * {@link PeticionEvaluacion} con la informacion de si es eliminable o no.
   * 
   * No son eliminables las {@link Tarea} que estan asociadas a una
   * {@link Memoria} que no esta en alguno de los siguiente estados: En
   * elaboración, Completada, Favorable, Pendiente de Modificaciones Mínimas,
   * Pendiente de correcciones y No procede evaluar.
   *
   * @param idPeticionEvaluacion Id de {@link PeticionEvaluacion}.
   * @return la lista de entidades {@link Tarea} paginadas y/o filtradas.
   */
  List<TareaWithIsEliminable> findAllByPeticionEvaluacionId(Long idPeticionEvaluacion);

  /**
   * Busca las tareas de una petición de evaluación
   * 
   * @param idPeticionEvaluacion el id de la petición de evaluación
   * @param pageable             la paginación
   * @return la lista de tareas de la petición de evaluación
   */

  Page<Tarea> findAllByEquipoTrabajoPeticionEvaluacionId(Long idPeticionEvaluacion, Pageable pageable);

}