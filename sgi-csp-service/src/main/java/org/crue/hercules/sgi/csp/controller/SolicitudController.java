package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.SolicitudProyectoPresupuestoTotalConceptoGasto;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoPresupuestoTotales;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoResponsableEconomicoOutput;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoAreaConocimiento;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoClasificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.EstadoSolicitudService;
import org.crue.hercules.sgi.csp.service.ProyectoService;
import org.crue.hercules.sgi.csp.service.SolicitudDocumentoService;
import org.crue.hercules.sgi.csp.service.SolicitudHitoService;
import org.crue.hercules.sgi.csp.service.SolicitudModalidadService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoAreaConocimientoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoClasificacionService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadFinanciadoraAjenaService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEquipoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPresupuestoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoResponsableEconomicoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
 * SolicitudController
 */
@RestController
@RequestMapping("/solicitudes")
@Slf4j
public class SolicitudController {

  private ModelMapper modelMapper;

  /** Solicitud service */
  private final SolicitudService service;

  /** SolicitudModalidad service */
  private final SolicitudModalidadService solicitudModalidadService;

  /** EstadoSolicitud service */
  private final EstadoSolicitudService estadoSolicitudService;

  /** SolicitudDocumento service */
  private final SolicitudDocumentoService solicitudDocumentoService;

  /** SolicitudHito service */
  private final SolicitudHitoService solicitudHitoService;

  /** SolicitudProyectoService service */
  private final SolicitudProyectoService solicitudProyectoService;

  /** SolicitudProyectoSocioService service */
  private final SolicitudProyectoSocioService solicitudProyectoSocioService;

  /** SolicitudProyectoEquipoService service */
  private final SolicitudProyectoEquipoService solicitudProyectoEquipoService;

  /** SolicitudProyectoEntidadFinanciadoraAjena service */
  private final SolicitudProyectoEntidadFinanciadoraAjenaService solicitudProyectoEntidadFinanciadoraAjenaService;

  /** SolicitudProyectoPresupuesto service */
  private final SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService;

  /** SolicitudProyectoClasificacion service */
  private final SolicitudProyectoClasificacionService solicitudProyectoClasificacionService;

  /** SolicitudProyectoAreaConocimientoService */
  private final SolicitudProyectoAreaConocimientoService solicitudProyectoAreaConocimientoService;

  /** SolicitudProyectoResponaableEconomicoService */
  private final SolicitudProyectoResponsableEconomicoService solicitudProyectoResponsableEconomicoService;

  /** SolicitudProyectoEntidadService */
  private final SolicitudProyectoEntidadService solicitudProyectoEntidadService;

  /** ProyectoService */
  private final ProyectoService proyectoService;

