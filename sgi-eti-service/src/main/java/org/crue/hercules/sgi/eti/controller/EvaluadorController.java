package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.ConflictoInteres;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.service.ConflictoInteresService;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
import org.crue.hercules.sgi.eti.service.EvaluadorService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * EvaluadorController
 */
@RestController
@RequestMapping("/evaluadores")
@Slf4j
public class EvaluadorController {

  /** Evaluador service */
  private final EvaluadorService evaluadorService;

  /** Evaluación service. */
  private final EvaluacionService evaluacionService;

  /** ConflictoInteres service */
  private final ConflictoInteresService conflictoInteresService;

  /**
   * Instancia un nuevo EvaluadorController. x
   * 
   * @param evaluadorService        {@link EvaluadorService}
   * @param evaluacionService       {@link EvaluacionService}
   * @param conflictoInteresService {@link ConflictoInteresService}
   */
  public EvaluadorController(EvaluadorService evaluadorService, EvaluacionService evaluacionService,
      ConflictoInteresService conflictoInteresService) {
    log.debug("EvaluadorController(EvaluadorService service) - start");
    this.evaluadorService = evaluadorService;
    this.evaluacionService = evaluacionService;
    this.conflictoInteresService = conflictoInteresService;
    log.debug("EvaluadorController(EvaluadorService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Evaluador}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVR-V')")
  ResponseEntity<Page<Evaluador>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Evaluador> page = evaluadorService.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link Evaluador} de un comite sin conflictos
   * de intereses con una memoria.
   * 
   * @param query filtro de búsqueda.
   */
  @GetMapping("comite/{idComite}/sinconflictointereses/{idMemoria}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-C', 'ETI-CNV-E')")
  ResponseEntity<List<Evaluador>> findAllByComiteSinconflictoInteresesMemoria(@PathVariable Long idComite,
      @PathVariable Long idMemoria) {
    log.debug("findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria) - start");
    List<Evaluador> result = evaluadorService.findAllByComiteSinconflictoInteresesMemoria(idComite, idMemoria);

    if (result.isEmpty()) {
      log.debug("findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria) ) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria)  - end");
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link Evaluador}.
   * 
   * @param nuevoEvaluador {@link Evaluador}. que se quiere crear.
   * @return Nuevo {@link Evaluador} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVR-C')")
  ResponseEntity<Evaluador> newEvaluador(@Valid @RequestBody Evaluador nuevoEvaluador) {
    log.debug("newEvaluador(Evaluador nuevoEvaluador) - start");
    Evaluador returnValue = evaluadorService.create(nuevoEvaluador);
    log.debug("newEvaluador(Evaluador nuevoEvaluador) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Evaluador}.
   * 
   * @param updatedEvaluador {@link Evaluador} a actualizar.
   * @param id               id {@link Evaluador} a actualizar.
   * @return {@link Evaluador} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVR-E')")
  Evaluador replaceEvaluador(@Valid @RequestBody Evaluador updatedEvaluador, @PathVariable Long id) {
    log.debug("replaceEvaluador(Evaluador updatedEvaluador, Long id) - start");
    updatedEvaluador.setId(id);
    Evaluador returnValue = evaluadorService.update(updatedEvaluador);
    log.debug("replaceEvaluador(Evaluador updatedEvaluador, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Evaluador} con el id indicado.
   * 
   * @param id Identificador de {@link Evaluador}.
   * @return {@link Evaluador} correspondiente al id.
   */
  @GetMapping("/{id}")
  Evaluador one(@PathVariable Long id) {
    log.debug("Evaluador one(Long id) - start");
    Evaluador returnValue = evaluadorService.findById(id);
    log.debug("Evaluador one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Evaluador} con id indicado.
   * 
   * @param id Identificador de {@link Evaluador}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVR-B')")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    Evaluador evaluador = this.one(id);
    evaluador.setActivo(Boolean.FALSE);
    evaluadorService.update(evaluador);
    log.debug("delete(Long id) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Evaluacion} según su
   * {@link Evaluador}.
   * 
   * @param query          filtro de búsqueda.
   * @param pageable       pageable
   * @param Authentication authorization
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @GetMapping("/evaluaciones")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V','ETI-EVC-VR', 'ETI-EVC-VR-INV', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  ResponseEntity<Page<Evaluacion>> getEvaluaciones(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable pageable, Authentication authorization) {
    log.debug("getEvaluaciones(String query, Pageable pageable) - start");
    String personaRef = authorization.getName();
    Page<Evaluacion> page = evaluacionService.findByEvaluador(personaRef, query, pageable);
    log.debug("getEvaluaciones(String query, Pageable pageable) - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene todas las entidades {@link Evaluacion}, en estado "En evaluación
   * seguimiento anual" (id = 11), "En evaluación seguimiento final" (id = 12) o
   * "En secretaría seguimiento final aclaraciones" (id = 13), paginadas asociadas
   * a un evaluador
   * 
   * @param query          filtro de búsqueda.
   * @param pageable       pageable
   * @param Authentication authorization
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  @GetMapping("/evaluaciones-seguimiento")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-VR-INV', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  ResponseEntity<Page<Evaluacion>> findEvaluacionesEnSeguimiento(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable pageable,
      Authentication authorization) {
    log.debug("findEvaluacionesEnSeguimiento(String query, Pageable pageable) - start");
    String personaRef = authorization.getName();
    Page<Evaluacion> page = evaluacionService.findEvaluacionesEnSeguimientosByEvaluador(personaRef, query, pageable);
    log.debug("findEvaluacionesEnSeguimiento(String query, Pageable pageable) - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ConflictoInteres} según su
   * {@link Evaluador}.
   * 
   * @param id       id {@Evaluador}
   * @param pageable pageable
   * @return la lista de entidades {@link ConflictoInteres} paginadas.
   */
  @GetMapping("{id}/conflictos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVR-C', 'ETI-EVR-E')")
  ResponseEntity<Page<ConflictoInteres>> getConflictosInteres(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getConflictosInteres(Long id, Pageable pageable) - start");
    Page<ConflictoInteres> page = conflictoInteresService.findAllByEvaluadorId(id, pageable);
    log.debug("getConflictosInteres(Long id, Pageable pageable) - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Comprueba si el usuario es Evaluador en alguna Evaluacion
   * 
   * @param authorization authorization
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/evaluaciones", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V','ETI-EVC-VR', 'ETI-EVC-VR-INV', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  public ResponseEntity<?> hasAssignedEvaluaciones(Authentication authorization) {
    log.debug("hasAssignedEvaluaciones(Authentication authorization) - start");
    String personaRef = authorization.getName();
    if (evaluacionService.hasAssignedEvaluacionesByEvaluador(personaRef)) {
      log.debug("hasAssignedEvaluaciones(Authentication authorization) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("hasAssignedEvaluaciones(Authentication authorization) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si el usuario es Evaluador en alguna Evaluacion en Seguimiento
   * 
   * @param authorization authorization
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/evaluaciones-seguimiento", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-VR-INV', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  public ResponseEntity<?> hasAssignedEvaluacionesSeguimiento(Authentication authorization) {
    log.debug("hasAssignedEvaluacionesSeguimiento(Authentication authorization) - start");
    String personaRef = authorization.getName();
    if (evaluacionService.hasAssignedEvaluacionesSeguimientoByEvaluador(personaRef)) {
      log.debug("hasAssignedEvaluacionesSeguimiento(Authentication authorization) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("hasAssignedEvaluacionesSeguimiento(Authentication authorization) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
