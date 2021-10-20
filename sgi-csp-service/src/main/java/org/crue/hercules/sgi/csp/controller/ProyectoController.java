package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.AnualidadGastoOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoAgrupacionGastoOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadResumen;
import org.crue.hercules.sgi.csp.dto.ProyectoPresupuestoTotales;
import org.crue.hercules.sgi.csp.dto.ProyectoResponsableEconomicoOutput;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAgrupacionGasto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.model.ProyectoAreaConocimiento;
import org.crue.hercules.sgi.csp.model.ProyectoClasificacion;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.model.ProyectoPartida;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.service.AnualidadGastoService;
import org.crue.hercules.sgi.csp.service.EstadoProyectoService;
import org.crue.hercules.sgi.csp.service.ProrrogaDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoAgrupacionGastoService;
import org.crue.hercules.sgi.csp.service.ProyectoAnualidadService;
import org.crue.hercules.sgi.csp.service.ProyectoAreaConocimientoService;
import org.crue.hercules.sgi.csp.service.ProyectoClasificacionService;
import org.crue.hercules.sgi.csp.service.ProyectoConceptoGastoService;
import org.crue.hercules.sgi.csp.service.ProyectoDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadGestoraService;
import org.crue.hercules.sgi.csp.service.ProyectoEquipoService;
import org.crue.hercules.sgi.csp.service.ProyectoFaseService;
import org.crue.hercules.sgi.csp.service.ProyectoHitoService;
import org.crue.hercules.sgi.csp.service.ProyectoPaqueteTrabajoService;
import org.crue.hercules.sgi.csp.service.ProyectoPartidaService;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoService;
import org.crue.hercules.sgi.csp.service.ProyectoProrrogaService;
import org.crue.hercules.sgi.csp.service.ProyectoProyectoSgeService;
import org.crue.hercules.sgi.csp.service.ProyectoResponsableEconomicoService;
import org.crue.hercules.sgi.csp.service.ProyectoService;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoJustificacionDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoSocioService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoController
 */
@RestController
@RequestMapping(ProyectoController.REQUEST_MAPPING)
@Slf4j
public class ProyectoController {

  /** El path que gestiona este controlador */
  public static final String REQUEST_MAPPING = "/proyectos";

  private ModelMapper modelMapper;

  /** Proyecto service */
  private final ProyectoService service;

  /** ProyectoHito service */
  private final ProyectoHitoService proyectoHitoService;

  /** ProyectoFaseService */
  private final ProyectoFaseService proyectoFaseService;

  /** ProyectoPaqueteTrabajo service */
  private final ProyectoPaqueteTrabajoService proyectoPaqueteTrabajoService;

  /** ProyectoSocio service */
  private final ProyectoSocioService proyectoSocioService;

  /** ProyectoPeriodoSeguimiento service */
  private final ProyectoPeriodoSeguimientoService proyectoPeriodoSeguimientoService;

  /** ConvocatoriaEntidadFinanciadora service */
  /** ProyectoEntidadFinanciadoraService service */
  private final ProyectoEntidadFinanciadoraService proyectoEntidadFinanciadoraService;

  /** ProyectoDocumentoService service */
  private final ProyectoDocumentoService proyectoDocumentoService;

  /** ProyectoEntidadGestoraService */
  private final ProyectoEntidadGestoraService proyectoEntidadGestoraService;

  /** ProyectoEquipo service */
  private final ProyectoEquipoService proyectoEquipoService;

  /** ProyectoProrrogaservice */
  private final ProyectoProrrogaService proyectoProrrogaService;

  /** EstadoProyecto service */
  private final EstadoProyectoService estadoProyectoService;

  /** ProrrogaDocumentoService */
  private final ProrrogaDocumentoService prorrogaDocumentoService;

  /** ProyectoPeriodoSeguimientoDocumento service */
  private final ProyectoPeriodoSeguimientoDocumentoService proyectoPeriodoSeguimientoDocumentoService;

  /** ProyectoSocioPeriodoJustificacionDocumentoService service */
  private final ProyectoSocioPeriodoJustificacionDocumentoService proyectoSocioPeriodoJustificacionDocumentoService;

  /** ProyectoClasificacionService service */
  private final ProyectoClasificacionService proyectoClasificacionService;