  /**
   * Instancia un nuevo SolicitudController.
   * 
   * @param modelMapper                                      {@link ModelMapper}.
   * @param solicitudService                                 {@link SolicitudService}.
   * @param solicitudModalidadService                        {@link SolicitudModalidadService}.
   * @param solicitudDocumentoService                        {@link SolicitudDocumentoService}
   * @param estadoSolicitudService                           {@link EstadoSolicitudService}.
   * @param solicitudHitoService                             {@link SolicitudHitoService}.
   * @param solicitudProyectoService                         {@link SolicitudProyectoService}
   * @param solicitudProyectoSocioService                    {@link SolicitudProyectoSocioService}
   * @param solicitudProyectoEquipoService                   {@link SolicitudProyectoEquipoService}
   * @param solicitudProyectoEntidadFinanciadoraAjenaService {@link SolicitudProyectoEntidadFinanciadoraAjenaService}.
   * @param solicitudProyectoPresupuestoService              {@link SolicitudProyectoPresupuestoService}.
   * @param solicitudProyectoClasificacionService            {@link SolicitudProyectoClasificacionService}.
   * @param solicitudProyectoAreaConocimientoService         {@link SolicitudProyectoAreaConocimientoService}.
   * @param solicitudProyectoResponsableEconomicoService     {@link SolicitudProyectoResponsableEconomicoService}.
   * @param solicitudProyectoEntidadService                  {@link SolicitudProyectoEntidadService}.
   * @param proyectoService                                  {@link ProyectoService}.
   */
  public SolicitudController(ModelMapper modelMapper, SolicitudService solicitudService,
      SolicitudModalidadService solicitudModalidadService, EstadoSolicitudService estadoSolicitudService,
      SolicitudDocumentoService solicitudDocumentoService, SolicitudHitoService solicitudHitoService,
      SolicitudProyectoService solicitudProyectoService, SolicitudProyectoSocioService solicitudProyectoSocioService,
      SolicitudProyectoEquipoService solicitudProyectoEquipoService,
      SolicitudProyectoEntidadFinanciadoraAjenaService solicitudProyectoEntidadFinanciadoraAjenaService,
      SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService,
      SolicitudProyectoClasificacionService solicitudProyectoClasificacionService,
      SolicitudProyectoAreaConocimientoService solicitudProyectoAreaConocimientoService,
      SolicitudProyectoResponsableEconomicoService solicitudProyectoResponsableEconomicoService,
      SolicitudProyectoEntidadService solicitudProyectoEntidadService, ProyectoService proyectoService) {
    this.modelMapper = modelMapper;
    this.service = solicitudService;
    this.solicitudModalidadService = solicitudModalidadService;
    this.estadoSolicitudService = estadoSolicitudService;
    this.solicitudDocumentoService = solicitudDocumentoService;
    this.solicitudHitoService = solicitudHitoService;
    this.solicitudProyectoService = solicitudProyectoService;
    this.solicitudProyectoSocioService = solicitudProyectoSocioService;
    this.solicitudProyectoEquipoService = solicitudProyectoEquipoService;
    this.solicitudProyectoEntidadFinanciadoraAjenaService = solicitudProyectoEntidadFinanciadoraAjenaService;
    this.solicitudProyectoPresupuestoService = solicitudProyectoPresupuestoService;
    this.solicitudProyectoClasificacionService = solicitudProyectoClasificacionService;
    this.solicitudProyectoAreaConocimientoService = solicitudProyectoAreaConocimientoService;
    this.solicitudProyectoResponsableEconomicoService = solicitudProyectoResponsableEconomicoService;
    this.solicitudProyectoEntidadService = solicitudProyectoEntidadService;
    this.proyectoService = proyectoService;
  }

