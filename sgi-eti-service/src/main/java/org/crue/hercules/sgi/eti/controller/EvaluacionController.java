package org.crue.hercules.sgi.eti.controller;

import java.time.Instant;
import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.dto.DocumentoOutput;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.model.BaseEntity;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.service.ComentarioService;
import org.crue.hercules.sgi.eti.service.DictamenService;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EvaluacionController
 */
@RestController
@RequestMapping("/evaluaciones")
@Slf4j
@RequiredArgsConstructor
public class EvaluacionController {

  private static final String FIND_ALL_STRING_QUERY_PAGEABLE_PAGING_END = "findAll(String query,Pageable paging) - end";
  private static final String FIND_ALL_STRING_QUERY_PAGEABLE_PAGING_START = "findAll(String query,Pageable paging) - start";

  /** Evaluacion service */
  private final EvaluacionService service;

  /** Comentario service */
  private final ComentarioService comentarioService;

  /** Dictamen service */
  private final DictamenService dictamenService;

  /**
   * Devuelve una lista paginada y filtrada {@link Evaluacion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return Lista paginada de evaluaciones
   */
  @GetMapping()
  public ResponseEntity<Page<Evaluacion>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug(FIND_ALL_STRING_QUERY_PAGEABLE_PAGING_START);
    Page<Evaluacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug(FIND_ALL_STRING_QUERY_PAGEABLE_PAGING_END);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug(FIND_ALL_STRING_QUERY_PAGEABLE_PAGING_END);
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-C','ETI-ACT-E','ETI-ACT-INV-ER','ETI-ACT-ER')")
  public ResponseEntity<Page<Evaluacion>> findAllActivasByConvocatoriaReunionId(@PathVariable Long id,
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
   * @param idConvocatoriaReunion Id de {@link ConvocatoriaReunion}.
   * @param query                 filtro de búsqueda.
   * @param paging                la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @GetMapping("/convocatoriareunionnorevminima/{idConvocatoriaReunion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-C', 'ETI-CNV-E')")
  public ResponseEntity<Page<EvaluacionWithIsEliminable>> findAllByConvocatoriaReunionIdAndNoEsRevMinima(
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
  public ResponseEntity<Page<Evaluacion>> findAllByMemoriaAndRetrospectivaEnEvaluacion(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllByMemoriaAndRetrospectivaEnEvaluacion() - start");
    Page<Evaluacion> page = service.findAllByMemoriaAndRetrospectivaEnEvaluacion(query, paging);
    log.debug("findAllByMemoriaAndRetrospectivaEnEvaluacion() - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-INV-VR', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
  public Evaluacion one(@PathVariable Long id) {
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
  public void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    Evaluacion evaluacion = this.one(id);
    evaluacion.setActivo(Boolean.FALSE);
    service.update(evaluacion);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtener todas las entidades {@link Comentario} activas para una
   * determinada {@link Evaluacion}.
   *
   * @param id Id de {@link Evaluacion}.
   * @return la lista de entidades {@link Comentario}.
   */
  @GetMapping("/{id}/comentarios-gestor")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-PEV-INV-ER', 'ETI-EVC-EVALR')")
  public ResponseEntity<List<Comentario>> getComentariosGestor(@PathVariable Long id) {
    log.debug("getComentariosGestor(Long id) - start");
    List<Comentario> comentarios = comentarioService.findByEvaluacionIdGestor(id);
    log.debug("getComentariosGestor(Long id, Pageable pageable) - end");
    if (comentarios.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(comentarios, HttpStatus.OK);
  }

  /**
   * Obtener todas las entidades {@link Comentario} activas para una
   * determinada {@link Evaluacion}.
   *
   * @param id            Id de {@link Evaluacion}.
   * @param authorization autenticación
   * @return la lista de entidades {@link Comentario}.
   */
  @GetMapping("/{id}/comentarios-evaluador")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<List<Comentario>> getComentariosEvaluador(@PathVariable Long id, Authentication authorization) {
    log.debug("getComentariosEvaluador(Long id - start");
    String personaRef = authorization.getName();
    List<Comentario> comentarios = comentarioService.findByEvaluacionIdEvaluador(id, personaRef);
    log.debug("getComentariosEvaluador(Long id) - end");
    if (comentarios.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(comentarios, HttpStatus.OK);
  }

  /**
   * Obtener todas las entidades {@link Comentario} activas para una
   * determinada {@link Evaluacion} de tipoComentario ACTA_GESTOR.
   *
   * @param id Id de {@link Evaluacion}.
   * @return la lista de entidades {@link Comentario}.
   */
  @GetMapping("/{id}/comentarios-acta-gestor")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-E', 'ETI-ACT-INV-ER', 'ETI-ACT-ER')")
  public ResponseEntity<List<Comentario>> getComentariosActaGestor(@PathVariable Long id) {
    log.debug("getComentariosActa(Long id) - start");
    List<Comentario> comentarios = comentarioService.findByEvaluacionIdActaGestor(id);
    log.debug("getComentariosActa(Long id) - end");
    if (comentarios.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return new ResponseEntity<>(comentarios, HttpStatus.OK);
  }

  /**
   * Obtener todas las entidades {@link Comentario} activas para una
   * determinada {@link Evaluacion} de tipoComentario ACTA_EVALUADOR.
   *
   * @param id            Id de {@link Evaluacion}.
   * @param authorization autenticación
   * 
   * @return la lista de entidades {@link Comentario}.
   */
  @GetMapping("/{id}/comentarios-acta-evaluador")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-ER', 'ETI-ACT-INV-ER')")
  public ResponseEntity<List<Comentario>> getComentariosActaEvaluador(@PathVariable Long id,
      Authentication authorization) {
    log.debug("getComentariosActa(Long id) - start");
    String personaRef = authorization.getName();
    List<Comentario> comentarios = comentarioService.findByEvaluacionIdActaEvaluador(id, personaRef);
    log.debug("getComentariosActa(Long id) - end");
    if (comentarios.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(comentarios, HttpStatus.OK);
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
  public ResponseEntity<Comentario> createComentarioGestor(@PathVariable Long id,
      @Valid @RequestBody Comentario comentario) {
    log.debug("createComentarioGestor(Comentario comentario) - start");
    Comentario returnValue = comentarioService.createComentarioGestor(id, comentario);
    log.debug("createComentarioGestor(comentario) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Crea un nuevo {@link Comentario} de tipo "EVALUADOR".
   * 
   * @param id            Id de {@link Evaluacion}.
   * @param comentario    {@link Comentario} a crear.
   * @param authorization autenticación
   * 
   * @return Nuevo {@link Comentario} creado.
   */
  @PostMapping("/{id}/comentario-evaluador")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<Comentario> createComentarioEvaluador(@PathVariable Long id,
      @Valid @RequestBody Comentario comentario, Authentication authorization) {
    log.debug("createComentarioEvaluador(Comentario comentario) - start");
    String personaRef = authorization.getName();
    Comentario returnValue = comentarioService.createComentarioEvaluador(id, comentario, personaRef);
    log.debug("createComentarioEvaluador(comentario) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Crea un nuevo {@link Comentario} de tipo "ACTA_GESTOR".
   * 
   * @param id         Id de {@link Evaluacion}.
   * @param comentario {@link Comentario} a crear.
   * @return Nuevo {@link Comentario} creado.
   */
  @PostMapping("/{id}/comentario-acta-gestor")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-INV-ER', 'ETI-ACT-ER', 'ETI-ACT-E')")
  public ResponseEntity<Comentario> createComentarioActaGestor(@PathVariable Long id,
      @Valid @RequestBody Comentario comentario) {
    log.debug("createComentarioActa(Comentario comentario) - start");
    Comentario returnValue = comentarioService.createComentarioActaGestor(id, comentario);
    log.debug("createComentarioActa(comentario) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Crea un nuevo {@link Comentario} de tipo "ACTA_EVALUADOR".
   * 
   * @param id            Id de {@link Evaluacion}.
   * @param comentario    {@link Comentario} a crear.
   * @param authorization autenticación
   * 
   * @return Nuevo {@link Comentario} creado.
   */
  @PostMapping("/{id}/comentario-acta-evaluador")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-INV-ER', 'ETI-ACT-ER', 'ETI-ACT-E')")
  public ResponseEntity<Comentario> createComentarioActaEvaluador(@PathVariable Long id,
      @Valid @RequestBody Comentario comentario, Authentication authorization) {
    log.debug("createComentarioActaEvaluador(Long id, Comentario comentario, Authentication authorization) - start");
    String personaRef = authorization.getName();
    Comentario returnValue = comentarioService.createComentarioActaEvaluador(id, comentario, personaRef);
    log.debug("createComentarioActaEvaluador(Long id, Comentario comentario, Authentication authorization) - end");
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
      @Validated({ BaseEntity.Update.class }) @RequestBody Comentario comentario) {
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
  public Comentario replaceComentarioEvaluador(@PathVariable Long id, @PathVariable Long idComentario,
      @Validated({ BaseEntity.Update.class }) @RequestBody Comentario comentario, Authentication authorization) {
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
   * @param id           Id de {@link Evaluacion}.
   * @param idComentario Identificador de {@link Comentario}.
   */
  @DeleteMapping("/{id}/comentario-gestor/{idComentario}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-EVC-EVAL')")
  public void deleteComentarioGestor(@PathVariable Long id, @PathVariable Long idComentario) {
    log.debug("deleteComentarioGestor(Long id,  Long idComentario) - start");
    comentarioService.deleteComentarioGestor(id, idComentario);
    log.debug("deleteComentarioGestor(Long id,  Long idComentario) - end");
  }

  /**
   * Elimina un {@link Comentario} de tipo "EVALUADOR" de una evaluación.
   * 
   * @param id            Id de {@link Evaluacion}.
   * @param idComentario  Identificador de {@link Comentario}.
   * @param authorization autenticación
   * 
   */
  @DeleteMapping("/{id}/comentario-evaluador/{idComentario}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
  public void deleteComentarioEvaluacion(@PathVariable Long id, @PathVariable Long idComentario,
      Authentication authorization) {
    log.debug("deleteComentarioEvaluacion(Long id,  Long idComentario) - start");
    String personaRef = authorization.getName();
    comentarioService.deleteComentarioEvaluador(id, idComentario, personaRef);
    log.debug("deleteComentarioEvaluacion(Long id,  Long idComentario) - end");
  }

  /**
   * Elimina un {@link Comentario} de tipo "ACTA_GESTOR" de una evaluación.
   * 
   * @param id           Id de {@link Evaluacion}.
   * @param idComentario Identificador de {@link Comentario}.
   */
  @DeleteMapping("/{id}/comentario-acta-gestor/{idComentario}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-INV-ER', 'ETI-ACT-ER', 'ETI-ACT-E')")
  public void deleteComentarioActaGestor(@PathVariable Long id, @PathVariable Long idComentario) {
    log.debug("deleteComentarioEvaluacion(Long id,  Long idComentario) - start");
    comentarioService.deleteComentarioActaGestor(id, idComentario);
    log.debug("deleteComentarioEvaluacion(Long id,  Long idComentario) - end");
  }

  /**
   * Elimina un {@link Comentario} de tipo "ACTA_EVALUADOR" de una evaluación.
   * 
   * @param id            Id de {@link Evaluacion}.
   * @param idComentario  Identificador de {@link Comentario}.
   * @param authorization autenticación
   */
  @DeleteMapping("/{id}/comentario-acta-evaluador/{idComentario}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-INV-ER', 'ETI-ACT-ER', 'ETI-ACT-E')")
  public void deleteComentarioActaEvaluador(@PathVariable Long id, @PathVariable Long idComentario,
      Authentication authorization) {
    log.debug("deleteComentarioActaEvaluador(Long id,  Long idComentario, Authentication authorization) - start");
    String personaRef = authorization.getName();
    comentarioService.deleteComentarioActaEvaluador(id, idComentario, personaRef);
    log.debug("deleteComentarioActaEvaluador(Long id,  Long idComentario, Authentication authorization) - end");
  }

  /**
   * Obtiene un listado de {@link Evaluacion} con un * determinados tipos de
   * seguimiento final
   * 
   * @param query    filtro de búsqueda.
   * @param pageable pageable
   * @return Lista paginada de evaluaciones
   */

  @GetMapping("/memorias-seguimiento-final")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-EVAL')")
  public ResponseEntity<Page<Evaluacion>> findByEvaluacionesEnSeguimientoAnualOrFinal(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findByEvaluacionesEnSeguimientoAnualOrFinal(String query, Pageable pageable) - start");
    Page<Evaluacion> page = service.findByEvaluacionesEnSeguimientoAnualOrFinal(query, pageable);
    log.debug("findByEvaluacionesEnSeguimientoAnualOrFinal(String query, Pageable pageable) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene el número de {@link Comentario} de la evaluación
   * 
   * @param id id {@link Evaluacion}
   * @return Número de comentarios
   */
  @GetMapping("/{id}/numero-comentarios")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-CNV-E')")
  public ResponseEntity<Integer> numComentariosEvaluacion(@PathVariable Long id) {
    log.debug("countComentariosEvaluacion(@PathVariable Long id) - start");
    int countComentarios = comentarioService.countByEvaluacionId(id);
    return new ResponseEntity<>(countComentarios, HttpStatus.OK);
  }

  /**
   * Obtiene el número de {@link Comentario} de la evaluación de un determinado
   * tipo
   * 
   * @param idEvaluacion     id {@link Evaluacion}
   * @param idTipoComentario id {@link TipoComentario}
   * @return Número de comentarios
   */
  @GetMapping("/{idEvaluacion}/{idTipoComentario}/numero-comentarios")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-INV-EVALR', 'ETI-EVC-EVALR')")
  public ResponseEntity<Integer> countByEvaluacionIdAndTipoComentarioId(@PathVariable Long idEvaluacion,
      @PathVariable Long idTipoComentario) {
    log.debug("countByEvaluacionIdAndTipoComentarioId(@PathVariable Long idEvaluacion, idTipoComentario) - start");
    int countComentarios = comentarioService.countByEvaluacionIdAndTipoComentarioId(idEvaluacion, idTipoComentario);
    log.debug("countByEvaluacionIdAndTipoComentarioId(@PathVariable Long idEvaluacion, idTipoComentario) - end");
    return new ResponseEntity<>(countComentarios, HttpStatus.OK);
  }

  /**
   * Obtiene el id del presidente de la evaluación
   * 
   * @param idEvaluacion id {@link Evaluacion}
   * @return Id del presidente
   */
  @GetMapping("/{idEvaluacion}/presidente")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL','ETI-MEM-INV-ERTR')")
  public ResponseEntity<String> findIdPresidenteByIdEvaluacion(@PathVariable Long idEvaluacion) {
    log.debug("findIdPresidenteByIdEvaluacion(@PathVariable Long idEvaluacion) - start");
    String presidente = service.findIdPresidenteByIdEvaluacion(idEvaluacion);
    log.debug("findIdPresidenteByIdEvaluacion(@PathVariable Long idEvaluacion) - end");
    return new ResponseEntity<>(presidente, HttpStatus.OK);
  }

  /**
   * Retorna la primera fecha de envío a secretaría (histórico estado)
   * 
   * @param idEvaluacion Id de {@link Evaluacion}.
   * @return fecha de envío a secretaría
   */
  @GetMapping("/{idEvaluacion}/primera-fecha-envio-secretaria")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-INV-EVALR')")
  ResponseEntity<Instant> findFirstFechaEnvioSecretariaByIdEvaluacion(@PathVariable Long idEvaluacion) {
    log.debug("findFirstFechaEnvioSecretariaByIdEvaluacion(@PathVariable Long idEvaluacion) - start");
    Instant fechaEnvioSecretaria = service.findFirstFechaEnvioSecretariaByIdEvaluacion(idEvaluacion);
    log.debug("findFirstFechaEnvioSecretariaByIdEvaluacion(@PathVariable Long idEvaluacion) - end");
    return new ResponseEntity<>(fechaEnvioSecretaria, HttpStatus.OK);
  }

  /**
   * Obtiene el documento de la ficha del Evaluador
   * 
   * @param idEvaluacion Id de {@link Evaluacion}.
   * @return el documento del evaluador
   */
  @GetMapping("/{idEvaluacion}/documento-evaluador")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-INV-EVALR', 'ETI-EVC-EVALR')")
  public ResponseEntity<DocumentoOutput> documentoEvaluador(@PathVariable Long idEvaluacion) {
    log.debug("documentoEvaluador(@PathVariable Long idEvaluacion) - start");
    DocumentoOutput documento = service.generarDocumentoEvaluador(idEvaluacion);
    log.debug("documentoEvaluador(@PathVariable Long idEvaluacion) - end");
    return new ResponseEntity<>(documento, HttpStatus.OK);
  }

  /**
   * Obtiene el documento de evaluación o favorable
   * 
   * @param idEvaluacion Id de {@link Evaluacion}.
   * @return el documento del evaluador
   */
  @GetMapping("/{idEvaluacion}/documento-evaluacion")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL','ETI-EVC-INV-EVALR','ETI-EVC-EVALR')")
  public ResponseEntity<DocumentoOutput> documentoEvaluacion(@PathVariable Long idEvaluacion) {
    log.debug("documentoEvaluacion(@PathVariable Long idEvaluacion) - start");
    DocumentoOutput documento = service.generarDocumentoEvaluacion(idEvaluacion);
    log.debug("documentoEvaluacion(@PathVariable Long idEvaluacion) - end");
    return new ResponseEntity<>(documento, HttpStatus.OK);
  }

  /**
   * Comprueba si el usuario es Evaluador de la {@link Evaluacion}
   * 
   * @param id            identificador de la {@link Evaluacion}
   * @param authorization authorization
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/evaluacion", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-INV-VR', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<Void> isEvaluacionEvaluable(@PathVariable Long id, Authentication authorization) {
    log.debug("isEvaluacionEvaluable(Long id, Authentication authorization) - start");
    String personaRef = authorization.getName();
    if (service.isEvaluacionEvaluableByEvaluador(id, personaRef).booleanValue()) {
      log.debug("isEvaluacionEvaluable(Long id, Authentication authorization) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("isEvaluacionEvaluable(Long id, Authentication authorization) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si el usuario es Evaluador de la {@link Evaluacion} en Seguimiento
   * 
   * @param id            identificador de la {@link Evaluacion} en Seguimiento
   * @param authorization authorization
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/evaluacion-seguimiento", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-INV-VR', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<Void> isSeguimientoEvaluable(@PathVariable Long id, Authentication authorization) {
    log.debug("isSeguimientoEvaluable(Long id, Authentication authorization) - start");
    String personaRef = authorization.getName();
    if (service.isEvaluacionSeguimientoEvaluableByEvaluador(id, personaRef).booleanValue()) {
      log.debug("isSeguimientoEvaluable(Long id, Authentication authorization) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("isSeguimientoEvaluable(Long id, Authentication authorization) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el secretario activo según la fecha de evaluación
   * 
   * @param idEvaluacion Id de {@link Evaluacion}.
   * @return el secretario de la evaluación
   */
  @GetMapping("/{idEvaluacion}/secretario-evaluacion")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-INV-EVALR')")
  ResponseEntity<Evaluador> findSecretarioEvaluacion(@PathVariable Long idEvaluacion) {
    log.debug("findSecretarioEvaluacion(@PathVariable Long idEvaluacion) - start");
    Evaluador secretario = service.findSecretarioEvaluacion(idEvaluacion);
    log.debug("findSecretarioEvaluacion(@PathVariable Long idEvaluacion) - end");
    return new ResponseEntity<>(secretario, HttpStatus.OK);
  }

  /**
   * Devuelve los dictamenes que se pueden asignar a la evaluacion
   * 
   * @param id identificador de la {@link Evaluacion}
   * @return el listado de {@link Dictamen}
   */
  @GetMapping("{id}/dictamenes")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-INV-VR', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<Page<Dictamen>> findAllDictamenEvaluacion(@PathVariable Long id) {
    log.debug("findAllDictamenEvaluacion(Long id, Pageable pageable) - start");
    List<Dictamen> dictamenes = dictamenService.findAllDictamenByEvaluacionId(id);
    log.debug("findAllDictamenEvaluacion(Long id, Pageable pageable) - end");
    return new ResponseEntity<>(new PageImpl<>(dictamenes), HttpStatus.OK);
  }

  /**
   * Informa si los {@link Comentario} han sido enviados por el usuario
   * {@link Evaluador}
   *
   * @param id            Id de {@link Evaluacion}.
   * @param authorization autenticación
   * @return HTTP-200 Si se han enviado / HTTP-204 Si no se han enviado
   */
  @RequestMapping(path = "/{id}/comentarios-evaluador-enviados", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<Void> isComentariosEvaluadorEnviados(@PathVariable Long id, Authentication authorization) {
    log.debug("isComentariosEvaluadorEnviados(Long id, Authentication authorization) - start");
    String personaRef = authorization.getName();
    if (comentarioService.isComentariosEvaluadorEnviados(id, personaRef)) {
      log.debug("isComentariosEvaluadorEnviados(Long id, Authentication authorization) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("isComentariosEvaluadorEnviados(Long id, Authentication authorization) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Indica si es posible enviar {@link Comentario} por el usuario
   *
   * @param id            Id de {@link Evaluacion}.
   * @param authorization autenticación
   * @return HTTP-200 Si es posible enviar / HTTP-204 Si no es posible enviar
   */
  @RequestMapping(path = "/{id}/posible-enviar-comentarios", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<Void> isPosibleEnviarComentarios(@PathVariable Long id,
      Authentication authorization) {
    log.debug("isPosibleEnviarComentarios(Long id, Authentication authorization) - start");
    String personaRef = authorization.getName();
    if (comentarioService.isPosibleEnviarComentarios(id, personaRef)) {
      log.debug("isPosibleEnviarComentarios(Long id, Authentication authorization) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("isPosibleEnviarComentarios(Long id, Authentication authorization) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Permite enviar los comentarios de
   * {@link Evaluacion}
   * 
   * @param id            Id del {@link Evaluacion}.
   * @param authorization autenticación
   * @return HTTP-200 Si se puede enviar / HTTP-204 Si no se puede enviar
   */
  @RequestMapping(path = "/{id}/enviar-comentarios", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-ECOMR','ETI-EVC-INV-ECOMR')")
  public ResponseEntity<Void> enviarComentarios(@PathVariable Long id, Authentication authorization) {
    log.debug("enviarComentarios(Long id) - start");
    String personaRef = authorization.getName();
    Boolean returnValue = comentarioService.enviarByEvaluacion(id, personaRef);
    log.debug("enviarComentarios(Long id) - end");
    return Boolean.TRUE.equals(returnValue) ? new ResponseEntity<>(HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Obtener todas las entidades {@link Comentario} activas para una
   * determinada {@link Evaluacion}.
   *
   * @param id         Id de {@link Evaluacion}.
   * @param personaRef identificador persona
   * @return la lista de entidades {@link Comentario}.
   */
  @GetMapping("/{id}/comentarios-evaluador/{personaRef}/persona")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<List<Comentario>> getComentariosEvaluadorByPersonaRef(@PathVariable Long id,
      @PathVariable String personaRef) {
    log.debug("getComentariosEvaluadorByPersonaRef(Long id, String personaRef) - start");
    List<Comentario> comentariosPersonaEvaluador = comentarioService.findComentariosEvaluadorByPersonaRef(id,
        personaRef);
    log.debug("getComentariosEvaluadorByPersonaRef(Long id,String personaRef) - end");
    if (comentariosPersonaEvaluador.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(comentariosPersonaEvaluador, HttpStatus.OK);
  }

  /**
   * Obtener todas las entidades paginadas {@link Comentario} activas para una
   * determinada {@link Evaluacion}. y con estado CERRADO
   *
   * @param id       Id de {@link Evaluacion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Comentario} paginadas.
   */
  @GetMapping("/{id}/comentarios-evaluador-cerrados")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<Page<Comentario>> getComentariosEvaluadorCerrados(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getComentariosEvaluadorCerrados(Long id, Pageable pageable) - start");
    Page<Comentario> page = comentarioService.findByEvaluacionEvaluadorAndEstadoCerrado(id, pageable);
    log.debug("getComentariosEvaluadorCerrados(Long id, Pageable pageable) - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene el número de {@link Comentario} de la evaluación de un determinado
   * tipo y con estado CERRADO
   * 
   * @param idEvaluacion     id {@link Evaluacion}
   * @param idTipoComentario id {@link TipoComentario}
   * @return Número de comentarios
   */
  @GetMapping("/{idEvaluacion}/numero-comentarios-cerrados-tipo/{idTipoComentario}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-INV-EVALR', 'ETI-EVC-EVALR')")
  public ResponseEntity<Integer> countByEvaluacionIdAndTipoComentarioIdAndEstadoCerrado(@PathVariable Long idEvaluacion,
      @PathVariable Long idTipoComentario) {
    log.debug("countByEvaluacionIdAndTipoComentarioId(@PathVariable Long idEvaluacion, idTipoComentario) - start");
    int countComentarios = comentarioService.countByEvaluacionIdAndTipoComentarioIdAndEstadoCerrado(idEvaluacion,
        idTipoComentario);
    log.debug("countByEvaluacionIdAndTipoComentarioId(@PathVariable Long idEvaluacion, idTipoComentario) - end");
    return new ResponseEntity<>(countComentarios, HttpStatus.OK);
  }

  /**
   * Obtener todas las entidades {@link Comentario} activas para una
   * determinada {@link Evaluacion}.
   *
   * @param id         Id de {@link Evaluacion}.
   * @param personaRef identificador persona
   * @return la lista de entidades {@link Comentario}.
   */
  @GetMapping("/{id}/comentarios-acta-evaluador/{personaRef}/persona")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-INV-ER', 'ETI-ACT-ER')")
  public ResponseEntity<List<Comentario>> getComentariosActaByPersonaRef(@PathVariable Long id,
      @PathVariable String personaRef) {
    log.debug("getComentariosActaByPersonaRef(Long id, String personaRef) - start");
    List<Comentario> comentariosPersonaActas = comentarioService.findComentariosActaByPersonaRef(id,
        personaRef);
    log.debug("getComentariosActaByPersonaRef(Long id,String personaRef) - end");
    if (comentariosPersonaActas.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(comentariosPersonaActas, HttpStatus.OK);
  }

}
