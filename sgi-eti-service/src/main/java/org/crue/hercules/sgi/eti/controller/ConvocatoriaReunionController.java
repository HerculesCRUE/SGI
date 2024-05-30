package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.dto.ConvocatoriaReunionDatosGenerales;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Asistentes;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.DocumentacionConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.DocumentacionConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.service.ActaService;
import org.crue.hercules.sgi.eti.service.AsistentesService;
import org.crue.hercules.sgi.eti.service.ConvocatoriaReunionService;
import org.crue.hercules.sgi.eti.service.DocumentacionConvocatoriaReunionService;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
import org.crue.hercules.sgi.framework.exception.NotFoundException;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaReunionController
 */
@RestController
@RequestMapping("/convocatoriareuniones")
@Slf4j
public class ConvocatoriaReunionController {

  /**
   * Asistentes service
   */
  private AsistentesService asistenteService;

  /**
   * Evaluacion service
   */
  private EvaluacionService evaluacionService;

  /**
   * ConvocatoriaReunion service
   */
  private ConvocatoriaReunionService convocatoriaReunionService;

  /**
   * Acta service
   */
  private ActaService actaService;

  /**
   * DocumentacionConvocatoriaReunion service
   */
  private DocumentacionConvocatoriaReunionService documentacionConvocatoriaReunionService;

  /**
   * Instancia un nuevo ConvocatoriaReunionController.
   *
   * @param asistenteService                        {@link AsistentesService}
   * @param evaluacionService                       {@link EvaluacionService}
   * @param convocatoriaReunionService              {@link ConvocatoriaReunionService}
   * @param actaService                             {@link ActaService}
   * @param documentacionConvocatoriaReunionService {@link DocumentacionConvocatoriaReunionService}
   */
  public ConvocatoriaReunionController(AsistentesService asistenteService, EvaluacionService evaluacionService,
      ConvocatoriaReunionService convocatoriaReunionService, ActaService actaService,
      DocumentacionConvocatoriaReunionService documentacionConvocatoriaReunionService) {
    log.debug("ConvocatoriaReunionController(ConvocatoriaReunionService service) - start");
    this.convocatoriaReunionService = convocatoriaReunionService;
    this.asistenteService = asistenteService;
    this.evaluacionService = evaluacionService;
    this.actaService = actaService;
    this.documentacionConvocatoriaReunionService = documentacionConvocatoriaReunionService;
    log.debug("ConvocatoriaReunionController(ConvocatoriaReunionService service) - end");
  }

