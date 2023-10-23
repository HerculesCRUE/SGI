package org.crue.hercules.sgi.eti.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.eti.converter.RespuestaConverter;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.dto.RespuestaOutput;
import org.crue.hercules.sgi.eti.model.BaseEntity;
import org.crue.hercules.sgi.eti.model.BaseEntity.Update;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria.Tipo;
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
import org.springframework.web.bind.annotation.PatchMapping;
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
 * MemoriaController
 */
@Validated
@RestController
@RequestMapping(MemoriaController.REQUEST_MAPPING)
@RequiredArgsConstructor
@Slf4j
public class MemoriaController {

  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "memorias";
  public static final String PATH_INVESTIGADOR = PATH_DELIMITER + "investigador";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_DOCUMENTACION_INICIAL = PATH_ID + PATH_DELIMITER + "documentacion-inicial";
  public static final String PATH_DOCUMENTACION_INICIAL_INVESTIGADOR = PATH_DOCUMENTACION_INICIAL + PATH_INVESTIGADOR;
  public static final String PATH_ESTADO_ACTUAL = PATH_ID + PATH_DELIMITER + "estado-actual";
  public static final String PATH_INDICAR_SUBSANACION = PATH_ID + PATH_DELIMITER + "indicar-subsanacion";
  public static final String PATH_LAST_EVALUACION = PATH_ID + PATH_DELIMITER + "last-evaluacion";
  public static final String PATH_LAST_EVALUACION_PENDIENTE_CORRECCIONES = PATH_ID + PATH_DELIMITER
      + "last-evaluacion-pendiente-correcciones";
  public static final String PATH_RESPUESTAS = PATH_ID + PATH_DELIMITER + "respuestas";

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

  /** Respuesta service */
  private final RespuestaService respuestaService;
  private final RespuestaConverter respuestaConverter;

