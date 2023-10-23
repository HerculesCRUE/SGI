package org.crue.hercules.sgi.eti.controller;

import java.time.Instant;
import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConflictoInteres;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.service.ActaService;
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
@RequestMapping(EvaluadorController.REQUEST_MAPPING)
@Slf4j
public class EvaluadorController {

  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "evaluadores";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_ACTIVO_COMITE = PATH_ID + PATH_DELIMITER + "activo-comite/{comiteId}";

  /** Evaluador service */
  private final EvaluadorService evaluadorService;

  /** Evaluación service. */
  private final EvaluacionService evaluacionService;

  /** Acta service. */
  private final ActaService actaService;

  /** ConflictoInteres service */
  private final ConflictoInteresService conflictoInteresService;

  public EvaluadorController(EvaluadorService evaluadorService, EvaluacionService evaluacionService,
      ConflictoInteresService conflictoInteresService, ActaService actaService) {
    this.evaluadorService = evaluadorService;
    this.evaluacionService = evaluacionService;
    this.conflictoInteresService = conflictoInteresService;
    this.actaService = actaService;
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
  @GetMapping("comite/{idComite}/sinconflictointereses/{idMemoria}/fecha/{fechaEvaluacion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-C', 'ETI-CNV-E','ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
  ResponseEntity<List<Evaluador>> findAllByComiteSinconflictoInteresesMemoria(@PathVariable Long idComite,
      @PathVariable Long idMemoria, @PathVariable Instant fechaEvaluacion) {
    log.debug("findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria) - start");
    List<Evaluador> result = evaluadorService.findAllByComiteSinconflictoInteresesMemoria(idComite, idMemoria,
        fechaEvaluacion);

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
  @PutMapping(PATH_ID)
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
  @GetMapping(PATH_ID)
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
  @DeleteMapping(PATH_ID)
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V','ETI-EVC-VR', 'ETI-EVC-INV-VR', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-INV-VR', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V','ETI-EVC-VR', 'ETI-EVC-INV-VR', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<?> hasAssignedEvaluaciones(Authentication authorization) {
    log.debug("hasAssignedEvaluaciones(Authentication authorization) - start");
    String personaRef = authorization.getName();
    if (Boolean.TRUE.equals(evaluacionService.hasAssignedEvaluacionesByEvaluador(personaRef))) {
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-INV-VR', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<?> hasAssignedEvaluacionesSeguimiento(Authentication authorization) {
    log.debug("hasAssignedEvaluacionesSeguimiento(Authentication authorization) - start");
    String personaRef = authorization.getName();
    if (Boolean.TRUE.equals(evaluacionService.hasAssignedEvaluacionesSeguimientoByEvaluador(personaRef))) {
      log.debug("hasAssignedEvaluacionesSeguimiento(Authentication authorization) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("hasAssignedEvaluacionesSeguimiento(Authentication authorization) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si el usuario es Evaluador en algun Acta
   * 
   * @param authorization authorization
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/actas", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V','ETI-EVC-VR', 'ETI-EVC-INV-VR', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<?> hasAssignedActas(Authentication authorization) {
    log.debug("hasAssignedActas(Authentication authorization) - start");
    String personaRef = authorization.getName();
    if (Boolean.TRUE.equals(actaService.hasAssignedActasByEvaluador(personaRef))) {
      log.debug("hasAssignedActas(Authentication authorization) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("hasAssignedActas(Authentication authorization) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si el usuario es Evaluador en algun comite
   * 
   * @param authorization authorization
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/is-evaluador", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V','ETI-EVC-VR', 'ETI-EVC-INV-VR', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<Void> isEvaluador(Authentication authorization) {
    log.debug("isEvaluador(Authentication authorization) - start");
    String personaRef = authorization.getName();
    if (evaluadorService.isEvaluador(personaRef)) {
      log.debug("isEvaluador(Authentication authorization) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("isEvaluador(Authentication authorization) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si la persona correspondiente al evaluador esta activa en el
   * {@link Comite}
   * 
   * @param id       identificador del {@link Evaluador}
   * @param comiteId identificador del {@link Comite}
   * @return {@link HttpStatus#OK} si esta activo o {@link HttpStatus#NO_CONTENT}
   *         si no
   */
  @RequestMapping(path = PATH_ACTIVO_COMITE, method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthority('ETI-CNV-E')")
  public ResponseEntity<Void> isEvaluadorActivoComite(@PathVariable Long id, @PathVariable Long comiteId) {
    log.debug("isEvaluadorActivoComite({}, {}) - start", id, comiteId);
    boolean activo = evaluadorService.isEvaluadorActivoComite(id, comiteId);
    log.debug("isEvaluadorActivoComite({}, {}) - end", id, comiteId);
    return activo ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