  /**
   * Crea {@link ConvocatoriaReunion}.
   *
   * @param convocatoriaReunion La entidad {@link ConvocatoriaReunion} a crear.
   * @return Nuevo {@link ConvocatoriaReunion} creado.
   * @throws IllegalArgumentException Si la entidad {@link ConvocatoriaReunion}
   *                                  tiene id.
   */
  @PostMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-C')")
  public ResponseEntity<ConvocatoriaReunion> newConvocatoriaReunion(
      @Valid @RequestBody ConvocatoriaReunion convocatoriaReunion) {
    log.debug("newConvocatoriaReunion(ConvocatoriaReunion convocatoriaReunion) - start");
    ConvocatoriaReunion returnValue = convocatoriaReunionService.create(convocatoriaReunion);
    log.debug("newConvocatoriaReunion(ConvocatoriaReunion convocatoriaReunion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza la entidad {@link ConvocatoriaReunion}.
   *
   * @param convocatoriaReunion La entidad {@link ConvocatoriaReunion} a
   *                            actualizar.
   * @param id                  Identificador de la entidad
   *                            {@link ConvocatoriaReunion}.
   * @return Entidad {@link ConvocatoriaReunion} actualizada.
   * @throws NotFoundException        Si no existe ninguna entidad
   *                                  {@link ConvocatoriaReunion} con ese id.
   * @throws IllegalArgumentException Si la entidad {@link ConvocatoriaReunion} no
   *                                  tiene id.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-E')")
  public ConvocatoriaReunion replaceConvocatoriaReunion(@Valid @RequestBody ConvocatoriaReunion convocatoriaReunion,
      @PathVariable Long id) {
    log.debug("replaceConvocatoriaReunion(ConvocatoriaReunion convocatoriaReunion, Long id) - start");
    convocatoriaReunion.setId(id);
    ConvocatoriaReunion returnValue = convocatoriaReunionService.update(convocatoriaReunion);
    log.debug("replaceConvocatoriaReunion(ConvocatoriaReunion convocatoriaReunion, Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza {@link ConvocatoriaReunion} con el indicador de activo a false.
   *
   * @param id El id de la entidad {@link ConvocatoriaReunion}.
   * @throws NotFoundException        Si no existe ninguna entidad
   *                                  {@link ConvocatoriaReunion} con ese id.
   * @throws IllegalArgumentException Si la entidad {@link ConvocatoriaReunion} no
   *                                  tiene id.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    ConvocatoriaReunion convocatoriaReunion = this.one(id);
    convocatoriaReunion.setActivo(Boolean.FALSE);
    convocatoriaReunionService.update(convocatoriaReunion);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las entidades {@link ConvocatoriaReunion} filtradas y paginadas según
   * los criterios de búsqueda.
   *
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link ConvocatoriaReunion} paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-C', 'ETI-CNV-V')")
  public ResponseEntity<Page<ConvocatoriaReunion>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging - start");
    Page<ConvocatoriaReunion> page = convocatoriaReunionService.findAll(query, paging);
    log.debug("findAll(String query, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene la entidad {@link ConvocatoriaReunion} por id.
   *
   * @param id El id de la entidad {@link ConvocatoriaReunion}.
   * @return La entidad {@link ConvocatoriaReunion}.
   * @throws NotFoundException        Si no existe ninguna entidad
   *                                  {@link ConvocatoriaReunion} con ese id.
   * @throws IllegalArgumentException Si no se informa id.
   */
  @GetMapping("/{id}")
  public ConvocatoriaReunion one(@PathVariable Long id) {
    log.debug("one(Long id) - start");
    ConvocatoriaReunion returnValue = convocatoriaReunionService.findById(id);
    log.debug("one(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene la entidad {@link ConvocatoriaReunionDatosGenerales} por id con el
   * número de evaluaciones activas que no son de revisión mínima.
   *
   * @param id El id de la entidad {@link ConvocatoriaReunionDatosGenerales}.
   * @return La entidad {@link ConvocatoriaReunionDatosGenerales}.
   * @throws NotFoundException        Si no existe ninguna entidad
   *                                  {@link ConvocatoriaReunionDatosGenerales}
   *                                  con ese id.
   * @throws IllegalArgumentException Si no se informa id.
   */
  @GetMapping("/{id}/datos-generales")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-V', 'ETI-CNV-E')")
  public ConvocatoriaReunionDatosGenerales oneWithDatosGenerales(@PathVariable Long id) {
    log.debug("oneWithDatosGenerales(Long id) - start");
    ConvocatoriaReunionDatosGenerales returnValue = convocatoriaReunionService.findByIdWithDatosGenerales(id);
    log.debug("oneWithDatosGenerales(Long id) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades paginadas {@link Asistentes} activas para una
   * determinada {@link ConvocatoriaReunion}.
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Asistentes} paginadas.
   */
  @GetMapping("/{id}/asistentes")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-C','ETI-ACT-E','ETI-ACT-INV-ER','ETI-ACT-ER')")
  public ResponseEntity<Page<Asistentes>> findAsistentes(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findAsistentes(Long id, Pageable pageable) - start");
    Page<Asistentes> page = asistenteService.findAllByConvocatoriaReunionId(id, pageable);

    if (page.isEmpty()) {
      log.debug("findAsistentes(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAsistentes(Long id, Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} para una
   * determinada {@link ConvocatoriaReunion}.
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @GetMapping("/{id}/evaluaciones/todas")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-C','ETI-ACT-E','ETI-ACT-INV-ER','ETI-ACT-ER')")
  public ResponseEntity<Page<Evaluacion>> findEvaluaciones(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findEvaluaciones(Long id, Pageable pageable) - start");
    Page<Evaluacion> page = evaluacionService.findAllByConvocatoriaReunionId(id, pageable);

    if (page.isEmpty()) {
      log.debug("findEvaluaciones(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findEvaluaciones(Long id, Pageable pageable) - end");
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
  @GetMapping("/{id}/evaluaciones-activas")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-C','ETI-ACT-E','ETI-ACT-INV-ER','ETI-ACT-ER')")
  public ResponseEntity<Page<Evaluacion>> findEvaluacionesActivas(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findEvaluacionesActivas(Long id, Pageable pageable) - start");
    Page<Evaluacion> page = evaluacionService.findAllActivasByConvocatoriaReunionId(id, pageable);

    if (page.isEmpty()) {
      log.debug("findEvaluacionesActivas(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findEvaluacionesActivas(Long id, Pageable pageable) - end");
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
  @GetMapping("/{id}/evaluaciones")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V')")
  public ResponseEntity<Page<Evaluacion>> getEvaluaciones(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findEvaluaciones(Long id, Pageable pageable) - start");
    Page<Evaluacion> page = evaluacionService.findAllActivasByConvocatoriaReunionId(id, pageable);
    log.debug("findEvaluaciones(Long id, Pageable pageable) - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Eliminar la memoria del listado de memorias a evaluar en una convocatoria
   * 
   * @param idConvocatoriaReunion Id de la convocatoria
   * @param idEvaluacion          Id de la evaluación
   */
  @DeleteMapping("/{idConvocatoriaReunion}/evaluacion/{idEvaluacion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-C', 'ETI-CNV-E')")
  public void deleteEvaluacion(@PathVariable Long idConvocatoriaReunion, @PathVariable Long idEvaluacion) {
    log.debug("deleteMemoria(Long idConvocatoriaReunion, Long idEvaluacion, Long idMemoria) - start");

    evaluacionService.deleteEvaluacion(idConvocatoriaReunion, idEvaluacion);

    log.debug("deleteMemoria(Long idConvocatoriaReunion, Long idEvaluacion, Long idMemoria) - end");

  }

  /**
   * Devuelve una lista de entidad {@link ConvocatoriaReunion} que no tengan acta
   * asociada y se encuentren activas
   * 
   * @return la lista de {@link ConvocatoriaReunion}
   */
  @GetMapping("/acta-no-asignada")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-C','ETI-ACT-E','ETI-ACT-INV-ER','ETI-ACT-ER')")
  public ResponseEntity<List<ConvocatoriaReunion>> findConvocatoriasSinActa() {
    log.debug("findConvocatoriasSinActa() - start");
    List<ConvocatoriaReunion> result = convocatoriaReunionService.findConvocatoriasSinActa();

    if (result.isEmpty()) {
      log.debug("findConvocatoriasSinActa() - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findConvocatoriasSinActa() - end");
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ConvocatoriaReunion} puede ser eliminada.
   * 
   * @param id Id del {@link ConvocatoriaReunion}.
   * @return HTTP-200 Si se permite eliminar / HTTP-204 Si no se permite eliminar
   */
  @RequestMapping(path = "/{id}/eliminable", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-C', 'ETI-CNV-V')")
  public ResponseEntity<ConvocatoriaReunion> eliminable(@PathVariable Long id) {
    log.debug("eliminable(Long id) - start");
    Boolean returnValue = convocatoriaReunionService.eliminable(id);
    log.debug("eliminable(Long id) - end");
    return Boolean.TRUE.equals(returnValue) ? new ResponseEntity<>(HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ConvocatoriaReunion} puede ser modificada.
   * 
   * @param id Id del {@link ConvocatoriaReunion}.
   * @return HTTP-200 Si se permite modificación / HTTP-204 Si no se permite
   *         modificación
   */
  @RequestMapping(path = "/{id}/modificable", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-V', 'ETI-CNV-E')")
  public ResponseEntity<ConvocatoriaReunion> modificable(@PathVariable Long id) {
    log.debug("modificable(Long id) - start");
    Boolean returnValue = convocatoriaReunionService.modificable(id);
    log.debug("modificable(Long id) - end");
    return Boolean.TRUE.equals(returnValue) ? new ResponseEntity<>(HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Obtiene el {@link Acta} asociada a la {@link ConvocatoriaReunion}
   * 
   * @param id Id del {@link ConvocatoriaReunion}.
   * @return la entidad {@link Acta} asociada ala {@link ConvocatoriaReunion}
   */
  @GetMapping(path = "/{id}/acta")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-V','ETI-ACT-V', 'ETI-PEV-INV-VR')")
  public ResponseEntity<Acta> actaConvocatoriaReunion(@PathVariable Long id) {
    log.debug("actaConvocatoriaReunion(Long id) - start");
    Acta returnValue = actaService.findByConvocatoriaReunionId(id);
    log.debug("actaConvocatoriaReunion(Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Retorna la fecha convocatoria y acta (codigo convocatoria) de la última
   * evaluación de tipo memoria de la memoria original (y que no sea revisión
   * mínima)
   * 
   * @param idEvaluacion Id de la {@link Evaluacion}.
   * @param idDictamen   Id del {@link Dictamen}.
   * @return {@link ConvocatoriaReunion}
   */
  @GetMapping(path = "/{idEvaluacion}/{idDictamen}/convocatoria-ultima-evaluacion")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<ConvocatoriaReunion> findConvocatoriaUltimaEvaluacionTipoMemoria(
      @PathVariable Long idEvaluacion, @PathVariable Long idDictamen) {
    log.debug("findConvocatoriaUltimaEvaluacionTipoMemoria(Long idEvaluacion, idDictamen) - start");
    ConvocatoriaReunion returnValue = convocatoriaReunionService
        .findConvocatoriaUltimaEvaluacionTipoMemoria(idEvaluacion, idDictamen);
    log.debug("findConvocatoriaUltimaEvaluacionTipoMemoria(Long idEvaluacion, idDictamen) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Permite enviar el comunicado de
   * {@link ConvocatoriaReunion}
   * 
   * @param id Id del {@link ConvocatoriaReunion}.
   * @return HTTP-200 Si se puede enviar / HTTP-204 Si no se puede enviar
   */
  @RequestMapping(path = "/{id}/comunicado", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-C', 'ETI-CNV-V')")
  public ResponseEntity<ConvocatoriaReunion> enviarComunicado(@PathVariable Long id) {
    log.debug("enviarComunicado(Long id) - start");
    Boolean returnValue = convocatoriaReunionService.enviarComunicado(id);
    log.debug("enviarComunicado(Long id) - end");
    return Boolean.TRUE.equals(returnValue) ? new ResponseEntity<>(HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Obtener todas las entidades paginadas
   * {@link DocumentacionConvocatoriaReunion} activas
   * para una determinada {@link ConvocatoriaReunion}
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link DocumentacionConvocatoriaReunion}
   *         paginadas.
   */
  @GetMapping("/{id}/documentaciones")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-INV-VR', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR', 'ETI-MEM-EDOC')")
  ResponseEntity<Page<DocumentacionConvocatoriaReunion>> getDocumentaciones(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getDocumentaciones(Long id, Long idTipoEvaluacion, Pageable pageable) - start");
    Page<DocumentacionConvocatoriaReunion> page = documentacionConvocatoriaReunionService
        .findDocumentacionConvocatoriaReunion(id, pageable);

    if (page.isEmpty()) {
      log.debug("getDocumentaciones(Long id, Long idTipoEvaluacion, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("getDocumentaciones(Long id, Long idTipoEvaluacion, Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nueva {@link DocumentacionConvocatoriaReunion}.
   * 
   * @param id                               Identificador de la
   *                                         {@link ConvocatoriaReunion}.
   * @param documentacionConvocatoriaReunion {@link DocumentacionConvocatoriaReunion}.
   *                                         que se quiere crear.
   * @return Nueva {@link DocumentacionConvocatoriaReunion} creada.
   */
  @PostMapping("/{id}/documentacion")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-C', 'ETI-CNV-E')")
  public ResponseEntity<DocumentacionConvocatoriaReunion> newDocumentacionConvocatoriaReunion(
      @PathVariable Long id,
      @Valid @RequestBody DocumentacionConvocatoriaReunion documentacionConvocatoriaReunion) {
    log.debug(
        "newDocumentacionConvocatoriaReunion(Long id, DocumentacionConvocatoriaReunion documentacionConvocatoriaReunion) - start");
    DocumentacionConvocatoriaReunion returnValue = documentacionConvocatoriaReunionService.createDocumentacion(
        id,
        documentacionConvocatoriaReunion);
    log.debug(
        "newDocumentacionConvocatoriaReunion(Long id, DocumentacionConvocatoriaReunion documentacionConvocatoriaReunion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link DocumentacionConvocatoriaReunion}.
   * 
   * @param id                               Identificador de la
   *                                         {@link ConvocatoriaReunion}.
   * @param documentacionConvocatoriaReunion {@link DocumentacionConvocatoriaReunion}.
   *                                         que se quiere crear.
   * @return Nueva {@link DocumentacionConvocatoriaReunion} creada.
   */
  @PutMapping("/{id}/documentacion/{idDocumentacionConvocatoriaReunion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-E')")
  public ResponseEntity<DocumentacionConvocatoriaReunion> updateDocumentacionConvocatoriaReunion(
      @PathVariable Long id, @PathVariable Long idDocumentacionConvocatoriaReunion,
      @Valid @RequestBody DocumentacionConvocatoriaReunion documentacionConvocatoriaReunion) {
    log.debug(
        "newDocumentacionConvocatoriaReunion(Long id, DocumentacionConvocatoriaReunion documentacionConvocatoriaReunion) - start");
    DocumentacionConvocatoriaReunion returnValue = documentacionConvocatoriaReunionService.updateDocumentacion(
        id,
        idDocumentacionConvocatoriaReunion,
        documentacionConvocatoriaReunion);
    log.debug(
        "newDocumentacionConvocatoriaReunion(Long id, DocumentacionConvocatoriaReunion documentacionConvocatoriaReunion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Elimina la {@link DocumentacionConvocatoriaReunion} del tipo retrospectiva.
   * 
   * @param id                                 Id {@link Memoria}.
   * @param idDocumentacionConvocatoriaReunion id
   *                                           {@link DocumentacionConvocatoriaReunion}
   *                                           a eliminar.
   * @param authentication                     Authentication
   */
  @DeleteMapping("/{id}/documentacion/{idDocumentacionConvocatoriaReunion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-E')")
  void deleteDocumentacion(@PathVariable Long id, @PathVariable Long idDocumentacionConvocatoriaReunion,
      Authentication authentication) {
    log.debug("deleteDocumentacion(Long id, Long idDocumentacionConvocatoriaReunion) - start");
    documentacionConvocatoriaReunionService.deleteDocumentacion(id, idDocumentacionConvocatoriaReunion, authentication);
    log.debug("deleteDocumentacion(Long id, Long idDocumentacionConvocatoriaReunion) - end");
  }

}