  /**
   * Crea nuevo {@link Solicitud}
   * 
   * @param solicitud      {@link Solicitud} que se quiere crear.
   * @param authentication {@link Authentication}.
   * @return Nuevo {@link Solicitud} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C','CSP-SOL-INV-C')")
  public ResponseEntity<Solicitud> create(@Valid @RequestBody Solicitud solicitud, Authentication authentication) {
    log.debug("create(Solicitud solicitud) - start");

    solicitud.setCreadorRef(authentication.getName());
    Solicitud returnValue = service.create(solicitud);
    log.debug("create(Solicitud solicitud) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Solicitud}.
   * 
   * @param solicitud {@link Solicitud} a actualizar.
   * @param id        Identificador {@link Solicitud} a actualizar.
   * @return Solicitud {@link Solicitud} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public Solicitud update(@Valid @RequestBody Solicitud solicitud, @PathVariable Long id) {
    log.debug("update(Solicitud solicitud, Long id) - start");

    solicitud.setId(id);
    Solicitud returnValue = service.update(solicitud);
    log.debug("update(Solicitud solicitud, Long id) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link Solicitud} con id indicado.
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-R')")
  Solicitud reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");

    Solicitud returnValue = service.enable(id);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva la {@link Solicitud} con id indicado.
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-B', 'CSP-SOL-INV-BR')")
  public Solicitud desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");

    Solicitud returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Solicitud} con el id indicado.
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return Solicitud {@link Solicitud} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-C')")
  Solicitud findById(@PathVariable Long id) {
    log.debug("Solicitud findById(Long id) - start");

    Solicitud returnValue = service.findById(id);
    log.debug("Solicitud findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Solicitud} activas que se
   * encuentren dentro de la unidad de gestión del usuario logueado
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Solicitud} activas paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  ResponseEntity<Page<Solicitud>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging, Authentication authentication) {
    log.debug("findAll(String query, Pageable paging) - start");

    Page<Solicitud> page = service.findAllRestringidos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Solicitud} que se encuentren
   * dentro de la unidad de gestión del usuario logueado
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Solicitud} activas paginadas y
   *         filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V','CSP-SOL-B', 'CSP-SOL-C', 'CSP-SOL-R', 'CSP-PRO-C')")
  ResponseEntity<Page<Solicitud>> findAllTodos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging, Authentication authentication) {
    log.debug("findAllTodos(String query, Pageable paging) - start");

    Page<Solicitud> page = service.findAllTodosRestringidos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link SolicitudModalidad} de la
   * {@link Solicitud}.
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudmodalidades")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V', 'CSP-SOL-INV-C')")
  ResponseEntity<Page<SolicitudModalidad>> findAllSolicitudModalidad(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudModalidad(Long id, String query, Pageable paging) - start");
    Page<SolicitudModalidad> page = solicitudModalidadService.findAllBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudModalidad(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudModalidad(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link SolicitudHito} de la
   * {@link Solicitud}.
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/estadosolicitudes")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  ResponseEntity<Page<EstadoSolicitud>> findAllEstadoSolicitud(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllEstadoSolicitud(Long id, Pageable paging) - start");
    Page<EstadoSolicitud> page = estadoSolicitudService.findAllBySolicitud(id, paging);

    if (page.isEmpty()) {
      log.debug("findAllEstadoSolicitud(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllEstadoSolicitud(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * Devuelve una lista paginada de {@link SolicitudHito}
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudhitos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V')")
  ResponseEntity<Page<SolicitudHito>> findAllSolicitudHito(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudHito(Long id, String query, Pageable paging) - start");
    Page<SolicitudHito> page = solicitudHitoService.findAllBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudHito(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudHito(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link SolicitudDocumento} de la
   * {@link Solicitud}.
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicituddocumentos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V','CSP-SOL-INV-ER')")
  ResponseEntity<Page<SolicitudDocumento>> findAllSolicitudDocumentos(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudDocumentos(Long id, String query, Pageable paging) - start");
    Page<SolicitudDocumento> page = solicitudDocumentoService.findAllBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudDocumentos(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudDocumentos(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Comprueba si la solicitud está asociada a una convocatoria SGI.
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return HTTP 200 si se encuentra asociada a convocatoria SGI y HTTP 204 si
   *         no.
   */
  @RequestMapping(path = "/{id}/convocatoria-sgi", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V')")
  public ResponseEntity<?> hasConvocatoriaSgi(@PathVariable Long id) {
    log.debug("hasConvocatoriaSgi(Long id) - start");
    Boolean returnValue = service.hasConvocatoriaSgi(id);
    log.debug("hasConvocatoriaSgi(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Recupera un {@link SolicitudProyecto} de una solicitud
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link SolicitudProyecto}
   */
  @RequestMapping(path = "/{id}/solicitudproyecto", method = RequestMethod.GET)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  public ResponseEntity<SolicitudProyecto> findSolictudProyectoDatos(@PathVariable Long id) {
    log.debug("findSolictudProyectoDatos(Long id) - start");
    SolicitudProyecto returnValue = solicitudProyectoService.findBySolicitud(id);

    if (returnValue == null) {
      log.debug("SolicitudProyecto findSolictudProyectoDatos(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("SolicitudProyecto findSolictudProyectoDatos(Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link SolicitudProyectoSocio}
   * 
   * @param id     Identificador de {@link SolicitudProyectoSocio}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectosocio")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  ResponseEntity<Page<SolicitudProyectoSocio>> findAllSolicitudProyectoSocio(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoSocio(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoSocio> page = solicitudProyectoSocioService.findAllBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoSocio(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoSocio(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link SolicitudProyectoEquipo}
   * 
   * @param id     Identificador de {@link SolicitudProyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectoequipo")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  ResponseEntity<Page<SolicitudProyectoEquipo>> findAllSolicitudProyectoEquipo(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoEquipo(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoEquipo> page = solicitudProyectoEquipoService.findAllBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoEquipo(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoEquipo(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve true si el solicitante existe en la SolicitudProyectoEquipo
   * 
   * @param id Identificador de {@link SolicitudProyecto}.
   */
  @RequestMapping(path = "/{id}/existssolicitante", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V')")
  ResponseEntity<?> existSolicitanteInSolicitudProyectoEquipo(@PathVariable Long id) {
    log.debug("existSolicitanteInSolicitudProyectoEquipo(Long id) - start");
    boolean returnValue = this.solicitudProyectoEquipoService.existsSolicitanteInSolicitudProyectoEquipo(id);
    log.debug("existSolicitanteInSolicitudProyectoEquipo(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada de
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectoentidadfinanciadoraajenas")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V')")
  ResponseEntity<Page<SolicitudProyectoEntidadFinanciadoraAjena>> findAllSolicitudProyectoEntidadFinanciadoraAjena(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoEntidadFinanciadoraAjena(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoEntidadFinanciadoraAjena> page = solicitudProyectoEntidadFinanciadoraAjenaService
        .findAllBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoEntidadFinanciadoraAjena(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoEntidadFinanciadoraAjena(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Recupera un {@link SolicitudProyecto} de una solicitud
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link SolicitudProyecto}
   */
  @RequestMapping(path = "/{id}/solicitudproyecto", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER' , 'CSP-SOL-INV-BR')")
  public ResponseEntity<?> existSolictudProyectoDatos(@PathVariable Long id) {
    log.debug("existSolictudProyectoDatos(Long id) - start");
    boolean returnValue = solicitudProyectoService.existsBySolicitudId(id);

    log.debug("SolicitudProyecto existSolictudProyectoDatos(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada de {@link SolicitudProyectoPresupuesto}
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectopresupuestos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V', 'CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<Page<SolicitudProyectoPresupuesto>> findAllSolicitudProyectoPresupuesto(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoPresupuesto(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoPresupuesto> page = solicitudProyectoPresupuestoService.findAllBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoPresupuesto(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoPresupuesto(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Comprueba la existencia de {@link SolicitudProyectoPresupuesto} asociados a
   * una solicitud para una entidadRef y relacionada con la convocatorai
   * 
   * @param id         Id de la Solicitud
   * @param entidadRef Referencia de la Entidad
   * @return {@link HttpStatus.OK} si existe alguna relación,
   *         {@link HttpStatus.NO_CONTENT} en cualquier otro caso
   */
  @RequestMapping(path = "/{id}/solicitudproyectopresupuestos/entidadconvocatoria/{entidadRef}", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<?> existSolicitudProyectoPresupuestoEntidadConvocatoria(@PathVariable Long id,
      @PathVariable String entidadRef) {
    log.debug("existSolicitudProyectoPresupuestoEntidadConvocatoria(Long id, String entidadRef) - start");
    boolean returnValue = solicitudProyectoPresupuestoService
        .existsBySolicitudProyectoSolicitudIdAndEntidadRefAndFinanciacionAjena(id, entidadRef, false);

    log.debug("existSolicitudProyectoPresupuestoEntidadConvocatoria(Long id, String entidadRef) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba la existencia de {@link SolicitudProyectoPresupuesto} asociados a
   * una solicitud para una entidadRef y NO relacionada con la convocatorai
   * 
   * @param id         Id de la Solicitud
   * @param entidadRef Referencia de la Entidad
   * @return {@link HttpStatus.OK} si existe alguna relación,
   *         {@link HttpStatus.NO_CONTENT} en cualquier otro caso
   */
  @RequestMapping(path = "/{id}/solicitudproyectopresupuestos/entidadajena/{entidadRef}", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<?> existSolicitudProyectoPresupuestoEntidadAjena(@PathVariable Long id,
      @PathVariable String entidadRef) {
    log.debug("existSolicitudProyectoPresupuestoEntidadAjena(Long id, String entidadRef) - start");
    boolean returnValue = solicitudProyectoPresupuestoService
        .existsBySolicitudProyectoSolicitudIdAndEntidadRefAndFinanciacionAjena(id, entidadRef, true);

    log.debug("existSolicitudProyectoPresupuestoEntidadAjena(Long id, String entidadRef) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Obtiene el {@link SolicitudProyectoPresupuestoTotales} de la
   * {@link Solicitud}.
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link SolicitudProyectoPresupuestoTotales}
   */
  @GetMapping("/{id}/solicitudproyectopresupuestos/totales")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V')")
  SolicitudProyectoPresupuestoTotales getSolicitudProyectoPresupuestoTotales(@PathVariable Long id) {
    log.debug("getSolicitudProyectoPresupuestoTotales(Long id) - start");
    SolicitudProyectoPresupuestoTotales returnValue = solicitudProyectoPresupuestoService.getTotales(id);
    log.debug("getSolicitudProyectoPresupuestoTotales(Long id) - end");
    return returnValue;
  }

  @GetMapping("/{id}/solicitudproyectopresupuestos/totalesconceptogasto")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V')")
  public ResponseEntity<List<SolicitudProyectoPresupuestoTotalConceptoGasto>> findAllSolicitudProyectoPresupuestoTotalConceptoGastos(
      @PathVariable Long id) {
    log.debug("findAllSolicitudProyectoPresupuestoTotalConceptoGastos(Long id) - start");
    List<SolicitudProyectoPresupuestoTotalConceptoGasto> returnValue = solicitudProyectoPresupuestoService
        .findAllSolicitudProyectoPresupuestoTotalConceptoGastos(id);
    log.debug("findAllSolicitudProyectoPresupuestoTotalConceptoGastos(Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Hace las comprobaciones necesarias para determinar si se puede crear un
   * {@link Proyecto} a partir de la {@link Solicitud}
   * 
   * @param id Id de la {@link Solicitud}.
   * @return HTTP-200 Si se permite la creación / HTTP-204 Si no se permite
   *         creación
   */
  @RequestMapping(path = "/{id}/crearproyecto", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-C')")
  ResponseEntity<Solicitud> isPosibleCrearProyecto(@PathVariable Long id) {
    log.debug("isPosibleCrearProyecto(Long id) - start");
    Boolean returnValue = service.isPosibleCrearProyecto(id);
    log.debug("isPosibleCrearProyecto(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Solicitud}
   * puede ser modificada.
   * 
   * @param id Id del {@link Solicitud}.
   * @return HTTP-200 Si se permite modificación / HTTP-204 Si no se permite
   *         modificación
   */
  @RequestMapping(path = "/{id}/modificable", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER' , 'CSP-SOL-INV-BR')")
  ResponseEntity<Solicitud> modificable(@PathVariable Long id) {
    log.debug("modificable(Long id) - start");
    Boolean returnValue = service.modificable(id);
    log.debug("modificable(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Se hace el cambio de estado de "Borrador" a "Presentada".
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @PatchMapping("/{id}/cambiar-estado")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  Solicitud cambiarEstado(@PathVariable Long id, @RequestBody EstadoSolicitud estadoSolicitud) {
    log.debug("presentar(Long id) - start");

    Solicitud returnValue = service.cambiarEstado(id, estadoSolicitud);

    log.debug("presentar(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Solicitud} que puede visualizar
   * un investigador paginadas y filtradas.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Solicitud} que puede visualizar un
   *         investigador paginadas y filtradas.
   */
  @GetMapping("/investigador")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-INV-ER' , 'CSP-SOL-INV-BR')")
  ResponseEntity<Page<Solicitud>> findAllInvestigador(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllInvestigador(String query, Pageable paging) - start");
    Page<Solicitud> page = service.findAllInvestigador(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllInvestigador(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllInvestigador(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link SolicitudProyectoClasificacion}
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitud-proyecto-clasificaciones")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V','CSP-SOL-INV-ER')")
  ResponseEntity<Page<SolicitudProyectoClasificacion>> findAllSolicitudProyectoClasificaciones(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoClasificaciones(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoClasificacion> page = solicitudProyectoClasificacionService.findAllBySolicitud(id, query,
        paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoClasificaciones(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoClasificaciones(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista de {@link SolicitudProyectoAreaConocimiento} con una
   * {@link SolicitudProyecto} con id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyecto}.
   * @return Lista de {@link SolicitudProyectoAreaConocimiento} correspondiente al
   *         id
   */
  @GetMapping("/{id}/solicitud-proyecto-areas-conocimiento")
  ResponseEntity<Page<SolicitudProyectoAreaConocimiento>> findAllBySolicitudProyectoId(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllBySolicitudProyectoId(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoAreaConocimiento> page = solicitudProyectoAreaConocimientoService
        .findAllBySolicitudProyectoId(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllBySolicitudProyectoId(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllBySolicitudProyectoId(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista de {@link SolicitudProyectoResponsableEconomico} con una
   * {@link SolicitudProyecto} con id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoResponsableEconomico}.
   * @return Lista de {@link SolicitudProyectoResponsableEconomico}
   *         correspondiente al id
   */
  @GetMapping("/{id}/solicitudproyectoresponsableseconomicos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  ResponseEntity<Page<SolicitudProyectoResponsableEconomicoOutput>> findAllResponsablesEconomicosBySolicitud(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllResponsablesEconomicosBySolicitud(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoResponsableEconomico> page = solicitudProyectoResponsableEconomicoService
        .findAllBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllResponsablesEconomicosBySolicitud(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllResponsablesEconomicosBySolicitud(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Comprueba la existencia de {@link SolicitudProyectoPresupuesto} asociados a
   * una solicitud
   * 
   * @param id Id de la Solicitud
   * @return {@link HttpStatus.OK} si existe alguna relación,
   *         {@link HttpStatus.NO_CONTENT} en cualquier otro caso
   */
  @RequestMapping(path = "/{id}/solicitudproyectopresupuestos", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  ResponseEntity<?> existSolicitudProyectoPresupuesto(@PathVariable Long id) {
    log.debug("existSolicitudProyectoPresupuesto(Long id) - start");
    boolean returnValue = solicitudProyectoPresupuestoService.existsBySolicitudProyectoSolicitudId(id);

    log.debug("existSolicitudProyectoPresupuesto(Long id,) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si la solicitud proyecto de la solicitud es de tipo Global
   * 
   * @param id Id de la Solicitud
   * @return {@link HttpStatus.OK} si es tipo Global,
   *         {@link HttpStatus.NO_CONTENT} Por Entidad o Mixto
   */
  @RequestMapping(path = "/{id}/solicitudproyecto-global", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E')")
  ResponseEntity<?> hasSolicitudProyectoTipoGlobal(@PathVariable Long id) {
    log.debug("hasSolicitudProyectoTipoGlobal(Long id) - start");
    boolean returnValue = solicitudProyectoService.isTipoPresupuestoGlobalBySolicitudId(id);

    log.debug("hasSolicitudProyectoTipoGlobal(Long id,) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  private SolicitudProyectoResponsableEconomicoOutput convert(
      SolicitudProyectoResponsableEconomico responsableEconomico) {
    return modelMapper.map(responsableEconomico, SolicitudProyectoResponsableEconomicoOutput.class);
  }

  private Page<SolicitudProyectoResponsableEconomicoOutput> convert(Page<SolicitudProyectoResponsableEconomico> page) {
    List<SolicitudProyectoResponsableEconomicoOutput> content = page.getContent().stream()
        .map((responsableEconomico) -> convert(responsableEconomico)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  /**
   * Devuelve una lista paginada de {@link ConvocatoriaEntidadFinanciadora}
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectoentidadfinanciadora")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V')")
  ResponseEntity<Page<ConvocatoriaEntidadFinanciadora>> findAllSolicitudProyectoEntidadFinanciadora(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoEntidadFinanciadora(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaEntidadFinanciadora> page = solicitudProyectoEntidadService
        .findConvocatoriaEntidadFinanciadoraBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoEntidadFinanciadora(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoEntidadFinanciadora(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link SolicitudProyectoEntidad} para una
   * {@link Solicitud} correspondientes a un tipo de presupuesto mixto (entidad
   * gestora de la convocatoria y entidades financiadoras ajenas).
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectoentidad/tipopresupuestomixto")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V')")
  ResponseEntity<Page<SolicitudProyectoEntidad>> findSolicitudProyectoEntidadTipoPresupuestoMixto(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findSolicitudProyectoEntidadTipoPresupuestoMixto(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoEntidad> page = solicitudProyectoEntidadService
        .findSolicitudProyectoEntidadTipoPresupuestoMixto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findSolicitudProyectoEntidadTipoPresupuestoMixto(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findSolicitudProyectoEntidadTipoPresupuestoMixto(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link SolicitudProyectoEntidad} para una
   * {@link Solicitud} correspondientes a un tipo de presupuesto mixto (entidad
   * gestora de la convocatoria y entidades financiadoras ajenas).
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectoentidad/tipopresupuestoporentidad")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V')")
  ResponseEntity<Page<SolicitudProyectoEntidad>> findSolicitudProyectoEntidadTipoPresupuestoPorEntidad(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findSolicitudProyectoEntidadTipoPresupuestoPorEntidad(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoEntidad> page = solicitudProyectoEntidadService
        .findSolicitudProyectoEntidadTipoPresupuestoPorEntidad(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findSolicitudProyectoEntidadTipoPresupuestoPorEntidad(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findSolicitudProyectoEntidadTipoPresupuestoPorEntidad(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista de identificadores de los objetos de tipo {@link Proyecto}
   * que están que tienen relación con el objeto de tipo {@link Solicitud} cuyo id
   * se recibe por el path
   * 
   * @param id id del objeto {@link Solicitud}
   * @return lista de identificadores de los objetos de tipo {@link Proyecto}
   */
  @GetMapping("/{id}/proyectosids")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  public ResponseEntity<List<Long>> findProyectosIdsBySolicitudId(@PathVariable Long id) {

    List<Long> proyectos = this.proyectoService.findIdsBySolicitudId(id);
    return proyectos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(proyectos);
  }

}
