package org.crue.hercules.sgi.eti.repository;

import java.util.List;

import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.repository.custom.CustomTareaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Spring Data JPA repository para {@link Tarea}.
 */
@Repository
public interface TareaRepository
    extends JpaRepository<Tarea, Long>, JpaSpecificationExecutor<Tarea>, CustomTareaRepository {

  /**
   * Obtener todas las entidades {@link Tarea} para un determinado
   * {@link EquipoTrabajo} que no estan asociadas a una {@link Memoria} en ninguno
   * de los estados de la lista.
   *
   * @param idEquipoTrabajo      Id de {@link EquipoTrabajo}.
   * @param idsTipoEstadoMemoria Ids de {@link TipoEstadoMemoria}.
   * @return la lista de entidades {@link Tarea}.
   */
  List<Tarea> findAllByEquipoTrabajoIdAndMemoriaEstadoActualIdNotIn(Long idEquipoTrabajo,
      List<Long> idsTipoEstadoMemoria);

  /**
   * Compureba si la entidad {@link Tarea} esta asociada a una {@link Memoria} en
   * alguno de los estados de la lista.
   *
   * @param id                   Id de {@link Tarea}.
   * @param idsTipoEstadoMemoria Ids de {@link TipoEstadoMemoria}.
   * @return si la tarea esta asociada a alguno de los estados o no.
   */
  boolean existsByIdAndMemoriaEstadoActualIdIn(Long id, List<Long> idsTipoEstadoMemoria);

  /**
   * Elimina las {@link Tarea} para un determinado {@link EquipoTrabajo}.
   * 
   * @param idEquipoTrabajo identificador {@link EquipoTrabajo}
   */
  void deleteByEquipoTrabajoId(Long idEquipoTrabajo);

  /**
   * Busca las tareas de una petición de evaluación
   * 
   * @param idPeticionEvaluacion el id de la petición de evaluación
   * @param pageable             la paginación
   * @return la lista de tareas de la petición de evaluación
   */

  Page<Tarea> findAllByEquipoTrabajoPeticionEvaluacionId(Long idPeticionEvaluacion, Pageable pageable);

}