  /** ProyectoAreaConocimientoService */
  private final ProyectoAreaConocimientoService proyectoAreaConocimientoService;

  /** ProyectoProyectoSge service */
  private final ProyectoProyectoSgeService proyectoProyectoSgeService;

  /** ProyectoAnualidadService service */
  private final ProyectoAnualidadService proyectoAnualidadService;

  /** ProyectoPartidaService service */
  private final ProyectoPartidaService proyectoPartidaService;
  /** ProyectoAgrupacionGasto Service */
  private final ProyectoAgrupacionGastoService proyectoAgrupacionGastoService;

  /** ProyectoConceptoGasto service */
  private final ProyectoConceptoGastoService proyectoConceptoGastoService;

  /** ProyectoResponsableEconomicoService */
  private final ProyectoResponsableEconomicoService proyectoResponsableEconomicoService;

  /** ProyectoPeriodoJustificacionService */
  private final ProyectoPeriodoJustificacionService proyectoPeriodoJustificacionService;

  private final AnualidadGastoService anualidadGastoService;

  /**
   * Instancia un nuevo ProyectoController.
   * 
   * @param modelMapper                                       {@link ModelMapper}.
   * @param proyectoService                                   {@link ProyectoService}.
   * @param proyectoHitoService                               {@link ProyectoHitoService}.
   * @param proyectoFaseService                               {@link ProyectoFaseService}.
   * @param proyectoPaqueteTrabajoService                     {@link ProyectoPaqueteTrabajoService}.
   * @param proyectoSocioService                              {@link ProyectoSocioService}.
   * @param proyectoEntidadFinanciadoraService                {@link ProyectoEntidadFinanciadoraService}.
   * @param proyectoPeriodoSeguimientoService                 {@link ProyectoPeriodoSeguimientoService}
   * @param proyectoEntidadGestoraService                     {@link ProyectoEntidadGestoraService}
   * @param proyectoEquipoService                             {@link ProyectoEquipoService}.
   * @param proyectoProrrogaService                           {@link ProyectoProrrogaService}.
   * @param estadoProyectoService                             {@link EstadoProyectoService}.
   * @param proyectoProrrogaService                           {@link ProyectoProrrogaService}.
   * @param proyectoDocumentoService                          {@link ProyectoDocumentoService}.
   * @param prorrogaDocumentoService                          {@link ProrrogaDocumentoService}.
   * @param proyectoPeriodoSeguimientoDocumentoService        {@link ProyectoPeriodoSeguimientoDocumentoService}.
   * @param proyectoSocioPeriodoJustificacionDocumentoService {@link ProyectoSocioPeriodoJustificacionDocumentoService}.
   * @param proyectoClasificacionService                      {@link ProyectoClasificacionService}.
   * @param proyectoAreaConocimientoService                   {@link ProyectoAreaConocimientoService}.
   * @param proyectoProyectoSgeService                        {@link ProyectoProyectoSgeService}.
   * @param proyectoAnualidadService                          {@link ProyectoAnualidadService}.
   * @param proyectoPartidaService                            {@link ProyectoPartidaService}.
   * @param proyectoConceptoGastoService                      {@link ProyectoConceptoGastoService}.
   * @param proyectoResponsableEconomicoService               {@link ProyectoResponsableEconomicoService}.
   * @param proyectoAgrupacionGastoService                    {@link ProyectoAgrupacionGastoService}.
   * @param proyectoPeriodoJustificacionService               {@link ProyectoPeriodoJustificacionService}.
   * @param anualidadGastoService                             {@link AnualidadGastoService}
   */
  public ProyectoController(ModelMapper modelMapper, ProyectoService proyectoService,
      ProyectoHitoService proyectoHitoService, ProyectoFaseService proyectoFaseService,
      ProyectoPaqueteTrabajoService proyectoPaqueteTrabajoService, ProyectoEquipoService proyectoEquipoService,
      ProyectoSocioService proyectoSocioService, ProyectoEntidadFinanciadoraService proyectoEntidadFinanciadoraService,
      ProyectoPeriodoSeguimientoService proyectoPeriodoSeguimientoService,
      ProyectoProrrogaService proyectoProrrogaService, ProyectoEntidadGestoraService proyectoEntidadGestoraService,
      ProyectoDocumentoService proyectoDocumentoService, EstadoProyectoService estadoProyectoService,
      ProrrogaDocumentoService prorrogaDocumentoService,
      ProyectoPeriodoSeguimientoDocumentoService proyectoPeriodoSeguimientoDocumentoService,
      ProyectoSocioPeriodoJustificacionDocumentoService proyectoSocioPeriodoJustificacionDocumentoService,
      ProyectoClasificacionService proyectoClasificacionService,
      ProyectoAreaConocimientoService proyectoAreaConocimientoService,
      ProyectoProyectoSgeService proyectoProyectoSgeService, ProyectoPartidaService proyectoPartidaService,
      ProyectoConceptoGastoService proyectoConceptoGastoService, ProyectoAnualidadService proyectoAnualidadService,
      ProyectoResponsableEconomicoService proyectoResponsableEconomicoService,
      ProyectoAgrupacionGastoService proyectoAgrupacionGastoService,
      ProyectoPeriodoJustificacionService proyectoPeriodoJustificacionService,
      AnualidadGastoService anualidadGastoService) {
    this.modelMapper = modelMapper;
    this.service = proyectoService;
    this.proyectoHitoService = proyectoHitoService;
    this.proyectoFaseService = proyectoFaseService;
    this.proyectoPaqueteTrabajoService = proyectoPaqueteTrabajoService;
    this.proyectoSocioService = proyectoSocioService;
    this.proyectoEntidadFinanciadoraService = proyectoEntidadFinanciadoraService;
    this.proyectoDocumentoService = proyectoDocumentoService;
    this.proyectoPeriodoSeguimientoService = proyectoPeriodoSeguimientoService;
    this.proyectoEntidadGestoraService = proyectoEntidadGestoraService;
    this.proyectoEquipoService = proyectoEquipoService;
    this.proyectoProrrogaService = proyectoProrrogaService;
    this.estadoProyectoService = estadoProyectoService;
    this.prorrogaDocumentoService = prorrogaDocumentoService;
    this.proyectoPeriodoSeguimientoDocumentoService = proyectoPeriodoSeguimientoDocumentoService;
    this.proyectoSocioPeriodoJustificacionDocumentoService = proyectoSocioPeriodoJustificacionDocumentoService;
    this.proyectoClasificacionService = proyectoClasificacionService;
    this.proyectoAreaConocimientoService = proyectoAreaConocimientoService;
    this.proyectoProyectoSgeService = proyectoProyectoSgeService;
    this.proyectoAnualidadService = proyectoAnualidadService;
    this.proyectoPartidaService = proyectoPartidaService;
    this.proyectoConceptoGastoService = proyectoConceptoGastoService;
    this.proyectoResponsableEconomicoService = proyectoResponsableEconomicoService;
    this.proyectoAgrupacionGastoService = proyectoAgrupacionGastoService;
    this.proyectoPeriodoJustificacionService = proyectoPeriodoJustificacionService;
    this.anualidadGastoService = anualidadGastoService;
  }

