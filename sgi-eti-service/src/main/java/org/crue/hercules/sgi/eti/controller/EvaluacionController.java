package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.dto.EvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.service.ComentarioService;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
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
 * EvaluacionController
 */
@RestController
@RequestMapping("/evaluaciones")
@Slf4j
public class EvaluacionController {

  /** Evaluacion service */
  private final EvaluacionService service;

  /** Comentario service */
  private final ComentarioService comentarioService;

  /**
   * Instancia un nuevo EvaluacionController.
   * 
   * @param service           EvaluacionService
   * @param comentarioService ComentarioService
   */
  public EvaluacionController(EvaluacionService service, ComentarioService comentarioService) {
    log.debug("EvaluacionController(EvaluacionService service, ComentarioService comentarioService) - start");
    this.service = service;
    this.comentarioService = comentarioService;
    log.debug("EvaluacionController(EvaluacionService service, ComentarioService comentarioService) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Evaluacion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<Evaluacion>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Evaluacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} activas para una
   * determinada {@link ConvocatoriaReunion}.
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @GetMapping("/convocatoriareunion/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-C', 'ETI-ACT-E')")
  ResponseEntity<Page<Evaluacion>> findAllActivasByConvocatoriaReunionId(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable) - start");
    Page<Evaluacion> page = service.findAllActivasByConvocatoriaReunionId(id, pageable);

    if (page.isEmpty()) {
      log.debug("findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene la lista de evaluaciones activas de una convocatoria reunion que no
   * estan en revisión mínima.
   *
   * @param id     Id de {@link ConvocatoriaReunion}.
   * @param query  filtro de búsqueda.
   * @param paging la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @GetMapping("/convocatoriareunionnorevminima/{idConvocatoriaReunion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-C', 'ETI-CNV-E')")
  ResponseEntity<Page<EvaluacionWithIsEliminable>> findAllByConvocatoriaReunionIdAndNoEsRevMinima(
      @PathVariable Long idConvocatoriaReunion, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllByConvocatoriaReunionIdAndNoEsRevMinima({}, {}, {}) - start", idConvocatoriaReunion, query,
        paging);
    Page<EvaluacionWithIsEliminable> page = service
        .findAllByConvocatoriaReunionIdAndNoEsRevMinima(idConvocatoriaReunion, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllByConvocatoriaReunionIdAndNoEsRevMinima({}, {}, {}) - end", idConvocatoriaReunion, query,
          paging);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllByConvocatoriaReunionIdAndNoEsRevMinima({}, {}, {}) - end", idConvocatoriaReunion, query, paging);
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /*
   * Devuelve una lista paginada y filtrada {@link Evaluacion}.
   * 
   * @param query filtro de búsqueda.
   * 
   * @param paging pageable
   */
  @GetMapping("/evaluables")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-EVAL')")
  ResponseEntity<Page<Evaluacion>> findAllByMemoriaAndRetrospectivaEnEvaluacion(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Evaluacion> page = service.findAllByMemoriaAndRetrospectivaEnEvaluacion(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link Evaluacion}.
   * 
   * @param nuevoEvaluacion {@link Evaluacion}. que se quiere crear.
   * @return Nuevo {@link Evaluacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-C', 'ETI-EVC-E','ETI-CNV-C', 'ETI-CNV-E')")
  public ResponseEntity<Evaluacion> newEvaluacion(@RequestBody Evaluacion nuevoEvaluacion) {
    log.debug("newEvaluacion(Evaluacion nuevoEvaluacion) - start");
    Evaluacion returnValue = service.create(nuevoEvaluacion);
    log.debug("newEvaluacion(Evaluacion nuevoEvaluacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Evaluacion}.
   * 
   * @param updatedEvaluacion {@link Evaluacion} a actualizar.
   * @param id                id {@link Evaluacion} a actualizar.
   * @return {@link Evaluacion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-CNV-E')")
  public Evaluacion replaceEvaluacion(@Valid @RequestBody Evaluacion updatedEvaluacion, @PathVariable Long id) {
    log.debug("replaceEvaluacion(Evaluacion updatedEvaluacion, Long id) - start");
    updatedEvaluacion.setId(id);
    Evaluacion returnValue = service.update(updatedEvaluacion);
    log.debug("replaceEvaluacion(Evaluacion updatedEvaluacion, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Evaluacion} con el id indicado.
   * 
   * @param id Identificador de {@link Evaluacion}.
   * @return {@link Evaluacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-VR-INV', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  Evaluacion one(@PathVariable Long id) {
    log.debug("Evaluacion one(Long id) - start");
    Evaluacion returnValue = service.findById(id);
    log.debug("Evaluacion one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Evaluacion} con id indicado.
   * 
   * @param id Identificador de {@link Evaluacion}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-CNV-E')")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    Evaluacion evaluacion = this.one(id);
    evaluacion.setActivo(Boolean.FALSE);
    service.update(evaluacion);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtener todas las entidades paginadas {@link Comentario} activas para una
   * determinada {@link Evaluacion}.
   *
   * @param id       Id de {@link Evaluacion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Comentario} paginadas.
   */
  @GetMapping("/{id}/comentarios-gestor")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-PEV-ER-INV')")
  ResponseEntity<Page<Comentario>> getComentariosGestor(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getComentariosGestor(Long id, Pageable pageable) - start");
    Page<Comentario> page = comentarioService.findByEvaluacionIdGestor(id, pageable);
    log.debug("getComentariosGestor(Long id, Pageable pageable) - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtener todas las entidades paginadas {@link Comentario} activas para una
   * determinada {@link Evaluacion}.
   *
   * @param id       Id de {@link Evaluacion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Comentario} paginadas.
   */
  @GetMapping("/{id}/comentarios-evaluador")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  ResponseEntity<Page<Comentario>> getComentariosEvaluador(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable, Authentication authorization) {
    log.debug("getComentariosEvaluador(Long id, Pageable pageable) - start");
    String personaRef = authorization.getName();
    Page<Comentario> page = comentarioService.findByEvaluacionIdEvaluador(id, pageable, personaRef);
    log.debug("getComentariosEvaluador(Long id, Pageable pageable) - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea un nuevo {@link Comentario} de tipo "GESTOR".
   * 
   * @param id         Id de {@link Evaluacion}.
   * @param comentario {@link Comentario} a crear.
   * @return Nuevo {@link Comentario} creado.
   */
  @PostMapping("/{id}/comentario-gestor")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-EVC-EVAL')")
  ResponseEntity<Comentario> createComentarioGestor(@PathVariable Long id, @Valid @RequestBody Comentario comentario) {
    log.debug("createComentarioGestor(Comentario comentario) - start");
    Comentario returnValue = comentarioService.createComentarioGestor(id, comentario);
    log.debug("createComentarioGestor(comentario) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Crea un nuevo {@link Comentario} de tipo "EVALUADOR".
   * 
   * @param id         Id de {@link Evaluacion}.
   * @param comentario {@link Comentario} a crear.
   * @return Nuevo {@link Comentario} creado.
   */
  @PostMapping("/{id}/comentario-evaluador")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  ResponseEntity<Comentario> createComentarioEvaluador(@PathVariable Long id, @Valid @RequestBody Comentario comentario,
      Authentication authorization) {
    log.debug("createComentarioEvaluador(Comentario comentario) - start");
    String personaRef = authorization.getName();
    Comentario returnValue = comentarioService.createComentarioEvaluador(id, comentario, personaRef);
    log.debug("createComentarioEvaluador(comentario) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza un {@link Comentario} de tipo "GESTOR".
   * 
   * @param id           Id de {@link Evaluacion}.
   * @param comentario   {@link Comentario} a actualizar.
   * @param idComentario Id de {@link Comentario}.
   * @return {@link Comentario} actualizado.
   */
  @PutMapping("/{id}/comentario-gestor/{idComentario}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-EVC-EVAL')")
  public Comentario replaceComentarioGestor(@PathVariable Long id, @PathVariable Long idComentario,
      @Validated({ Comentario.Update.class }) @RequestBody Comentario comentario) {
    log.debug("replaceComentarioGestor(Long id,  Long idComentario, Comentario comentario) - start");

    comentario.setId(idComentario);
    Comentario returnValue = comentarioService.updateComentarioGestor(id, comentario);
    log.debug("replaceComentarioGestor(Long id,  Long idComentario, Comentario comentario) - end");

    return returnValue;
  }

  /**
   * Actualiza un {@link Comentario} de tipo "EVALUADOR".
   * 
   * @param id            Id de {@link Evaluacion}.
   * @param idComentario  Id de {@link Comentario} a actualizar.
   * @param comentario    {@link Comentario} a actualizar.
   * @param authorization datos autenticación.
   * @return {@link Comentario} actualizado.
   */
  @PutMapping("/{id}/comentario-evaluador/{idComentario}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  public Comentario replaceComentarioEvaluador(@PathVariable Long id, @PathVariable Long idComentario,
      @Validated({ Comentario.Update.class }) @RequestBody Comentario comentario, Authentication authorization) {
    log.debug("replaceComentarioEvaluador( Long id,  Long idComentario, Comentario comentario) - start");
    String personaRef = authorization.getName();
    comentario.setId(idComentario);
    Comentario returnValue = comentarioService.updateComentarioEvaluador(id, comentario, personaRef);
    log.debug("replaceComentarioEvaluador( Long id,  Long idComentario, Comentario comentario) - end");

    return returnValue;
  }

  /**
   * Elimina un {@link Comentario} de tipo "GESTOR" de una evaluación.
   * 
   * @param id  Id de {@link Evaluacion}.
   * @param ids Listado de identificadores de {@link Comentario}.
   */
  @DeleteMapping("/{id}/comentario-gestor/{idComentario}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-EVC-EVAL')")
  void deleteComentarioGestor(@PathVariable Long id, @PathVariable Long idComentario) {
    log.debug("deleteComentarioGestor(Long id,  Long idComentario) - start");
    comentarioService.deleteComentarioGestor(id, idComentario);
    log.debug("deleteComentarioGestor(Long id,  Long idComentario) - end");
  }

  /**
   * Elimina un {@link Comentario} de tipo "EVALUADOR" de una evaluación.
   * 
   * @param id  Id de {@link Evaluacion}.
   * @param ids Listado de identificadores de {@link Comentario}.
   */
  @DeleteMapping("/{id}/comentario-evaluador/{idComentario}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  void deleteComentarioEvaluacion(@PathVariable Long id, @PathVariable Long idComentario,
      Authentication authorization) {
    log.debug("deleteComentarioEvaluacion(Long id,  Long idComentario) - start");
    String personaRef = authorization.getName();
    comentarioService.deleteComentarioEvaluador(id, idComentario, personaRef);
    log.debug("deleteComentarioEvaluacion(Long id,  Long idComentario) - end");
  }

  /**
   * Obtiene un listado de {@link Evaluacion} con un * determinados tipos de
   * seguimiento final
   * 
   * @param query    filtro de búsqueda.
   * @param pageable pageable
   */

  @GetMapping("/memorias-seguimiento-final")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-EVAL')")
  ResponseEntity<Page<Evaluacion>> findByEvaluacionesEnSeguimientoFinal(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Evaluacion> page = service.findByEvaluacionesEnSeguimientoFinal(query, pageable);
    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene el número de {@link Comentario} de la evaluación
   * 
   * @param id       id {@link Evaluacion}
   * @param pageable pageable
   * @return
   * @return
   */
  @GetMapping("/{id}/numero-comentarios")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-CNV-E')")
  ResponseEntity<Integer> numComentariosEvaluacion(@PathVariable Long id) {
    log.debug("countComentariosEvaluacion(@PathVariable Long id) - start");
    int countComentarios = comentarioService.countByEvaluacionId(id);
    log.debug("countComentariosEvaluacion(@PathVariable Long id) - end");
    return new ResponseEntity<>(countComentarios, HttpStatus.OK);
  }

}
