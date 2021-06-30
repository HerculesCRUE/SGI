package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.EquipoTrabajoWithIsEliminable;
import org.crue.hercules.sgi.eti.exceptions.EquipoTrabajoNotFoundException;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link EquipoTrabajo}.
 */
public interface EquipoTrabajoService {
  /**
   * Guardar {@link EquipoTrabajo}.
   *
   * @param equipoTrabajo la entidad {@link EquipoTrabajo} a guardar.
   * @return la entidad {@link EquipoTrabajo} persistida.
   */
  EquipoTrabajo create(EquipoTrabajo equipoTrabajo);

  /**
   * Obtener todas las entidades {@link EquipoTrabajo} paginadas y/o filtradas con
   * informacion de si es eliminable.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link EquipoTrabajo} paginadas y/o filtradas.
   */
  Page<EquipoTrabajo> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link EquipoTrabajo} por id.
   *
   * @param id el id de la entidad {@link EquipoTrabajo}.
   * @return la entidad {@link EquipoTrabajo}.
   */
  EquipoTrabajo findById(Long id);

  /**
   * Elimina el {@link EquipoTrabajo} por id.
   *
   * @param id el id de la entidad {@link EquipoTrabajo}.
   */
  void delete(Long id) throws EquipoTrabajoNotFoundException;

  /**
   * Obtener todas las entidades paginadas {@link EquipoTrabajo} activas para una
   * determinada {@link PeticionEvaluacion} con la informacion de si es eliminable
   * o no.
   * 
   * No son eliminables los {@link EquipoTrabajo} que tienen tareas {@link Tarea}
   * que estan asociadas a una {@link Memoria} que no esta en alguno de los
   * siguiente estados: En elaboración, Completada, Favorable, Pendiente de
   * Modificaciones Mínimas, Pendiente de correcciones y No procede evaluar.
   *
   * @param id Id de {@link PeticionEvaluacion}.
   * @return la lista de entidades {@link EquipoTrabajo} paginadas.
   */
  List<EquipoTrabajoWithIsEliminable> findAllByPeticionEvaluacionId(Long id);

}