  /**
   * Crea nuevo {@link Proyecto}
   * 
   * @param proyecto {@link Proyecto} que se quiere crear.
   * @return Nuevo {@link Proyecto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-C')")
  public ResponseEntity<Proyecto> create(@Valid @RequestBody Proyecto proyecto) {
    log.debug("create(Proyecto proyecto) - start");

    Proyecto returnValue = service.create(proyecto);
    log.debug("create(Proyecto proyecto) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Proyecto}.
   * 
   * @param proyecto {@link Proyecto} a actualizar.
   * @param id       Identificador {@link Proyecto} a actualizar.
   * @return Proyecto {@link Proyecto} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public Proyecto update(@Valid @RequestBody Proyecto proyecto, @PathVariable Long id) {
    log.debug("update(Proyecto proyecto, Long id) - start");

    proyecto.setId(id);
    Proyecto returnValue = service.update(proyecto);
    log.debug("update(Proyecto proyecto, Long id) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link Proyecto} con id indicado.
   * 
   * @param id Identificador de {@link Proyecto}.
   * @return {@link Proyecto} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-R')")
  Proyecto reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    Proyecto returnValue = service.enable(id);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva la {@link Proyecto} con id indicado.
   * 
   * @param id Identificador de {@link Proyecto}.
   * @return {@link Proyecto} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-B')")
  Proyecto desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");

    Proyecto returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Proyecto} con el id indicado.
   * 
   * @param id Identificador de {@link Proyecto}.
   * @return Proyecto {@link Proyecto} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E', 'CSP-PRO-MOD-V')")
  Proyecto findById(@PathVariable Long id) {
    log.debug("Proyecto findById(Long id) - start");

    Proyecto returnValue = service.findById(id);
    log.debug("Proyecto findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Proyecto} activas que se
   * encuentren dentro de la unidad de gestión del usuario logueado
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E', 'CSP-PRO-B', 'CSP-PRO-MOD-V')")
  ResponseEntity<Page<Proyecto>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Page<Proyecto> page = service.findAllRestringidos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Proyecto} que se encuentren
   * dentro de la unidad de gestión del usuario logueado
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E', 'CSP-PRO-B', 'CSP-PRO-R')")
  ResponseEntity<Page<Proyecto>> findAllTodos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");

    Page<Proyecto> page = service.findAllTodosRestringidos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO HITO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoHito} del
   * {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectohitos")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<Page<ProyectoHito>> findAllProyectoHito(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoHito(Long id, String query, Pageable paging) - start");
    Page<ProyectoHito> page = proyectoHitoService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoHito(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoHito(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO FASE
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoFase} del
   * {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectofases")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<Page<ProyectoFase>> findAllProyectoFase(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoFase(Long id, String query, Pageable paging) - start");
    Page<ProyectoFase> page = proyectoFaseService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoFase(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoFase(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO PAQUETE TRABAJO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoPaqueteTrabajo} del
   * {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectopaquetetrabajos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E')")
  ResponseEntity<Page<ProyectoPaqueteTrabajo>> findAllProyectoPaqueteTrabajo(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoPaqueteTrabajo(Long id, String query, Pageable paging) - start");
    Page<ProyectoPaqueteTrabajo> page = proyectoPaqueteTrabajoService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoPaqueteTrabajo(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoPaqueteTrabajo(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO SOCIO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoSocio} del
   * {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectosocios")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<Page<ProyectoSocio>> findAllProyectoSocio(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoSocio(Long id, String query, Pageable paging) - start");
    Page<ProyectoSocio> page = proyectoSocioService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoSocio(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoSocio(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO ENTIDAD FINANCIADORA
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoEntidadFinanciadora}
   * del {@link Proyecto}.
   * 
   * @param id     Identificador del {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectoentidadfinanciadoras")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<Page<ProyectoEntidadFinanciadora>> findAllProyectoEntidadFinanciadora(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoEntidadFinanciadora(Long id, String query, Pageable paging) - start");
    Page<ProyectoEntidadFinanciadora> page = proyectoEntidadFinanciadoraService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoEntidadFinanciadora(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoEntidadFinanciadora(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * 
   * PROYECTO DOCUMENTOS
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoDocumento} del
   * {@link Proyecto}.
   * 
   * @param id Identificador del {@link Proyecto}.
   */
  @GetMapping("/{id}/documentos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<Page<ProyectoDocumento>> findAllDocumentos(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllDocumentos(Long id String query, Pageable paging) - start");