  /**
   * Devuelve una lista paginada y filtrada {@link Memoria}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-VR', 'ETI-MEM-V')")
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
    log.debug("findAllMemoriasAsignablesConvocatoria({}) - start", idConvocatoria);
    List<Memoria> result = service.findAllMemoriasAsignablesConvocatoria(idConvocatoria);
    log.debug("findAllMemoriasAsignablesConvocatoria({}) - end", idConvocatoria);
    return result.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(result, HttpStatus.OK);
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-CR', 'ETI-MEM-INV-ER')")
  public ResponseEntity<Memoria> newMemoria(
      @Validated({ BaseEntity.Create.class, Default.class }) @RequestBody Memoria nuevaMemoria) {
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-CR', 'ETI-MEM-INV-ER')")
  public ResponseEntity<Memoria> newMemoriaModificada(
      @Validated({ BaseEntity.Create.class, Default.class }) @RequestBody Memoria nuevaMemoria, @PathVariable Long id) {
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
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER')")
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
  @GetMapping(PATH_ID)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-eti')) or hasAnyAuthority('ETI-MEM-INV-ER', 'ETI-MEM-V')")
  public Memoria findById(@PathVariable Long id) {
    log.debug("findById({}) - start", id);
    Memoria returnValue = service.findById(id);
    log.debug("findById({}) - end", id);
    return returnValue;
  }

  /**
   * Elimina {@link Memoria} con id indicado.
   * 
   * @param id Identificador de {@link Memoria}.
   */
  @DeleteMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('ETI-MEM-INV-BR')")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    Memoria memoria = service.findById(id);
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR')")
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
  @GetMapping("/{id}/documentacion-formulario")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER', 'ETI-MEM-V', 'ETI-MEM-EDOC')")
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
  @GetMapping("/{id}/documentacion-seguimiento-anual")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER', 'ETI-MEM-V', 'ETI-MEM-EDOC')")
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
  @GetMapping("/{id}/documentacion-seguimiento-final")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER', 'ETI-MEM-V', 'ETI-MEM-EDOC')")
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
  @GetMapping("/{id}/documentacion-retrospectiva")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER', 'ETI-MEM-V', 'ETI-MEM-EDOC')")
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-INV-VR', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR', 'ETI-MEM-EDOC')")
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-VR', 'ETI-MEM-V')")
  public ResponseEntity<Page<MemoriaPeticionEvaluacion>> findAllMemoriasEvaluacionByPersonaRef(
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
  @PostMapping(PATH_DOCUMENTACION_INICIAL)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-EDOC')")
  public ResponseEntity<DocumentacionMemoria> newDocumentacionMemoriaInicial(@PathVariable Long id,
      @Valid @RequestBody DocumentacionMemoria documentacionMemoria) {
    log.debug("newDocumentacionMemoriaInicial(Long id, DocumentacionMemoria documentacionMemoria) - start");
    DocumentacionMemoria returnValue = documentacionMemoriaService.createDocumentacionInicial(id, documentacionMemoria);

    log.debug("newDocumentacionMemoriaInicial(Long id, DocumentacionMemoria documentacionMemoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Crea nueva {@link DocumentacionMemoria}.
   * 
   * @param id                   Identificador de la {@link Memoria}.
   * @param documentacionMemoria {@link DocumentacionMemoria}. que se quiere
   *                             crear.
   * @return Nueva {@link DocumentacionMemoria} creada.
   */
  @PostMapping(PATH_DOCUMENTACION_INICIAL_INVESTIGADOR)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER')")
  public ResponseEntity<DocumentacionMemoria> newDocumentacionMemoriaInicialInvestigador(@PathVariable Long id,
      @Valid @RequestBody DocumentacionMemoria documentacionMemoria) {
    log.debug("newDocumentacionMemoriaInicialInvestigador(Long id, DocumentacionMemoria documentacionMemoria) - start");
    DocumentacionMemoria returnValue = documentacionMemoriaService.createDocumentacionInicialInvestigador(id,
        documentacionMemoria);

    log.debug("newDocumentacionMemoriaInicialInvestigador(Long id, DocumentacionMemoria documentacionMemoria) - end");
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
  @PostMapping("/{id}/documentacion-seguimiento-anual")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER', 'ETI-MEM-V', 'ETI-MEM-EDOC')")
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
  @PostMapping("/{id}/documentacion-seguimiento-final")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER', 'ETI-MEM-V', 'ETI-MEM-EDOC')")
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
  @PostMapping("/{id}/documentacion-retrospectiva")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER', 'ETI-MEM-V', 'ETI-MEM-EDOC')")
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER', 'ETI-MEM-V')")
  public ResponseEntity<Page<Informe>> getInformesFormulario(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getInformesFormulario(Long id, Pageable pageable) - start");
    Page<Informe> page = informeService.findByMemoria(id, pageable);
    log.debug("getInformesFormulario(Long id, Pageable pageable) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);

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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER', 'ETI-MEM-V')")
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
  @DeleteMapping("/{id}/documentacion-seguimiento-anual/{idDocumentacionMemoria}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER', 'ETI-MEM-EDOC')")
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
  @DeleteMapping("/{id}/documentacion-seguimiento-final/{idDocumentacionMemoria}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER', 'ETI-MEM-EDOC')")
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
  @DeleteMapping("/{id}/documentacion-retrospectiva/{idDocumentacionMemoria}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER', 'ETI-MEM-EDOC')")
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
  @PreAuthorize("hasAuthorityForAnyUO('ETI-MEM-CEST')")
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
  @PreAuthorize("hasAuthorityForAnyUO('ETI-MEM-INV-ESCR')")
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
  @PreAuthorize("hasAuthorityForAnyUO('ETI-MEM-INV-ERTR')")
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
   * @param authentication         Authentication
   */
  @DeleteMapping("/{id}/documentacion-inicial/{idDocumentacionMemoria}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER', 'ETI-MEM-EDOC')")
  void deleteDocumentacionInicial(@PathVariable Long id, @PathVariable Long idDocumentacionMemoria,
      Authentication authentication) {
    log.debug("deleteDocumentacionRetrospectiva(Long id, Long idDocumentacionMemoria) - start");
    documentacionMemoriaService.deleteDocumentacionInicial(id, idDocumentacionMemoria, authentication);
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-INV-C', 'ETI-PEV-INV-ER')")
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER', 'ETI-MEM-V', 'ETI-EVC-EVALR')")
  public ResponseEntity<Informe> getInformeFormularioUltimaVersion(@PathVariable Long id) {
    log.debug("getInformeFormularioUltimaVersion(Long id) - start");
    Optional<Informe> returnValue = informeService.findFirstByMemoriaOrderByVersionDesc(id);
    log.debug("getInformeFormularioUltimaVersion(Long id) - end");
    return returnValue.isPresent() ? new ResponseEntity<>(returnValue.get(), HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprobación de si están o no los documentos obligatorios aportados para
   * pasar la memoria al estado en secretaría
   * 
   * @param id Id de {@link Memoria}
   * @return HTTP-200 Si se permite enviar a secretaría / HTTP-204 Si no se
   *         permite enviar a secretaría
   */
  @RequestMapping(path = "/{id}/enviar-secretaria", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('ETI-MEM-INV-ESCR')")
  ResponseEntity<Boolean> checkDatosAdjuntosExists(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("checkDatosAdjuntosExists(Long id) - start");
    Boolean returnValue = service.checkDatosAdjuntosExists(id, pageable);
    log.debug("checkDatosAdjuntosExists(Long id) - end");
    return returnValue.booleanValue() ? new ResponseEntity<>(HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Se actualiza el estado de la memoria a "Archivado" de {@link Memoria} que han
   * pasado "mesesArchivadaPendienteCorrecciones" días desde la fecha de estado de
   * una memoria cuyo estado es "Pendiente Correcciones"
   * 
   * @return Los ids de memorias que pasan al estado "Archivado"
   */
  @PatchMapping("/no-presentado/archivar")
  @PreAuthorize("isClient() and hasAuthority('SCOPE_sgi-eti')")
  public ResponseEntity<String> archivarNoPresentados() {
    log.debug("archivarNoPresentados() - start");
    List<Long> archivadas = service.archivarNoPresentados();
    log.debug("archivarNoPresentados() - end");
    return new ResponseEntity<>(String.format("archivarNoPresentados() - Archivadas %d memorias. Ids: %s",
        archivadas.size(), Arrays.toString(archivadas.toArray())), HttpStatus.OK);
  }

  /**
   * Se actualiza el estado de la memoria a "Archivado" de {@link Memoria} que han
   * pasado "diasArchivadaInactivo" meses desde la fecha de estado de una memoria
   * cuyo estados son "Favorable Pendiente de Modificaciones Mínimas" o "No
   * procede evaluar" o "Solicitud modificación"
   * 
   * @return Los ids de memorias que pasan al estado "Archivado"
   */
  @PatchMapping("/inactivo/archivar")
  @PreAuthorize("isClient() and hasAuthority('SCOPE_sgi-eti')")
  public ResponseEntity<String> archivarInactivos() {
    log.debug("archivarInactivos() - start");
    List<Long> archivadas = service.archivarInactivos();
    log.debug("archivarInactivos() - end");
    return new ResponseEntity<>(String.format("archivarInactivos() - Archivadas %d memorias. Ids: %s",
        archivadas.size(), Arrays.toString(archivadas.toArray())), HttpStatus.OK);
  }

  /**
   * * Devuelve el informe con la última versión
   * 
   * @param id             identificador de la {@link Memoria}
   * @param tipoEvaluacion identificador del tipo de evaluación
   * @return el {@link Informe}
   */
  @GetMapping("/{id}/informe/ultima-version/tipo/{tipoEvaluacion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ER', 'ETI-MEM-V', 'ETI-EVC-EVALR')")
  public ResponseEntity<Informe> getInformeFormularioUltimaVersionTipoEvaluacion(@PathVariable Long id,
      @PathVariable Long tipoEvaluacion) {
    log.debug("getInformeFormularioUltimaVersionTipoEvaluacion(Long id, Long tipoEvaluacion) - start");
    Optional<Informe> returnValue = informeService.findByMemoriaAndTipoEvaluacion(id, tipoEvaluacion);

    if (!returnValue.isPresent()) {
      log.debug("getInformeFormularioUltimaVersionTipoEvaluacion(Long id, Long tipoEvaluacion) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("getInformeFormularioUltimaVersionTipoEvaluacion(Long id, Long tipoEvaluacion) - end");
    return new ResponseEntity<>(returnValue.get(), HttpStatus.OK);

  }

  /**
   * Comprueba si la persona es responsable de la {@link Memoria} o creador de la
   * {@link PeticionEvaluacion} con el id de {@link Memoria} indicado.
   * 
   * @param id             Identificador de {@link Memoria}.
   * @param authentication Authentication
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/responsable-creador", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('ETI-MEM-INV-ER')")
  public ResponseEntity<Void> isResponsableOrCreador(@PathVariable Long id, Authentication authentication) {
    log.debug("isResponsableOrCreador(Long id) - start");
    String personaRef = authentication.getName();
    if (service.isMemoriaWithPersonaRefCreadorPeticionEvaluacionOrResponsableMemoria(personaRef, id).booleanValue()) {
      log.debug("isResponsableOrCreador(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("isResponsableOrCreador(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Cambia el estado de la memoria a {@link Tipo#SUBSANACION} con el comentario
   * 
   * @param id         Identificador del Checklist a actualizar
   * @param comentario un comentario
   * @return {@link HttpStatus#OK}
   */
  @PatchMapping(PATH_INDICAR_SUBSANACION)
  @PreAuthorize("hasAuthority('ETI-MEM-CEST')")
  public ResponseEntity<Void> indicarSubsanacion(@PathVariable Long id,
      @NotEmpty @Size(max = EstadoMemoria.COMENTARIO_MAX_LENGTH) @RequestBody String comentario) {
    log.debug("indicarSubsanacion({}, {}) - start", id, comentario);
    service.indicarSubsanacion(id, comentario);
    log.debug("indicarSubsanacion({}, {}) - end", id, comentario);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Recupera el estado actual de la {@link Memoria}.
   * 
   * @param id Id de {@link Memoria}.
   * @return el estado de la {@link Memoria}.
   */
  @GetMapping(PATH_ESTADO_ACTUAL)
  @PreAuthorize("hasAnyAuthority('ETI-MEM-INV-ER', 'ETI-MEM-V')")
  public ResponseEntity<EstadoMemoria> getEstadoActual(@PathVariable Long id) {
    log.debug("getEstadoActual({}) - start", id);
    EstadoMemoria estado = service.getEstadoActualMemoria(id);
    log.debug("getEstadoActual({}) - end", id);
    return new ResponseEntity<>(estado, HttpStatus.OK);
  }

  /**
   * Obtiene la ultima evaluacion de la memoria
   * 
   * @param id identificador de la {@link Memoria}
   * @return la evaluacion
   */
  @GetMapping(PATH_LAST_EVALUACION)
  @PreAuthorize("hasAnyAuthority('ETI-CNV-E', 'ETI-MEM-INV-ER', 'ETI-MEM-V')")
  public ResponseEntity<Evaluacion> getLastEvaluacionMemoria(@PathVariable Long id) {
    log.debug("getLastEvaluacionMemoria({}) - start", id);
    Evaluacion returnValue = evaluacionService.getLastEvaluacionMemoria(id);
    log.debug("getLastEvaluacionMemoria({}) - end", id);
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Comprueba si la ultima evaluacion de la memoria tiene dictamen pendiente de
   * correcciones
   * 
   * @param id identificador de la {@link Memoria}
   * @return {@link HttpStatus#OK} si la ultima evaluacion tiene dictamen
   *         pendiente de correcciones / {@link HttpStatus#NO_CONTENT} si no lo
   *         tiene
   */
  @RequestMapping(path = PATH_LAST_EVALUACION_PENDIENTE_CORRECCIONES, method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthority('ETI-CNV-E')")
  public ResponseEntity<Void> isLastEvaluacionMemoriaPendienteCorrecciones(@PathVariable Long id) {
    log.debug("isLastEvaluacionMemoriaPendienteCorrecciones({}) - start", id);
    boolean isPendienteCorrecciones = evaluacionService.isLastEvaluacionMemoriaPendienteCorrecciones(id);
    log.debug("isLastEvaluacionMemoriaPendienteCorrecciones({}) - end", id);
    return isPendienteCorrecciones ? new ResponseEntity<>(HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada de {@link Memoria} a partir de una petición de
   * evaluación asignables para una convocatoria determinada
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
   * @param idPeticionEvaluacion identificador de la {@link PeticionEvaluacion}.
   */
  @GetMapping("/asignables-peticion-evaluacion/{idPeticionEvaluacion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-E')")
  ResponseEntity<List<Memoria>> findAllMemoriasAsignablesPeticionEvaluacion(@PathVariable Long idPeticionEvaluacion) {
    log.debug("findAllMemoriasAsignablesPeticionEvaluacion(Long idPeticionEvaluacion) - start");
    List<Memoria> result = service.findAllMemoriasAsignablesPeticionEvaluacion(idPeticionEvaluacion);

    if (result.isEmpty()) {
      log.debug("findAllMemoriasAsignablesPeticionEvaluacion(Long idPeticionEvaluacion) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllMemoriasAsignablesPeticionEvaluacion(Long idPeticionEvaluacion) - end");
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  /**
   * Devuelve la lista de {@link RespuestaOutput} de una memoria determinada
   * 
   * @param id identificador de la {@link Memoria}.
   * @return la lista de respuestas
   */
  @GetMapping(PATH_RESPUESTAS)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-eti'))")
  ResponseEntity<List<RespuestaOutput>> getRespuestasMemoria(@PathVariable Long id) {
    log.debug("getRespuestasMemoria({}) - start", id);
    List<RespuestaOutput> result = respuestaConverter.convert(respuestaService.findByMemoriaId(id));
    log.debug("getRespuestasMemoria({}) - end", id);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

}
