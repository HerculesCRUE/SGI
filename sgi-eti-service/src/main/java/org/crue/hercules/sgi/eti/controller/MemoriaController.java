package org.crue.hercules.sgi.eti.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.BaseEntity.Update;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.repository.custom.CustomConvocatoriaReunionRepository;
import org.crue.hercules.sgi.eti.service.DocumentacionMemoriaService;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
import org.crue.hercules.sgi.eti.service.InformeService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.service.RespuestaService;
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
 * MemoriaController
 */
@RestController
@RequestMapping("/memorias")
@Slf4j
public class MemoriaController {

  /** Memoria service */
  private final MemoriaService service;

  /** Evaluacion service */
  private final EvaluacionService evaluacionService;

  /** DocumentacionMemoria service */
  private final DocumentacionMemoriaService documentacionMemoriaService;

  /** Informe service */
  private final InformeService informeService;

  /** ConvocatoriaReunion repository */
  private final CustomConvocatoriaReunionRepository convocatoriaReunionRepository;

  /** Respuesta repository */
  private final RespuestaService respuestaService;

  /**
   * Instancia un nuevo MemoriaController.
   * 
   * @param service                       {@link MemoriaService}
   * @param evaluacionService             {@link EvaluacionService}
   * @param documentacionMemoriaService   {@link DocumentacionMemoriaService}
   * @param informeService                {@link InformeService}
   * @param convocatoriaReunionRepository {@link CustomConvocatoriaReunionRepository}
   * @param respuestaService              {@link RespuestaService}
   */
  public MemoriaController(MemoriaService service, EvaluacionService evaluacionService,
      DocumentacionMemoriaService documentacionMemoriaService, InformeService informeService,
      CustomConvocatoriaReunionRepository convocatoriaReunionRepository, RespuestaService respuestaService) {
    log.debug(
        "MemoriaController(MemoriaService service, EvaluacionService evaluacionService, DocumentacionMemoriaService documentacionMemoriaService, InformeService informeService, CustomConvocatoriaReunionRepository convocatoriaReunionRepository, RespuestaService respuestaService) - start");
    this.service = service;
    this.evaluacionService = evaluacionService;
    this.documentacionMemoriaService = documentacionMemoriaService;
    this.informeService = informeService;
    this.convocatoriaReunionRepository = convocatoriaReunionRepository;
    this.respuestaService = respuestaService;
    log.debug(
        "MemoriaController(MemoriaService service, EvaluacionService evaluacionService, DocumentacionMemoriaService documentacionMemoriaService, InformeService informeService, CustomConvocatoriaReunionRepository convocatoriaReunionRepository, RespuestaService respuestaService) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Memoria}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-VR-INV', 'ETI-PEV-V', 'ETI-PEV-E')")
  ResponseEntity<Page<MemoriaPeticionEvaluacion>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<MemoriaPeticionEvaluacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link Memoria} asignables para una
   * convocatoria determinada
   * 
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param idConvocatoria identificador de la {@link ConvocatoriaReunion}.
   */
  @GetMapping("/asignables/{idConvocatoria}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-E')")
  ResponseEntity<List<Memoria>> findAllMemoriasAsignablesConvocatoria(@PathVariable Long idConvocatoria) {
    log.debug("findAll(Long idConvocatoria) - start");
    List<Memoria> result = service.findAllMemoriasAsignablesConvocatoria(idConvocatoria);

    if (result.isEmpty()) {
      log.debug("findAll(String query) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query) - end");
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada con las entidades {@link Memoria}
   * asignables a una Convocatoria de tipo "Ordinaria" o "Extraordinaria".
   * 
   * Para determinar si es asignable es necesario especificar en el filtro el
   * Comité Fecha Límite de la convocatoria.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param query    filtro de búsqueda.
   * @param pageable pageable
   */
  @GetMapping("/tipo-convocatoria-ord-ext")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-C')")
  ResponseEntity<Page<Memoria>> findAllAsignablesTipoConvocatoriaOrdExt(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findAllAsignablesTipoConvocatoriaOrdExt(String query,Pageable pageable) - start");
    Page<Memoria> page = service.findAllAsignablesTipoConvocatoriaOrdExt(query, pageable);

    if (page.isEmpty()) {
      log.debug("findAllAsignablesTipoConvocatoriaOrdExt(String query,Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllAsignablesTipoConvocatoriaOrdExt(String query,Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada con las entidades {@link Memoria}
   * asignables a una Convocatoria de tipo "Seguimiento".
   * 
   * Para determinar si es asignable es necesario especificar en el filtro el
   * Comité y Fecha Límite de la convocatoria.
   * 
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * @param query    filtro de búsqueda.
   * @param pageable pageable
   */
  @GetMapping("/tipo-convocatoria-seg")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-C')")
  ResponseEntity<Page<Memoria>> findAllAsignablesTipoConvocatoriaSeguimiento(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findAllAsignablesTipoConvocatoriaSeguimiento(String query,Pageable pageable) - start");
    Page<Memoria> page = service.findAllAsignablesTipoConvocatoriaSeguimiento(query, pageable);

    if (page.isEmpty()) {
      log.debug("findAllAsignablesTipoConvocatoriaSeguimiento(String query,Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllAsignablesTipoConvocatoriaSeguimiento(String query,Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nueva {@link Memoria}.
   * 
   * @param nuevaMemoria {@link Memoria}. que se quiere crear.
   * @return Nueva {@link Memoria} creada.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-C-INV', 'ETI-PEV-ER-INV')")
  public ResponseEntity<Memoria> newMemoria(
      @Validated({ Memoria.Create.class, Default.class }) @RequestBody Memoria nuevaMemoria) {
    log.debug("newMemoria(Memoria nuevaMemoria) - start");
    Memoria returnValue = service.create(nuevaMemoria);
    log.debug("newMemoria(Memoria nuevaMemoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Crea nueva {@link Memoria} de tipo modificada.
   * 
   * @param id           Identificador de la memoria a copiar.
   * @param nuevaMemoria {@link Memoria}. que se quiere crear.
   * @return Nueva {@link Memoria} creada.
   */
  @PostMapping("/{id}/crear-memoria-modificada")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-C-INV', 'ETI-PEV-ER-INV')")
  public ResponseEntity<Memoria> newMemoriaModificada(
      @Validated({ Memoria.Create.class, Default.class }) @RequestBody Memoria nuevaMemoria, @PathVariable Long id) {
    log.debug("newMemoriaModificada(Memoria nuevaMemoria,  Long id) - start");
    Memoria returnValue = service.createModificada(nuevaMemoria, id);
    log.debug("newMemoriaModificada(Memoria nuevaMemoria,  Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Memoria}.
   * 
   * @param updatedMemoria {@link Memoria} a actualizar.
   * @param id             id {@link Memoria} a actualizar.
   * @return {@link Memoria} actualizada.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV')")
  Memoria replaceMemoria(@Validated({ Update.class, Default.class }) @RequestBody Memoria updatedMemoria,
      @PathVariable Long id) {
    log.debug("replaceMemoria(Memoria updatedMemoria, Long id) - start");
    updatedMemoria.setId(id);
    Memoria returnValue = service.update(updatedMemoria);
    log.debug("replaceMemoria(Memoria updatedMemoria, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Memoria} con el id indicado.
   * 
   * @param id Identificador de {@link Memoria}.
   * @return {@link Memoria} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-VR-INV', 'ETI-PEV-V')")
  Memoria one(@PathVariable Long id) {
    log.debug("Memoria one(Long id) - start");
    Memoria returnValue = service.findById(id);
    log.debug("Memoria one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Memoria} con id indicado.
   * 
   * @param id Identificador de {@link Memoria}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    Memoria memoria = this.one(id);
    memoria.setActivo(Boolean.FALSE);
    service.update(memoria);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} activas para una
   * determinada {@link Memoria} anterior al id de evaluación recibido.
   *
   * @param id               Id de {@link Memoria}.
   * @param idEvaluacion     Id de la {@link Evaluacion}.
   * @param idTipoComentario Id del {@link TipoComentario}.
   * @param pageable         la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @GetMapping("/{id}/evaluaciones-anteriores/{idEvaluacion}/{idTipoComentario}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  ResponseEntity<Page<EvaluacionWithNumComentario>> getEvaluacionesAnteriores(@PathVariable Long id,
      @PathVariable Long idEvaluacion, @PathVariable Long idTipoComentario,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getEvaluaciones(Long id, Pageable pageable) - start");
    Page<EvaluacionWithNumComentario> page = evaluacionService.findEvaluacionesAnterioresByMemoria(id, idEvaluacion,
        idTipoComentario, pageable);

    if (page.isEmpty()) {
      log.debug("getEvaluaciones(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("getEvaluaciones(Long id, Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria}.
   * 
   * @param id       Id de {@link Memoria}.
   * 
   * @param pageable la información de la paginación.
   * 
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV', 'ETI-PEV-V')")
  @GetMapping("/{id}/documentacion-formulario")
  ResponseEntity<Page<DocumentacionMemoria>> getDocumentacionFormulario(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getDocumentacionFormulario(Long id, Pageable pageable) - start");
    Page<DocumentacionMemoria> page = documentacionMemoriaService.findDocumentacionMemoria(id, pageable);

    if (page.isEmpty()) {
      log.debug("getDocumentacionFormulario(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("getDocumentacionFormulario(Long id, Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria} del tipo Seguimiento Anual.
   * 
   * @param id       Id de {@link Memoria}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV', 'ETI-PEV-V')")
  @GetMapping("/{id}/documentacion-seguimiento-anual")
  ResponseEntity<Page<DocumentacionMemoria>> getDocumentacionSeguimientoAnual(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getDocumentacionSeguimientoAnual(Long id, Pageable pageable) - start");
    Page<DocumentacionMemoria> page = documentacionMemoriaService.findDocumentacionSeguimientoAnual(id, pageable);

    if (page.isEmpty()) {
      log.debug("getDocumentacionSeguimientoAnual(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("getDocumentacionSeguimientoAnual(Long id, Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria} del tipo Seguimiento Final.
   * 
   * @param id       Id de {@link Memoria}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV', 'ETI-PEV-V')")
  @GetMapping("/{id}/documentacion-seguimiento-final")
  ResponseEntity<Page<DocumentacionMemoria>> getDocumentacionSeguimientoFinal(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getDocumentacionSeguimientoFinal(Long id, Pageable pageable) - start");
    Page<DocumentacionMemoria> page = documentacionMemoriaService.findDocumentacionSeguimientoFinal(id, pageable);

    if (page.isEmpty()) {
      log.debug("getDocumentacionSeguimientoFinal(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("getDocumentacionSeguimientoFinal(Long id, Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria} del tipo Retrospectiva.
   * 
   * @param id       Id de {@link Memoria}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV', 'ETI-PEV-V')")
  @GetMapping("/{id}/documentacion-retrospectiva")
  ResponseEntity<Page<DocumentacionMemoria>> getDocumentacionRetrospectiva(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getDocumentacionRetrospectiva(Long id, Pageable pageable) - start");
    Page<DocumentacionMemoria> page = documentacionMemoriaService.findDocumentacionRetrospectiva(id, pageable);

    if (page.isEmpty()) {
      log.debug("getDocumentacionRetrospectiva(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("getDocumentacionRetrospectiva(Long id, Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtener todas las entidades paginadas {@link DocumentacionMemoria} activas
   * para una determinada {@link Memoria} según el {@link TipoEvaluacion}.
   *
   * @param id               Id de {@link Memoria}.
   * @param idTipoEvaluacion Id de {@link TipoEvaluacion}.
   * @param pageable         la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @GetMapping("/{id}/documentaciones/{idTipoEvaluacion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-VR-INV', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  ResponseEntity<Page<DocumentacionMemoria>> getDocumentacionesTipoEvaluacion(@PathVariable Long id,
      @PathVariable Long idTipoEvaluacion, @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getDocumentacionesTipoEvaluacion(Long id, Long idTipoEvaluacion, Pageable pageable) - start");
    Page<DocumentacionMemoria> page = documentacionMemoriaService.findByMemoriaIdAndTipoEvaluacion(id,
        TipoEvaluacion.Tipo.fromId(idTipoEvaluacion), pageable);

    if (page.isEmpty()) {
      log.debug("getDocumentacionesTipoEvaluacion(Long id, Long idTipoEvaluacion, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("getDocumentacionesTipoEvaluacion(Long id, Long idTipoEvaluacion, Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link Memoria}.
   * 
   * @param query          filtro de búsqueda.
   * @param paging         pageable
   * @param authentication Authentication
   * @return la lista de entidades {@link Memoria} paginadas.
   */
  @GetMapping("/persona")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-VR-INV', 'ETI-PEV-V')")
  ResponseEntity<Page<MemoriaPeticionEvaluacion>> findAllMemoriasEvaluacionByPersonaRef(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging,
      Authentication authentication) {
    log.debug("findAllMemoriasPeticionesEvaluacionByPersonaRef(String query,Pageable paging) - start");
    String personaRef = authentication.getName();

    Page<MemoriaPeticionEvaluacion> page = service
        .findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(query, paging, personaRef);

    if (page.isEmpty()) {
      log.debug("findAllMemoriasPeticionesEvaluacionByPersonaRef(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllMemoriasPeticionesEvaluacionByPersonaRef(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nueva {@link DocumentacionMemoria}.
   * 
   * @param id                   Identificador de la {@link Memoria}.
   * @param documentacionMemoria {@link DocumentacionMemoria}. que se quiere
   *                             crear.
   * @return Nueva {@link DocumentacionMemoria} creada.
   */
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV', 'ETI-PEV-V')")
  @PostMapping("/{id}/documentacion-inicial")
  public ResponseEntity<DocumentacionMemoria> newDocumentacionMemoriaInicial(@PathVariable Long id,
      @Valid @RequestBody DocumentacionMemoria documentacionMemoria) {
    log.debug("newDocumentacionMemoriaInicial(Long id, DocumentacionMemoria documentacionMemoria) - start");
    DocumentacionMemoria returnValue = documentacionMemoriaService.createDocumentacionInicial(id, documentacionMemoria);

    log.debug("newDocumentacionMemoriaInicial(Long id, DocumentacionMemoria documentacionMemoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Crea nueva {@link DocumentacionMemoria} de seguimiento anual
   * 
   * @param id                   Identificador de la {@link Memoria}.
   * @param documentacionMemoria {@link DocumentacionMemoria}. que se quiere
   *                             crear.
   * @return Nueva {@link DocumentacionMemoria} creada.
   */
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV', 'ETI-PEV-V')")
  @PostMapping("/{id}/documentacion-seguimiento-anual")
  public ResponseEntity<DocumentacionMemoria> newDocumentacionMemoriaSeguimientoAnual(@PathVariable Long id,
      @Valid @RequestBody DocumentacionMemoria documentacionMemoria) {

    log.debug("newDocumentacionMemoriaSeguimientoAnual(Long id, DocumentacionMemoria documentacionMemoria) - start");
    DocumentacionMemoria returnValue = documentacionMemoriaService.createSeguimientoAnual(id, documentacionMemoria);

    log.debug("newDocumentacionMemoriaSeguimientoAnual(Long id, DocumentacionMemoria documentacionMemoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Crea nueva {@link DocumentacionMemoria} de seguimiento final.
   * 
   * @param id                   Identificador de la {@link Memoria}.
   * @param documentacionMemoria {@link DocumentacionMemoria}. que se quiere
   *                             crear.
   * @return Nueva {@link DocumentacionMemoria} creada.
   */
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV', 'ETI-PEV-V')")
  @PostMapping("/{id}/documentacion-seguimiento-final")
  public ResponseEntity<DocumentacionMemoria> newDocumentacionMemoriaSeguimientoFinal(@PathVariable Long id,
      @Valid @RequestBody DocumentacionMemoria documentacionMemoria) {

    log.debug("newDocumentacionMemoriaSeguimientoFinal(Long id, DocumentacionMemoria documentacionMemoria) - start");
    DocumentacionMemoria returnValue = documentacionMemoriaService.createSeguimientoFinal(id, documentacionMemoria);

    log.debug("newDocumentacionMemoriaSeguimientoFinal(Long id, DocumentacionMemoria documentacionMemoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Crea nueva {@link DocumentacionMemoria} de retrospectiva.
   * 
   * @param id                   Identificador de la {@link Memoria}.
   * @param documentacionMemoria {@link DocumentacionMemoria}. que se quiere
   *                             crear.
   * @return Nueva {@link DocumentacionMemoria} creada.
   */
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV', 'ETI-PEV-V')")
  @PostMapping("/{id}/documentacion-retrospectiva")
  public ResponseEntity<DocumentacionMemoria> newDocumentacionMemoriaRetrospectiva(@PathVariable Long id,
      @Valid @RequestBody DocumentacionMemoria documentacionMemoria) {

    log.debug("newDocumentacionMemoriaRetrospectiva(Long id, DocumentacionMemoria documentacionMemoria) - start");
    DocumentacionMemoria returnValue = documentacionMemoriaService.createRetrospectiva(id, documentacionMemoria);

    log.debug("newDocumentacionMemoriaRetrospectiva(Long id, DocumentacionMemoria documentacionMemoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * * Devuelve una lista paginada y filtrada de {@link Informe} filtradas por la
   * memoria correspondiente
   * 
   * @param id       identificador de la {@link Memoria}
   * @param pageable paginación
   * @return listado paginado de {@link Informe}
   */
  @GetMapping("/{id}/informes")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV', 'ETI-PEV-V')")
  public ResponseEntity<Page<Informe>> getInformesFormulario(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {

    Page<Informe> page = informeService.findByMemoria(id, pageable);

    if (page.isEmpty()) {
      log.debug("getInformesFormulario(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("getInformesFormulario(Long id, Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * Obtener todas las entidades {@link Evaluacion} activas para una determinada
   * {@link Memoria}.
   *
   * @param id       Id de {@link Memoria}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @GetMapping("/{id}/evaluaciones")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV', 'ETI-PEV-V')")
  ResponseEntity<Page<Evaluacion>> getEvaluacionesMemoria(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getEvaluacionesMemoria(Long id, Pageable pageable) - start");
    Page<Evaluacion> page = evaluacionService.findAllByMemoriaId(id, pageable);
    log.debug("getEvaluacionesMemoria(Long id, Pageable pageable) - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Elimina la {@link DocumentacionMemoria} del tipo seguimiento anual.
   * 
   * @param id                     Id {@link Memoria}.
   * @param idDocumentacionMemoria id {@link DocumentacionMemoria} a eliminar.
   */
  @PreAuthorize("hasAuthorityForAnyUO('ETI-PEV-ER-INV')")
  @DeleteMapping("/{id}/documentacion-seguimiento-anual/{idDocumentacionMemoria}")
  void deleteDocumentacionSeguimientoAnual(@PathVariable Long id, @PathVariable Long idDocumentacionMemoria) {
    log.debug("deleteDocumentacionSeguimientoAnual(Long id, Long idDocumentacionMemoria) - start");
    documentacionMemoriaService.deleteDocumentacionSeguimientoAnual(id, idDocumentacionMemoria);
    log.debug("deleteDocumentacionSeguimientoAnual(Long id, Long idDocumentacionMemoria) - end");
  }

  /**
   * Elimina la {@link DocumentacionMemoria} del tipo seguimiento final.
   * 
   * @param id                     Id {@link Memoria}.
   * @param idDocumentacionMemoria id {@link DocumentacionMemoria} a eliminar.
   */
  @PreAuthorize("hasAuthorityForAnyUO('ETI-PEV-ER-INV')")
  @DeleteMapping("/{id}/documentacion-seguimiento-final/{idDocumentacionMemoria}")
  void deleteDocumentacionSeguimientoFinal(@PathVariable Long id, @PathVariable Long idDocumentacionMemoria) {
    log.debug("deleteDocumentacionSeguimientoFinal(Long id, Long idDocumentacionMemoria) - start");
    documentacionMemoriaService.deleteDocumentacionSeguimientoFinal(id, idDocumentacionMemoria);
    log.debug("deleteDocumentacionSeguimientoFinal(Long id, Long idDocumentacionMemoria) - end");
  }

  /**
   * Elimina la {@link DocumentacionMemoria} del tipo retrospectiva.
   * 
   * @param id                     Id {@link Memoria}.
   * @param idDocumentacionMemoria id {@link DocumentacionMemoria} a eliminar.
   */
  @PreAuthorize("hasAuthorityForAnyUO('ETI-PEV-ER-INV')")
  @DeleteMapping("/{id}/documentacion-retrospectiva/{idDocumentacionMemoria}")
  void deleteDocumentacionRetrospectiva(@PathVariable Long id, @PathVariable Long idDocumentacionMemoria) {
    log.debug("deleteDocumentacionRetrospectiva(Long id, Long idDocumentacionMemoria) - start");
    documentacionMemoriaService.deleteDocumentacionRetrospectiva(id, idDocumentacionMemoria);
    log.debug("deleteDocumentacionRetrospectiva(Long id, Long idDocumentacionMemoria) - end");
  }

  /**
   * Recupera el estado anterior de la {@link Memoria}.
   * 
   * @param id Id de {@link Memoria}.
   * @return la {@link Memoria} si el estado se ha podido actualizar
   */
  @GetMapping("/{id}/recuperar-estado-anterior")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-PEV-V')")
  ResponseEntity<Memoria> recuperarEstadoAnterior(@PathVariable Long id) {
    log.debug("recuperarEstadoAnterior(Long id) - start");
    Memoria estadoReestablecido = service.updateEstadoAnteriorMemoria(id);
    log.debug("recuperarEstadoAnterior(Long id) - end");
    return new ResponseEntity<>(estadoReestablecido, HttpStatus.OK);
  }

  /**
   * Se cambia el estado de la memoria a 'En Secretaría' o 'En Secretaría Revisión
   * Mínima'
   *
   * @param id Id de {@link Memoria}.
   */
  @PutMapping("/{id}/enviar-secretaria")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV')")
  void enviarSecretaria(@PathVariable Long id, Authentication authentication) {
    log.debug("enviarSecretaria(Long id) - start");
    String personaRef = authentication.getName();
    service.enviarSecretaria(id, personaRef);
    log.debug("enviarSecretaria(Long id) - end");
  }

  /**
   * Se cambia la retrospectiva de la memoria al estado 'En secretaría'
   *
   * @param id Id de {@link Memoria}.
   */
  @PutMapping("/{id}/enviar-secretaria-retrospectiva")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV')")
  void enviarSecretariaRetrospectiva(@PathVariable Long id, Authentication authentication) {
    log.debug("enviarSecretariaRetrospectiva(Long id) - start");
    String personaRef = authentication.getName();
    service.enviarSecretariaRetrospectiva(id, personaRef);
    log.debug("enviarSecretariaRetrospectiva(Long id) - end");
  }

  /**
   * Elimina la {@link DocumentacionMemoria} del tipo retrospectiva.
   * 
   * @param id                     Id {@link Memoria}.
   * @param idDocumentacionMemoria id {@link DocumentacionMemoria} a eliminar.
   */
  @PreAuthorize("hasAuthorityForAnyUO('ETI-PEV-ER-INV')")
  @DeleteMapping("/{id}/documentacion-inicial/{idDocumentacionMemoria}")
  void deleteDocumentacionInicial(@PathVariable Long id, @PathVariable Long idDocumentacionMemoria) {
    log.debug("deleteDocumentacionRetrospectiva(Long id, Long idDocumentacionMemoria) - start");
    documentacionMemoriaService.deleteDocumentacionInicial(id, idDocumentacionMemoria);
    log.debug("deleteDocumentacionRetrospectiva(Long id, Long idDocumentacionMemoria) - end");
  }

  /**
   * Recupera la próxima convocatoria de reunión sin acta finalizada asignada con
   * comité igual al de la memoria
   * 
   * @param idComite Id del {@link Comite}.
   * @return la {@link ConvocatoriaReunion}
   */
  @GetMapping("/{idComite}/convocatoria-reunion/proxima")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-C-INV', 'ETI-PEV-ER-INV')")
  ResponseEntity<ConvocatoriaReunion> getConvocatoriaReunionProxima(@PathVariable Long idComite) {
    log.debug("getConvocatoriaReunionProxima(Long id) - start");
    Optional<ConvocatoriaReunion> result = convocatoriaReunionRepository
        .findFirstConvocatoriaReunionSinActaFinalizadaByComiteOrderByFechaEvaluacionAsc(idComite);
    log.debug("getConvocatoriaReunionProxima(Long id) - end");
    if (!result.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(result.get(), HttpStatus.OK);
  }

  @GetMapping("/{id}/respuestas-documento")
  public ResponseEntity<Page<Respuesta>> getTiposDocumento(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {

    log.debug("newDocumentacionMemoriaSeguimientoAnual(Long id, DocumentacionMemoria documentacionMemoria) - start");
    Page<Respuesta> respuesta = respuestaService.findByMemoriaId(id, pageable);

    log.debug("newDocumentacionMemoriaSeguimientoAnual(Long id, DocumentacionMemoria documentacionMemoria) - end");
    if (respuesta.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(respuesta, HttpStatus.OK);
  }

  /**
   * * Devuelve el informe con la última versión
   * 
   * @param id identificador de la {@link Memoria}
   * @return el {@link Informe}
   */
  @GetMapping("/{id}/informe/ultima-version")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV', 'ETI-PEV-V')")
  public ResponseEntity<Informe> getInformeFormularioUltimaVersion(@PathVariable Long id) {

    Optional<Informe> returnValue = informeService.findFirstByMemoriaOrderByVersionDesc(id);

    if (!returnValue.isPresent()) {
      log.debug("getInformesFormulario(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("getInformesFormulario(Long id, Pageable pageable) - end");
    return new ResponseEntity<>(returnValue.get(), HttpStatus.OK);

  }
}