    Page<ProyectoDocumento> page = proyectoDocumentoService.findAllByProyectoId(id, query, paging);
    if (page.isEmpty()) {
      log.debug("findAllDocumentos(Long id String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllDocumentos(Long id String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * 
   * PROYECTO PERIODO SEGUIMIENTO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoPeriodoSeguimiento}
   * de la {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectoperiodoseguimientos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<Page<ProyectoPeriodoSeguimiento>> findAllProyectoPeriodoSeguimiento(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoPeriodoSeguimiento(Long id, String query, Pageable paging) - start");
    Page<ProyectoPeriodoSeguimiento> page = proyectoPeriodoSeguimientoService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoPeriodoSeguimiento(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoPeriodoSeguimiento(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO ENTIDAD GESTORA
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoEntidadGestora} del
   * {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectoentidadgestoras")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<Page<ProyectoEntidadGestora>> findAllProyectoEntidadGestora(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoEntidadGestora(Long id, String query, Pageable paging) - start");
    Page<ProyectoEntidadGestora> page = proyectoEntidadGestoraService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoEntidadGestora(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoEntidadGestora(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO EQUIPO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoEquipo} del
   * {@link Proyecto}.
   * 
   * @param id     Identificador del {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectoequipos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<Page<ProyectoEquipo>> findAllProyectoEquipo(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoEquipo(Long id, String query, Pageable paging) - start");
    Page<ProyectoEquipo> page = proyectoEquipoService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoEquipo(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoEquipo(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * 
   * PROYECTO PRÓRROGA
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoProrroga} del
   * {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyecto-prorrogas")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<Page<ProyectoProrroga>> findAllProyectoProrroga(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoProrroga(Long id, String query, Pageable paging) - start");
    Page<ProyectoProrroga> page = proyectoProrrogaService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoProrroga(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoProrroga(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoProrroga}
   *
   * @param id Id del {@link Proyecto}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/proyecto-prorrogas", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<Proyecto> hasProyectoProrrogas(@PathVariable Long id) {
    log.debug("hasProyectoProrrogas(Long id) - start");
    boolean returnValue = false;
    if (proyectoProrrogaService.existsByProyecto(id)) {
      returnValue = true;
    }
    log.debug("hasProyectoProrrogas(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * 
   * PROYECTO ESTADO
   * 
   */

  /**
   * Devuelve una lista de EstadoProyecto paginada y filtrada de {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param paging pageable.
   */

  @GetMapping("/{id}/estadoproyectos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<Page<EstadoProyecto>> findAllEstadoProyecto(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllEstadoProyecto(Long id, Pageable paging) - start");
    Page<EstadoProyecto> page = estadoProyectoService.findAllByProyecto(id, paging);

    if (page.isEmpty()) {
      log.debug("findAllEstadoProyecto(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllEstadoProyecto(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * 
   * SOLICITUD
   * 
   */

  /**
   * Crea nuevo {@link Proyecto} a partir de los datos de una {@link Solicitud}
   * 
   * @param id       identificador de la {@link Solicitud}
   * @param proyecto {@link Proyecto} a crear
   * @return Nuevo {@link Proyecto} creado.
   */
  @PostMapping("/{id}/solicitud")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-C')")
  public ResponseEntity<Proyecto> createProyectoBySolicitud(@PathVariable Long id, @RequestBody Proyecto proyecto) {
    log.debug("createProyectoBySolicitud(@PathVariable Long id) - start");

    Proyecto returnValue = service.createProyectoBySolicitud(id, proyecto);
    log.debug("createProyectoBySolicitud(@PathVariable Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoDocumento}
   *
   * @param id Id del {@link Proyecto}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/documentos", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<Proyecto> hasProyectoDocumentos(@PathVariable Long id) {
    log.debug("hasProyectoDocumentos(Long id) - start");
    boolean returnValue = false;
    if (prorrogaDocumentoService.existsByProyecto(id) || proyectoDocumentoService.existsByProyecto(id)
        || proyectoPeriodoSeguimientoDocumentoService.existsByProyecto(id)
        || proyectoSocioPeriodoJustificacionDocumentoService.existsByProyecto(id)) {
      returnValue = true;
    }
    log.debug("hasProyectoDocumentos(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoFase}
   *
   * @param id Id del {@link Proyecto}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/proyectofases", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<Proyecto> hasProyectoFases(@PathVariable Long id) {
    log.debug("hasProyectoFases(Long id) - start");
    boolean returnValue = proyectoFaseService.existsByProyecto(id);
    log.debug("hasProyectoFases(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoHito}
   *
   * @param id Id del {@link Proyecto}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/proyectohitos", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<Proyecto> hasProyectoHitos(@PathVariable Long id) {
    log.debug("hasProyectoHitos(Long id) - start");
    boolean returnValue = proyectoHitoService.existsByProyecto(id);
    log.debug("hasProyectoHitos(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada de {@link ProyectoClasificacion}
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyecto-clasificaciones")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  ResponseEntity<Page<ProyectoClasificacion>> findAllProyectoClasificaciones(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoClasificaciones(Long id, String query, Pageable paging) - start");
    Page<ProyectoClasificacion> page = proyectoClasificacionService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoClasificaciones(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoClasificaciones(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista de {@link ProyectoAreaConocimiento} con una
   * {@link Proyecto} con id indicado.
   * 
   * @param id Identificador de {@link Proyecto}.
   * @return Lista de {@link ProyectoAreaConocimiento} correspondiente al id
   */
  @GetMapping("/{id}/proyecto-areas-conocimiento")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  ResponseEntity<Page<ProyectoAreaConocimiento>> findAllByProyectoId(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllByProyectoId(Long id, String query, Pageable paging) - start");
    Page<ProyectoAreaConocimiento> page = proyectoAreaConocimientoService.findAllByProyectoId(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllByProyectoId(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllByProyectoId(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link ProyectoProyectoSge}
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectossge")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  ResponseEntity<Page<ProyectoProyectoSge>> findAllProyectoProyectosSge(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoProyectoSge(Long id, String query, Pageable paging) - start");
    Page<ProyectoProyectoSge> page = proyectoProyectoSgeService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoProyectoSge(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoProyectoSge(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link ProyectoAnualidadOutput}
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/anualidades")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  ResponseEntity<Page<ProyectoAnualidadResumen>> findAllProyectoAnualidadResumen(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoAnualidadResumen(Long id, String query, Pageable paging) - start");
    Page<ProyectoAnualidadResumen> page = proyectoAnualidadService.findAllResumenByProyecto(id, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoAnualidadResumen(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoAnualidadResumen(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link AnualidadGasto}
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/anualidadesgasto")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  ResponseEntity<Page<AnualidadGasto>> findAllProyectoAnualidadGasto(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoAnualidadGasto(Long id, String query, Pageable paging) - start");
    Page<AnualidadGasto> page = proyectoAnualidadService.findAllProyectoAnualidadGasto(id, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoAnualidadGasto(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoAnualidadGasto(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link ProyectoPartida}
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyecto-partidas")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  ResponseEntity<Page<ProyectoPartida>> findAllProyectoPartida(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoPartida(Long id, String query, Pageable paging) - start");
    Page<ProyectoPartida> page = proyectoPartidaService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoPartida(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoPartida(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO CONCEPTO GASTOS
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoConceptoGasto}
   * permitidos de {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectoconceptosgasto/permitidos")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<Page<ProyectoConceptoGasto>> findAllProyectoGastosPermitidos(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoGastosPermitidos(Long id, Pageable paging) - start");
    Page<ProyectoConceptoGasto> page = proyectoConceptoGastoService.findAllByProyectoAndPermitidoTrue(id, paging);
    if (page.isEmpty()) {
      log.debug("findAllProyectoGastosPermitidos(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoGastosPermitidos(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaConceptoGasto}
   * no permitidos del {@link Proyecto}.
   *
   * @param id     Identificador de {@link Proyecto}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectoconceptosgasto/nopermitidos")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<Page<ProyectoConceptoGasto>> findAllProyectoGastosNoPermitidos(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoGastosNoPermitidos(Long id, Pageable paging) - start");
    Page<ProyectoConceptoGasto> page = proyectoConceptoGastoService.findAllByProyectoAndPermitidoFalse(id, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoGastosNoPermitidos(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoGastosNoPermitidos(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Realiza los cambios de estado de proyectos".
   * 
   * @param id Identificador de {@link Proyecto}.
   * @return {@link Proyecto} actualizado.
   */
  @PatchMapping("/{id}/cambiar-estado")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  Proyecto cambiarEstado(@PathVariable Long id, @RequestBody EstadoProyecto estadoProyecto) {
    log.debug("cambiarEstado(EstadoProyecto estadoProyecto) - start");

    Proyecto returnValue = service.cambiarEstado(id, estadoProyecto);

    log.debug("cambiarEstado(EstadoProyecto estadoProyecto) - end");
    return returnValue;
  }

  /**
   * Obtiene el {@link ProyectoPresupuestoTotales} de la {@link Proyecto}.
   * 
   * @param id Identificador de {@link Proyecto}.
   * @return {@link ProyectoPresupuestoTotales}
   */
  @GetMapping("/{id}/presupuesto-totales")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V')")
  ProyectoPresupuestoTotales getProyectoPresupuestoTotales(@PathVariable Long id) {
    log.debug("getProyectoPresupuestoTotales(Long id) - start");
    ProyectoPresupuestoTotales returnValue = service.getTotales(id);
    log.debug("getProyectoPresupuestoTotales(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista de {@link ProyectoResponsableEconomico} con una
   * {@link Proyecto} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoResponsableEconomico}.
   * @return Lista de {@link ProyectoResponsableEconomico} correspondiente al id
   */
  @GetMapping("/{id}/proyectoresponsableseconomicos")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<Page<ProyectoResponsableEconomicoOutput>> findAllResponsablesEconomicosByProyecto(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllResponsablesEconomicosByProyecto(Long id, String query, Pageable paging) - start");
    Page<ProyectoResponsableEconomico> page = proyectoResponsableEconomicoService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllResponsablesEconomicosByProyecto(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllResponsablesEconomicosByProyecto(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista de {@link ProyectoAgrupacionGastoOutput} con una
   * {@link Proyecto} con id indicado.
   * 
   * @param id Identificador de {@link Proyecto}.
   * @return Lista de {@link ProyectoAgrupacionGastoOutput} correspondiente al id
   */

  @GetMapping("/{id}/proyectoagrupaciongasto")
  ResponseEntity<Page<ProyectoAgrupacionGastoOutput>> findAllProyectoAgrupacionGastoByProyectoId(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoAgrupacionGastoByProyectoId(Long id, String query, Pageable paging) - start");
    Page<ProyectoAgrupacionGastoOutput> page = convertProyectoAgrupacionGastoOutput(
        proyectoAgrupacionGastoService.findAllByProyectoId(id, query, paging));

    if (page.isEmpty()) {
      log.debug("findAllProyectoAgrupacionGastoByProyectoId(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoAgrupacionGastoByProyectoId(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de todos los
   * {@link ProyectoPeriodoJustificacion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectoperiodojustificacion")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<Page<ProyectoPeriodoJustificacion>> findAllPeriodoJustificacionByProyectoId(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllPeriodoJustificacionByProyectoId(Long id, String query, Pageable paging) - start");
    Page<ProyectoPeriodoJustificacion> page = proyectoPeriodoJustificacionService.findAllByProyectoId(id, query,
        paging);

    if (page.isEmpty()) {
      log.debug("findAllPeriodoJustificacionByProyectoId(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllPeriodoJustificacionByProyectoId(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  @RequestMapping(path = "/{proyectoId}/proyectosocios", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V')")
  public ResponseEntity<Object> hasAnyProyectoSocio(@PathVariable(required = true) Long proyectoId) {

    return this.proyectoSocioService.hasAnyProyectoSocioWithProyectoId(proyectoId) ? ResponseEntity.ok().build()
        : ResponseEntity.noContent().build();
  }

  @RequestMapping(path = "/{proyectoId}/proyectosocios/coordinador", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V')")
  public ResponseEntity<Object> hasAnyProyectoSocioWithRolCoordinador(@PathVariable(required = true) Long proyectoId) {

    return this.proyectoSocioService.hasAnyProyectoSocioWithRolCoordinador(proyectoId) ? ResponseEntity.ok().build()
        : ResponseEntity.noContent().build();
  }

  @RequestMapping(path = "/{proyectoId}/proyectosocios/periodospago", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V')")
  public ResponseEntity<Object> existsProyectoSocioPeriodoPagoByProyectoSocioId(
      @PathVariable(required = true) Long proyectoId) {

    return this.proyectoSocioService.existsProyectoSocioPeriodoPagoByProyectoSocioId(proyectoId)
        ? ResponseEntity.ok().build()
        : ResponseEntity.noContent().build();
  }

  @RequestMapping(path = "/{proyectoId}/proyectosocios/periodosjustificacion", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V')")
  public ResponseEntity<Object> existsProyectoSocioPeriodoJustificacionByProyectoSocioId(
      @PathVariable(required = true) Long proyectoId) {

    return this.proyectoSocioService.existsProyectoSocioPeriodoJustificacionByProyectoSocioId(proyectoId)
        ? ResponseEntity.ok().build()
        : ResponseEntity.noContent().build();
  }

  private ProyectoResponsableEconomicoOutput convert(ProyectoResponsableEconomico responsableEconomico) {
    return modelMapper.map(responsableEconomico, ProyectoResponsableEconomicoOutput.class);
  }

  private Page<ProyectoResponsableEconomicoOutput> convert(Page<ProyectoResponsableEconomico> page) {
    List<ProyectoResponsableEconomicoOutput> content = page.getContent().stream()
        .map((responsableEconomico) -> convert(responsableEconomico)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());

  }

  private ProyectoAgrupacionGastoOutput convertProyectoAgrupacionGastoOutput(
      ProyectoAgrupacionGasto proyectoAgrupacionGasto) {
    return modelMapper.map(proyectoAgrupacionGasto, ProyectoAgrupacionGastoOutput.class);
  }

  private Page<ProyectoAgrupacionGastoOutput> convertProyectoAgrupacionGastoOutput(Page<ProyectoAgrupacionGasto> page) {
    List<ProyectoAgrupacionGastoOutput> content = page.getContent().stream()
        .map((proyectoAgrupacionGasto) -> convertProyectoAgrupacionGastoOutput(proyectoAgrupacionGasto))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());

  }

  /**
   * Hace las comprobaciones necesarias para determinar si el {@link Proyecto}
   * puede ser modificada.
   * 
   * @param id Id del {@link Proyecto}.
   * @return HTTP-200 Si se permite modificación / HTTP-204 Si no se permite
   *         modificación
   */
  @RequestMapping(path = "/{id}/modificable", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E', 'CSP-PRO-V')")
  ResponseEntity<Convocatoria> modificable(@PathVariable Long id) {
    log.debug("modificable(Long id) - start");
    boolean returnValue = service.modificable(id, new String[] { "CSP-PRO-E", "CSP-PRO-V" });
    log.debug("modificable(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoFase}
   *
   * @param id Id del {@link Proyecto}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/proyectossge", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<Proyecto> hasProyectosSge(@PathVariable Long id) {
    log.debug("hasProyectosSge(Long id) - start");
    boolean returnValue = proyectoProyectoSgeService.existsByProyecto(id);
    log.debug("hasProyectosSge(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Obtiene los ids de {@link Proyecto} que cumplan las condiciones indicadas en
   * el filtro de búsqueda
   * 
   * @param query filtro de búsqueda.
   * @return lista de ids de {@link Proyecto}.
   */
  @GetMapping("/modificados-ids")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V')")
  ResponseEntity<List<Long>> findIds(@RequestParam(name = "q", required = false) String query) {
    log.debug("findIds(String query) - start");

    List<Long> returnValue = service.findIds(query);

    if (returnValue.isEmpty()) {
      log.debug("findIds(String query) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findIds(String query) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Devuelve una lista de {@link ProyectoAnualidad}
   * 
   * @param id Identificador del {@link Proyecto}.
   */
  @GetMapping("/{id}/proyectoanualidades")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  ResponseEntity<List<ProyectoAnualidad>> findAllProyectoAnualidad(@PathVariable Long id) {

    List<ProyectoAnualidad> anualidades = proyectoAnualidadService.findByProyectoId(id);

    return anualidades.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(anualidades);
  }

  /**
   * Devuelve una lista de {@link AnualidadGastoOutput} asociados a un
   * {@link Proyecto}
   * 
   * @param id Identificador del {@link Proyecto}.
   */
  @GetMapping("/{id}/anualidadesgastos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  ResponseEntity<List<AnualidadGastoOutput>> findProyectoAnualidadesGasto(@PathVariable Long id) {

    List<AnualidadGastoOutput> anualidades = this
        .convertListAnualidadGastoOutput(this.anualidadGastoService.findAnualidadesGastosByProyectoId(id));

    return anualidades.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(anualidades);
  }

  private List<AnualidadGastoOutput> convertListAnualidadGastoOutput(List<AnualidadGasto> anualidadesGasto) {
    return anualidadesGasto.stream().map((anualidadGasto) -> {
      return modelMapper.map(anualidadGasto, AnualidadGastoOutput.class);
    }).collect(Collectors.toList());

  }

}
