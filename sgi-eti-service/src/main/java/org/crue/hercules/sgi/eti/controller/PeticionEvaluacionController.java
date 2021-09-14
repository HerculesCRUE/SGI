package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.dto.EquipoTrabajoWithIsEliminable;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.dto.TareaWithIsEliminable;
import org.crue.hercules.sgi.eti.exceptions.EquipoTrabajoNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.TareaNotFoundException;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.service.EquipoTrabajoService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.service.PeticionEvaluacionService;
import org.crue.hercules.sgi.eti.service.TareaService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * PeticionEvaluacionController
 */
@RestController
@RequestMapping("/peticionevaluaciones")
@Slf4j
public class PeticionEvaluacionController {

  /** EquipoTrabajo service */
  private final EquipoTrabajoService equipoTrabajoService;

  /** Memoria service */
  private final MemoriaService memoriaService;

  /** PeticionEvaluacion service */
  private final PeticionEvaluacionService service;

  /** Tarea service */
  private final TareaService tareaService;

  /**
   * Instancia un nuevo PeticionEvaluacionController.
   * 
   * @param service              PeticionEvaluacionService
   * @param equipoTrabajoService EquipoTrabajoService
   * @param memoriaService       MemoriaService
   * @param tareaService         TareaService
   */
  public PeticionEvaluacionController(PeticionEvaluacionService service, EquipoTrabajoService equipoTrabajoService,
      MemoriaService memoriaService, TareaService tareaService) {
    log.debug(
        "PeticionEvaluacionController(PeticionEvaluacionService service, EquipoTrabajoService equipoTrabajoService, MemoriaService memoriaService, TareaService tareaService) - start");
    this.service = service;
    this.equipoTrabajoService = equipoTrabajoService;
    this.memoriaService = memoriaService;
    this.tareaService = tareaService;
    log.debug(
        "PeticionEvaluacionController(PeticionEvaluacionService service, EquipoTrabajoService equipoTrabajoService, MemoriaService memoriaService, TareaService tareaService)) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada
   * {@link PeticionEvaluacionWithIsEliminable}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return la lista de entidades {@link PeticionEvaluacionWithIsEliminable}
   *         paginadas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-VR-INV', 'ETI-PEV-V')")
  ResponseEntity<Page<PeticionEvaluacionWithIsEliminable>> findAll(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<PeticionEvaluacionWithIsEliminable> page = service
        .findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(query, paging, null);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link PeticionEvaluacion}.
   * 
   * @param nuevoPeticionEvaluacion {@link PeticionEvaluacion}. que se quiere
   *                                crear.
   * @return Nuevo {@link PeticionEvaluacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-C-INV', 'ETI-PEV-MOD-C')")
  ResponseEntity<PeticionEvaluacion> newPeticionEvaluacion(
      @Valid @RequestBody PeticionEvaluacion nuevoPeticionEvaluacion) {
    log.debug("newPeticionEvaluacion(PeticionEvaluacion nuevoPeticionEvaluacion) - start");
    PeticionEvaluacion returnValue = service.create(nuevoPeticionEvaluacion);
    log.debug("newPeticionEvaluacion(PeticionEvaluacion nuevoPeticionEvaluacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link PeticionEvaluacion}.
   * 
   * @param updatedPeticionEvaluacion {@link PeticionEvaluacion} a actualizar.
   * @param id                        id {@link PeticionEvaluacion} a actualizar.
   * @return {@link PeticionEvaluacion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV', 'ETI-PEV-MOD-C')")
  PeticionEvaluacion replacePeticionEvaluacion(@Valid @RequestBody PeticionEvaluacion updatedPeticionEvaluacion,
      @PathVariable Long id) {
    log.debug("replacePeticionEvaluacion(PeticionEvaluacion updatedPeticionEvaluacion, Long id) - start");
    updatedPeticionEvaluacion.setId(id);
    PeticionEvaluacion returnValue = service.update(updatedPeticionEvaluacion);
    log.debug("replacePeticionEvaluacion(PeticionEvaluacion updatedPeticionEvaluacion, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link PeticionEvaluacion} con el id indicado.
   * 
   * @param id Identificador de {@link PeticionEvaluacion}.
   * @return {@link PeticionEvaluacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV', 'ETI-PEV-V', 'ETI-PEV-MOD-C')")
  PeticionEvaluacion one(@PathVariable Long id) {
    log.debug("PeticionEvaluacion one(Long id) - start");
    PeticionEvaluacion returnValue = service.findById(id);
    log.debug("PeticionEvaluacion one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link PeticionEvaluacion} con id indicado.
   * 
   * @param id Identificador de {@link PeticionEvaluacion}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-BR-INV')")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    PeticionEvaluacion peticionEvaluacion = this.one(id);
    peticionEvaluacion.setActivo(Boolean.FALSE);
    service.update(peticionEvaluacion);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtener todas las entidades paginadas {@link EquipoTrabajo} para una
   * determinada {@link PeticionEvaluacion}.
   *
   * @param id Id de {@link PeticionEvaluacion}.
   * @return la lista de entidades {@link EquipoTrabajo} paginadas.
   */
  @GetMapping("/{id}/equipo-investigador")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-V', 'ETI-PEV-VR-INV', 'ETI-PEV-C-INV', 'ETI-PEV-ER-INV')")
  ResponseEntity<List<EquipoTrabajoWithIsEliminable>> findEquipoInvestigador(@PathVariable Long id) {
    log.debug("findEquipoInvestigador(Long id) - start");

    List<EquipoTrabajoWithIsEliminable> result = equipoTrabajoService.findAllByPeticionEvaluacionId(id);

    if (result.isEmpty()) {
      log.debug("findEquipoInvestigador(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findEquipoInvestigador(Long id) - end");
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  /**
   * Obtener todas las entidades paginadas {@link TareaWithIsEliminable} para una
   * determinada {@link PeticionEvaluacion}.
   *
   * @param id Id de {@link PeticionEvaluacion}.
   * @return la lista de entidades {@link TareaWithIsEliminable} paginadas.
   */
  @GetMapping("/{id}/tareas")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-C-INV', 'ETI-PEV-ER-INV', 'ETI-PEV-V')")
  ResponseEntity<List<TareaWithIsEliminable>> findTareas(@PathVariable Long id) {
    log.debug("findTareas(Long id) - start");

    List<TareaWithIsEliminable> result = tareaService.findAllByPeticionEvaluacionId(id);

    if (result.isEmpty()) {
      log.debug("findTareas(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findTareas(Long id) - end");
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  /**
   * Obtener todas las entidades paginadas {@link EquipoTrabajo} para una
   * determinada {@link PeticionEvaluacion}.
   *
   * @param id Id de {@link PeticionEvaluacion}.
   * @return la lista de entidades {@link Memoria} paginadas.
   */
  @GetMapping("/{id}/memorias")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-C-INV', 'ETI-PEV-ER-INV', 'ETI-PEV-V')")
  ResponseEntity<List<MemoriaPeticionEvaluacion>> findMemorias(@PathVariable Long id) {
    log.debug("findMemorias(Long id) - start");

    List<MemoriaPeticionEvaluacion> result = memoriaService.findMemoriaByPeticionEvaluacionMaxVersion(id);

    if (result.isEmpty()) {
      log.debug("findMemorias(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findMemorias(Long id) - end");
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  /**
   * Crea un nuevo {@link EquipoTrabajo} para la {@link PeticionEvaluacion} con el
   * id indicado.
   * 
   * @param id                 Identificador de {@link PeticionEvaluacion}.
   * @param nuevoEquipoTrabajo {@link EquipoTrabajo}. que se quiere crear.
   * @return Nuevo {@link EquipoTrabajo} creado.
   */
  @PostMapping("/{id}/equipos-trabajo")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-V', 'ETI-PEV-VR-INV', 'ETI-PEV-C-INV', 'ETI-PEV-ER-INV', 'ETI-PEV-MOD-C')")
  public ResponseEntity<EquipoTrabajo> createEquipoTrabajo(@PathVariable Long id,
      @Valid @RequestBody EquipoTrabajo nuevoEquipoTrabajo) {
    log.debug("createEquipoTrabajo(Long id, EquipoTrabajo nuevoEquipoTrabajo) - start");

    PeticionEvaluacion peticionEvaluacion = service.findById(id);
    if (peticionEvaluacion == null) {
      throw new PeticionEvaluacionNotFoundException(id);
    }

    nuevoEquipoTrabajo.setPeticionEvaluacion(peticionEvaluacion);

    EquipoTrabajo returnValue = equipoTrabajoService.create(nuevoEquipoTrabajo);
    log.debug("createEquipoTrabajo(Long id, EquipoTrabajo nuevoEquipoTrabajo) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Crea un nuevo {@link EquipoTrabajo} para la {@link PeticionEvaluacion} con el
   * id indicado.
   * 
   * @param idPeticionEvaluacion Identificador de {@link PeticionEvaluacion}.
   * @param idEquipoTrabajo      Identificador de {@link EquipoTrabajo}.
   * @param nuevaTarea           {@link Tarea} que se quiere crear.
   * @return Nuevo {@link EquipoTrabajo} creado.
   */
  @PostMapping("/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}/tareas")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-C-INV', 'ETI-PEV-ER-INV')")
  public ResponseEntity<Tarea> createTarea(@PathVariable Long idPeticionEvaluacion, @PathVariable Long idEquipoTrabajo,
      @Valid @RequestBody Tarea nuevaTarea) {
    log.debug("createTarea(Long idPeticionEvaluacion, Long idEquipoTrabajo, Tarea nuevaTarea) - start");

    EquipoTrabajo equipoTrabajo = equipoTrabajoService.findById(idEquipoTrabajo);
    if (equipoTrabajo == null) {
      throw new EquipoTrabajoNotFoundException(idEquipoTrabajo);
    }

    Assert.isTrue(equipoTrabajo.getPeticionEvaluacion().getId().equals(idPeticionEvaluacion),
        "El equipo de trabajo no pertenece a la peticion de evaluación");

    nuevaTarea.setEquipoTrabajo(equipoTrabajo);

    Tarea returnValue = tareaService.create(nuevaTarea);
    log.debug("createTarea(Long idPeticionEvaluacion, Long idEquipoTrabajo, Tarea nuevaTarea) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Elimina la {@link Tarea} con el idTarea indicado de
   * {@link PeticionEvaluacion} con idPeticionEvaluacion indicado.
   * 
   * @param idPeticionEvaluacion Identificador de {@link PeticionEvaluacion}.
   * @param idEquipoTrabajo      Identificador de {@link EquipoTrabajo}.
   */
  @DeleteMapping("/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV')")
  void deleteEquipoTrabajo(@PathVariable Long idPeticionEvaluacion, @PathVariable Long idEquipoTrabajo) {
    log.debug("deleteEquipoTrabajo(Long idPeticionEvaluacion, Long idEquipoTrabajo) - start");

    EquipoTrabajo equipoTrabajo = equipoTrabajoService.findById(idEquipoTrabajo);
    if (equipoTrabajo == null) {
      throw new EquipoTrabajoNotFoundException(idEquipoTrabajo);
    }

    Assert.isTrue(equipoTrabajo.getPeticionEvaluacion().getId().equals(idPeticionEvaluacion),
        "El equipo de trabajo no pertenece a la peticion de evaluación");

    tareaService.deleteByEquipoTrabajo(idEquipoTrabajo);
    equipoTrabajoService.delete(idEquipoTrabajo);

    log.debug("deleteEquipoTrabajo(Long idPeticionEvaluacion, Long idEquipoTrabajo) - end");
  }

  /**
   * Elimina la {@link Tarea} con el idTarea indicado de
   * {@link PeticionEvaluacion} con idPeticionEvaluacion indicado.
   * 
   * @param idPeticionEvaluacion Identificador de {@link PeticionEvaluacion}.
   * @param idEquipoTrabajo      Identificador de {@link EquipoTrabajo}.
   * @param idTarea              Identificador de {@link Tarea}.
   */
  @DeleteMapping("/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}/tareas/{idTarea}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV')")
  void deleteTarea(@PathVariable Long idPeticionEvaluacion, @PathVariable Long idEquipoTrabajo,
      @PathVariable Long idTarea) {
    log.debug("deleteTarea(Long idPeticionEvaluacion, Long idTarea) - start");

    Tarea tarea = tareaService.findById(idTarea);
    if (tarea == null) {
      throw new TareaNotFoundException(idTarea);
    }

    Assert.isTrue(tarea.getEquipoTrabajo().getId().equals(idEquipoTrabajo),
        "La tarea no pertenece al equipo de trabajo");

    Assert.isTrue(tarea.getEquipoTrabajo().getPeticionEvaluacion().getId().equals(idPeticionEvaluacion),
        "La tarea no pertenece a la peticion de evaluación");

    tareaService.delete(idTarea);

    log.debug("deleteTarea(Long idPeticionEvaluacion, Long idTarea) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link PeticionEvaluacion} de una
   * persona.
   * 
   * @param query          filtro de búsqueda.
   * @param paging         pageable
   * @param authentication Authentication
   * @return la lista de entidades {@link PeticionEvaluacion} paginadas.
   */
  @GetMapping("persona")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-VR-INV', 'ETI-PEV-V')")
  ResponseEntity<Page<PeticionEvaluacion>> findAllByPersonaRef(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging, Authentication authentication) {
    log.debug("findAll(String query,Pageable paging) - start");
    String personaRef = authentication.getName();
    Page<PeticionEvaluacion> page = service.findAllByPersonaRef(query, paging, personaRef);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link PeticionEvaluacion} de una
   * persona responsable de memorias o creador de peticiones de evaluacion
   * 
   * @param query          filtro de búsqueda.
   * @param paging         pageable
   * @param authentication Authentication
   * @return la lista de entidades {@link PeticionEvaluacion} paginadas.
   */
  @GetMapping("/memorias")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-VR-INV', 'ETI-PEV-V')")
  ResponseEntity<Page<PeticionEvaluacionWithIsEliminable>> findAllPeticionEvaluacionMemoria(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging,
      Authentication authentication) {
    log.debug("findAllPeticionEvaluacionMemoria(String query,Pageable paging) - start");
    String personaRef = authentication.getName();
    Page<PeticionEvaluacionWithIsEliminable> page = service
        .findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(query, paging, personaRef);

    if (page.isEmpty()) {
      log.debug("findAllPeticionEvaluacionMemoria(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllPeticionEvaluacionMemoria(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
