package org.crue.hercules.sgi.eti.service.impl;

import java.util.Arrays;
import java.util.List;

import org.crue.hercules.sgi.eti.dto.TareaWithIsEliminable;
import org.crue.hercules.sgi.eti.exceptions.TareaNotFoundException;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.repository.TareaRepository;
import org.crue.hercules.sgi.eti.service.TareaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Tarea}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TareaServiceImpl implements TareaService {
  private final TareaRepository tareaRepository;

  private final static List<Long> estadosMemoriaEliminables = Arrays.asList(1L, 2L, 6L, 7L, 8L);

  public TareaServiceImpl(TareaRepository tareaRepository) {
    this.tareaRepository = tareaRepository;
  }

  /**
   * Guarda la entidad {@link Tarea}.
   *
   * @param tarea la entidad {@link Tarea} a guardar.
   * @return la entidad {@link Tarea} persistida.
   */
  @Transactional
  public Tarea create(Tarea tarea) {
    log.debug("Petición a create Tarea : {} - start", tarea);
    Assert.isNull(tarea.getId(), "Tarea id tiene que ser null para crear una nueva tarea");

    return tareaRepository.save(tarea);
  }

  /**
   * Obtiene todas las entidades {@link Tarea} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Tarea} paginadas y filtradas.
   */
  public Page<Tarea> findAll(String query, Pageable paging) {
    log.debug("findAllTarea(String query, Pageable paging) - start");
    Specification<Tarea> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Tarea> returnValue = tareaRepository.findAll(specs, paging);
    log.debug("findAllTarea(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Tarea} por id.
   *
   * @param id el id de la entidad {@link Tarea}.
   * @return la entidad {@link Tarea}.
   * @throws TareaNotFoundException Si no existe ningún {@link Tarea} con ese id.
   */
  public Tarea findById(final Long id) throws TareaNotFoundException {
    log.debug("Petición a get Tarea : {}  - start", id);
    final Tarea tarea = tareaRepository.findById(id).orElseThrow(() -> new TareaNotFoundException(id));
    log.debug("Petición a get Tarea : {}  - end", id);
    return tarea;

  }

  /**
   * Elimina una entidad {@link Tarea} por id si no esta asignada a una
   * {@link Memoria} que este en un estado lo eliminable.
   * 
   * @param id el id de la entidad {@link Tarea}.
   */
  @Transactional
  public void delete(Long id) throws TareaNotFoundException {
    log.debug("Petición a delete Tarea : {}  - start", id);
    Assert.notNull(id, "El id de Tarea no puede ser null.");
    if (!tareaRepository.existsById(id)) {
      throw new TareaNotFoundException(id);
    }

    Assert.isTrue(tareaRepository.existsByIdAndMemoriaEstadoActualIdIn(id, estadosMemoriaEliminables),
        "La memoria de la tarea esta en un estado no eliminable.");

    tareaRepository.deleteById(id);
    log.debug("Petición a delete Tarea : {}  - end", id);
  }

  /**
   * Elimina las {@link Tarea} del {@link EquipoTrabajo} si ninguna de las tareas
   * esta asignada a una {@link Memoria} que este en un estado lo eliminable.
   * 
   * @param idEquipoTrabajo el id de la entidad {@link EquipoTrabajo}.
   */
  @Transactional
  public void deleteByEquipoTrabajo(Long idEquipoTrabajo) {
    log.debug("deleteByEquipoTrabajo(Long idEquipoTrabajo) - start");
    Assert.notNull(idEquipoTrabajo, "El id de EquipoTrabajo no puede ser null.");
    tareaRepository.deleteByEquipoTrabajoId(idEquipoTrabajo);

    List<Tarea> tareasNoEliminables = tareaRepository
        .findAllByEquipoTrabajoIdAndMemoriaEstadoActualIdNotIn(idEquipoTrabajo, estadosMemoriaEliminables);
    Assert.isTrue(tareasNoEliminables.isEmpty(),
        "El equipo de trabajo tiene tareas con memorias en un estado no eliminable.");

    log.debug("deleteByEquipoTrabajo(Long idEquipoTrabajo) - end");
  }

  /**
   * Actualiza los datos del {@link Tarea}.
   * 
   * @param tareaActualizar {@link Tarea} con los datos actualizados.
   * @return El {@link Tarea} actualizado.
   * @throws TareaNotFoundException   Si no existe ningún {@link Tarea} con ese
   *                                  id.
   * @throws IllegalArgumentException Si el {@link Tarea} no tiene id.
   */
  @Transactional
  public Tarea update(final Tarea tareaActualizar) {
    log.debug("update(Tarea tareaActualizar) - start");

    Assert.notNull(tareaActualizar.getId(), "Tarea id no puede ser null para actualizar una tarea");

    return tareaRepository.findById(tareaActualizar.getId()).map(tarea -> {
      tarea.setEquipoTrabajo(tareaActualizar.getEquipoTrabajo());
      tarea.setMemoria(tareaActualizar.getMemoria());
      tarea.setTarea(tareaActualizar.getTarea());
      tarea.setFormacion(tareaActualizar.getFormacion());
      tarea.setFormacionEspecifica(tareaActualizar.getFormacionEspecifica());
      tarea.setOrganismo(tareaActualizar.getOrganismo());
      tarea.setAnio(tareaActualizar.getAnio());
      tarea.setTipoTarea(tareaActualizar.getTipoTarea());

      Tarea returnValue = tareaRepository.save(tarea);
      log.debug("update(Tarea tareaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TareaNotFoundException(tareaActualizar.getId()));
  }

  /**
   * Obtiene todas las entidades {@link Tarea} para una determinada
   * {@link PeticionEvaluacion} con la informacion de si es eliminable o no.
   * 
   * No son eliminables las {@link Tarea} que estan asociadas a una
   * {@link Memoria} que no esta en alguno de los siguiente estados: En
   * elaboración, Completada, Favorable, Pendiente de Modificaciones Mínimas,
   * Pendiente de correcciones y No procede evaluar.
   *
   * @param idPeticionEvaluacion Id de {@link PeticionEvaluacion}.
   * @return el listado de entidades {@link Tarea} paginadas y filtradas.
   */
  public List<TareaWithIsEliminable> findAllByPeticionEvaluacionId(Long idPeticionEvaluacion) {
    log.debug("findAllByPeticionEvaluacionId(Long idPeticionEvaluacion) - start");
    List<TareaWithIsEliminable> returnValue = tareaRepository.findAllByPeticionEvaluacionId(idPeticionEvaluacion);
    log.debug("findAllByPeticionEvaluacionId(Long idPeticionEvaluacion) - end");
    return returnValue;
  }

  /**
   * Busca las tareas de una petición de evaluación
   * 
   * @param idPeticionEvaluacion el id de la petición de evaluación
   * @param pageable             la paginación
   * @return la lista de tareas de la petición de evaluación
   */

  @Override
  public Page<Tarea> findAllByEquipoTrabajoPeticionEvaluacionId(Long idPeticionEvaluacion, Pageable pageable) {
    log.debug("findAllTareasNoEliminablesPeticionEvaluacion(Long idPeticionEvaluacion) - start");

    Page<Tarea> returnValue = tareaRepository.findAllByEquipoTrabajoPeticionEvaluacionId(idPeticionEvaluacion,
        pageable);
    log.debug("findAllTareasNoEliminablesPeticionEvaluacion(Long idPeticionEvaluacion) - end");
    return returnValue;
  }

}
