package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.EquipoTrabajoWithIsEliminable;
import org.crue.hercules.sgi.eti.exceptions.EquipoTrabajoNotFoundException;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.repository.EquipoTrabajoRepository;
import org.crue.hercules.sgi.eti.service.EquipoTrabajoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link EquipoTrabajo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class EquipoTrabajoServiceImpl implements EquipoTrabajoService {
  private final EquipoTrabajoRepository equipoTrabajoRepository;

  public EquipoTrabajoServiceImpl(EquipoTrabajoRepository equipoTrabajoRepository) {
    this.equipoTrabajoRepository = equipoTrabajoRepository;
  }

  /**
   * Guarda la entidad {@link EquipoTrabajo}.
   *
   * @param equipoTrabajo la entidad {@link EquipoTrabajo} a guardar.
   * @return la entidad {@link EquipoTrabajo} persistida.
   */
  @Transactional
  public EquipoTrabajo create(EquipoTrabajo equipoTrabajo) {
    log.debug("Petición a create EquipoTrabajo : {} - start", equipoTrabajo);
    Assert.isNull(equipoTrabajo.getId(), "EquipoTrabajo id tiene que ser null para crear un nuevo equipoTrabajo");

    return equipoTrabajoRepository.save(equipoTrabajo);
  }

  /**
   * Obtiene todas las entidades {@link EquipoTrabajo} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link EquipoTrabajo} paginadas y filtradas.
   */
  public Page<EquipoTrabajo> findAll(String query, Pageable paging) {
    log.debug("findAllEquipoTrabajo(String query,Pageable paging) - start");
    Specification<EquipoTrabajo> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<EquipoTrabajo> returnValue = equipoTrabajoRepository.findAll(specs, paging);
    log.debug("findAllEquipoTrabajo(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link EquipoTrabajo} por id.
   *
   * @param id el id de la entidad {@link EquipoTrabajo}.
   * @return la entidad {@link EquipoTrabajo}.
   * @throws EquipoTrabajoNotFoundException Si no existe ningún
   *                                        {@link EquipoTrabajo} con ese id.
   */
  public EquipoTrabajo findById(final Long id) throws EquipoTrabajoNotFoundException {
    log.debug("Petición a get EquipoTrabajo : {}  - start", id);
    final EquipoTrabajo EquipoTrabajo = equipoTrabajoRepository.findById(id)
        .orElseThrow(() -> new EquipoTrabajoNotFoundException(id));
    log.debug("Petición a get EquipoTrabajo : {}  - end", id);
    return EquipoTrabajo;

  }

  /**
   * Elimina una entidad {@link EquipoTrabajo} por id.
   *
   * @param id el id de la entidad {@link EquipoTrabajo}.
   */
  @Transactional
  public void delete(Long id) throws EquipoTrabajoNotFoundException {
    log.debug("Petición a delete EquipoTrabajo : {}  - start", id);
    Assert.notNull(id, "El id de EquipoTrabajo no puede ser null.");
    if (!equipoTrabajoRepository.existsById(id)) {
      throw new EquipoTrabajoNotFoundException(id);
    }
    equipoTrabajoRepository.deleteById(id);
    log.debug("Petición a delete EquipoTrabajo : {}  - end", id);
  }

  /**
   * Actualiza los datos del {@link EquipoTrabajo}.
   * 
   * @param equipoTrabajoActualizar {@link EquipoTrabajo} con los datos
   *                                actualizados.
   * @return El {@link EquipoTrabajo} actualizado.
   * @throws EquipoTrabajoNotFoundException Si no existe ningún
   *                                        {@link EquipoTrabajo} con ese id.
   * @throws IllegalArgumentException       Si el {@link EquipoTrabajo} no tiene
   *                                        id.
   */

  @Transactional
  public EquipoTrabajo update(final EquipoTrabajo equipoTrabajoActualizar) {
    log.debug("update(EquipoTrabajo equipoTrabajoActualizar) - start");

    Assert.notNull(equipoTrabajoActualizar.getId(),
        "EquipoTrabajo id no puede ser null para actualizar un equipo de trabajo");

    return equipoTrabajoRepository.findById(equipoTrabajoActualizar.getId()).map(equipoTrabajo -> {
      equipoTrabajo.setPeticionEvaluacion(equipoTrabajoActualizar.getPeticionEvaluacion());
      equipoTrabajo.setPersonaRef(equipoTrabajoActualizar.getPersonaRef());

      EquipoTrabajo returnValue = equipoTrabajoRepository.save(equipoTrabajo);
      log.debug("update(EquipoTrabajo equipoTrabajoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new EquipoTrabajoNotFoundException(equipoTrabajoActualizar.getId()));
  }

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
  @Override
  public List<EquipoTrabajoWithIsEliminable> findAllByPeticionEvaluacionId(Long id) {
    log.debug("findAllByPeticionEvaluacionId(Long id, Pageable pageable) - start");
    Assert.notNull(id, "PeticionEvaluacion id no puede ser null para buscar su equipo de trabajo");

    List<EquipoTrabajoWithIsEliminable> returnValue = equipoTrabajoRepository.findAllByPeticionEvaluacionId(id);
    log.debug("findAllByPeticionEvaluacionId(Long id, Pageable pageable) - end");
    return returnValue;
  }

}
