package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.csp.converter.ProyectoFaseConverter;
import org.crue.hercules.sgi.csp.converter.RequerimientoJustificacionConverter;
import org.crue.hercules.sgi.csp.dto.AnualidadGastoOutput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaTituloOutput;
import org.crue.hercules.sgi.csp.dto.NotificacionProyectoExternoCVNOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoAgrupacionGastoOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadResumen;
import org.crue.hercules.sgi.csp.dto.ProyectoDto;
import org.crue.hercules.sgi.csp.dto.ProyectoEquipoDto;
import org.crue.hercules.sgi.csp.dto.ProyectoFacturacionOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoFaseOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoPalabraClaveInput;
import org.crue.hercules.sgi.csp.dto.ProyectoPalabraClaveOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoPresupuestoTotales;
import org.crue.hercules.sgi.csp.dto.ProyectoResponsableEconomicoOutput;
import org.crue.hercules.sgi.csp.dto.ProyectosCompetitivosPersonas;
import org.crue.hercules.sgi.csp.dto.RequerimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.AnualidadIngreso;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.GastoProyecto;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAgrupacionGasto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.model.ProyectoAreaConocimiento;
import org.crue.hercules.sgi.csp.model.ProyectoClasificacion;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoPalabraClave;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.model.ProyectoPartida;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.service.AnualidadGastoService;
import org.crue.hercules.sgi.csp.service.AnualidadIngresoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.csp.service.EstadoProyectoService;
import org.crue.hercules.sgi.csp.service.GastoProyectoService;
import org.crue.hercules.sgi.csp.service.NotificacionProyectoExternoCVNService;
import org.crue.hercules.sgi.csp.service.ProrrogaDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoAgrupacionGastoService;
import org.crue.hercules.sgi.csp.service.ProyectoAnualidadService;
import org.crue.hercules.sgi.csp.service.ProyectoAreaConocimientoService;
import org.crue.hercules.sgi.csp.service.ProyectoClasificacionService;
import org.crue.hercules.sgi.csp.service.ProyectoConceptoGastoCodigoEcService;
import org.crue.hercules.sgi.csp.service.ProyectoConceptoGastoService;
import org.crue.hercules.sgi.csp.service.ProyectoDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadGestoraService;
import org.crue.hercules.sgi.csp.service.ProyectoEquipoService;
import org.crue.hercules.sgi.csp.service.ProyectoFaseService;
import org.crue.hercules.sgi.csp.service.ProyectoHitoService;
import org.crue.hercules.sgi.csp.service.ProyectoPalabraClaveService;
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
import org.crue.hercules.sgi.csp.service.RequerimientoJustificacionService;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoController
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(ProyectoController.REQUEST_MAPPING)
@Slf4j
public class ProyectoController {

  /** El path que gestiona este controlador */
  public static final String PATH_SEPARATOR = "/";
  public static final String REQUEST_MAPPING = PATH_SEPARATOR + "proyectos";

  public static final String PATH_PROYECTOS_COMPETITIVOS_PERSONAS = PATH_SEPARATOR + "competitivos-personas";
  public static final String PATH_INVESTIGADOR = PATH_SEPARATOR + "investigador";

  public static final String PATH_ID = PATH_SEPARATOR + "{id}";
  public static final String PATH_ANUALIDAD_GASTOS = PATH_ID + PATH_SEPARATOR + "anualidad-gastos";
  public static final String PATH_ANUALIDAD_INGRESOS = PATH_ID + PATH_SEPARATOR + "anualidad-ingresos";
  public static final String PATH_CAMBIAR_ESTADO = PATH_ID + PATH_SEPARATOR + "cambiar-estado";
  public static final String PATH_CODIGOS_ECONOMICOS = PATH_ID + PATH_SEPARATOR + "proyectoconceptosgastocodigosecs";
  public static final String PATH_CODIGOS_ECONOMICOS_NO_PERMITIDOS = PATH_CODIGOS_ECONOMICOS + PATH_SEPARATOR
      + "nopermitidos";
  public static final String PATH_CODIGOS_ECONOMICOS_PERMITIDOS = PATH_CODIGOS_ECONOMICOS + PATH_SEPARATOR
      + "permitidos";
  public static final String PATH_GASTOS_PROYECTO = PATH_ID + PATH_SEPARATOR + "gastos-proyecto";
  public static final String PATH_INVESTIGADORES_PRINCIPALES = PATH_ID + PATH_SEPARATOR + "investigadoresprincipales";
  public static final String PATH_MODIFICABLE = PATH_ID + PATH_SEPARATOR + "modificable";
  public static final String PATH_PRORROGAS = PATH_ID + PATH_SEPARATOR + "proyecto-prorrogas";
  public static final String PATH_REQUERIMIENTOS_JUSTIFICACION = PATH_ID + PATH_SEPARATOR
      + "requerimientos-justificacion";
  public static final String PATH_SOLICITUD = PATH_ID + PATH_SEPARATOR + "solicitud";

  private final ModelMapper modelMapper;

  private final AnualidadGastoService anualidadGastoService;
  private final AnualidadIngresoService anualidadIngresoService;
  private final ConvocatoriaService convocatoriaService;
  private final EstadoProyectoService estadoProyectoService;
  private final GastoProyectoService gastoProyectoService;
  private final NotificacionProyectoExternoCVNService notificacionProyectoExternoCVNService;
  private final ProrrogaDocumentoService prorrogaDocumentoService;
  private final ProyectoAgrupacionGastoService proyectoAgrupacionGastoService;
  private final ProyectoAnualidadService proyectoAnualidadService;
  private final ProyectoAreaConocimientoService proyectoAreaConocimientoService;
  private final ProyectoClasificacionService proyectoClasificacionService;
  private final ProyectoConceptoGastoCodigoEcService proyectoConceptoGastoCodigoEcService;
  private final ProyectoConceptoGastoService proyectoConceptoGastoService;
  private final ProyectoDocumentoService proyectoDocumentoService;
  private final ProyectoEntidadFinanciadoraService proyectoEntidadFinanciadoraService;
  private final ProyectoEntidadGestoraService proyectoEntidadGestoraService;
  private final ProyectoEquipoService proyectoEquipoService;
  private final ProyectoFaseService proyectoFaseService;
  private final ProyectoHitoService proyectoHitoService;
  private final ProyectoPalabraClaveService proyectoPalabraClaveService;
  private final ProyectoPaqueteTrabajoService proyectoPaqueteTrabajoService;
  private final ProyectoPartidaService proyectoPartidaService;
  private final ProyectoPeriodoJustificacionService proyectoPeriodoJustificacionService;
  private final ProyectoPeriodoSeguimientoDocumentoService proyectoPeriodoSeguimientoDocumentoService;
  private final ProyectoPeriodoSeguimientoService proyectoPeriodoSeguimientoService;
  private final ProyectoProrrogaService proyectoProrrogaService;
  private final ProyectoProyectoSgeService proyectoProyectoSgeService;
  private final ProyectoResponsableEconomicoService proyectoResponsableEconomicoService;
  private final ProyectoService service;
  private final ProyectoSocioPeriodoJustificacionDocumentoService proyectoSocioPeriodoJustificacionDocumentoService;
  private final ProyectoSocioService proyectoSocioService;
  private final RequerimientoJustificacionService requerimientoJustificacionService;

  private final ProyectoFaseConverter proyectoFaseConverter;
  private final RequerimientoJustificacionConverter requerimientoJustificacionConverter;

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
  public Proyecto reactivar(@PathVariable Long id) {
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
  public Proyecto desactivar(@PathVariable Long id) {
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E', 'CSP-PRO-MOD-V', 'CSP-PRO-INV-VR')")
  public Proyecto findById(@PathVariable Long id) {
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
  public ResponseEntity<Page<Proyecto>> findAll(@RequestParam(name = "q", required = false) String query,
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E', 'CSP-PRO-B', 'CSP-PRO-R', 'CSP-PRO-INV-VR')")
  public ResponseEntity<Page<Proyecto>> findAllTodos(@RequestParam(name = "q", required = false) String query,
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
   * @return el listado de entidades {@link ProyectoHito} paginadas
   *         y filtradas del {@link Proyecto}.
   */
  @GetMapping("/{id}/proyectohitos")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoHito>> findAllProyectoHito(@PathVariable Long id,
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
   * @return el listado de entidades {@link ProyectoFase} paginadas
   *         y filtradas del {@link Proyecto}.
   */
  @GetMapping("/{id}/proyectofases")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoFaseOutput>> findAllProyectoFase(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoFase(Long id, String query, Pageable paging) - start");
    Page<ProyectoFase> page = proyectoFaseService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoFase(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoFase(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(this.proyectoFaseConverter.convert(page), HttpStatus.OK);
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
   * @return el listado de entidades {@link ProyectoPaqueteTrabajo} paginadas
   *         y filtradas del {@link Proyecto}.
   */
  @GetMapping("/{id}/proyectopaquetetrabajos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoPaqueteTrabajo>> findAllProyectoPaqueteTrabajo(@PathVariable Long id,
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
   * @return el listado de entidades {@link ProyectoSocio} paginadas
   *         y filtradas del {@link Proyecto}.
   */
  @GetMapping("/{id}/proyectosocios")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoSocio>> findAllProyectoSocio(@PathVariable Long id,
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
   * @return el listado de entidades {@link ProyectoEntidadFinanciadora} paginadas
   *         y filtradas del {@link Proyecto}.
   */
  @GetMapping("/{id}/proyectoentidadfinanciadoras")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E', 'CSP-PRO-MOD-V')")
  public ResponseEntity<Page<ProyectoEntidadFinanciadora>> findAllProyectoEntidadFinanciadora(@PathVariable Long id,
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
   * @param id     Identificador del {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ProyectoDocumento} paginadas
   *         y filtradas del {@link Proyecto}.
   */
  @GetMapping("/{id}/documentos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoDocumento>> findAllDocumentos(@PathVariable Long id,
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
   * @return el listado de entidades {@link ProyectoPeriodoSeguimiento} paginadas
   *         y filtradas del {@link Proyecto}.
   */
  @GetMapping("/{id}/proyectoperiodoseguimientos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoPeriodoSeguimiento>> findAllProyectoPeriodoSeguimiento(@PathVariable Long id,
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
   * @return el listado de entidades {@link ProyectoEntidadGestora} paginadas y
   *         filtradas del {@link Proyecto}.
   */
  @GetMapping("/{id}/proyectoentidadgestoras")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoEntidadGestora>> findAllProyectoEntidadGestora(@PathVariable Long id,
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
   * @return el listado de entidades {@link ProyectoEquipo} paginadas y
   *         filtradas del {@link Proyecto}.
   */
  @GetMapping("/{id}/proyectoequipos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E', 'CSP-PRO-MOD-V', 'CSP-PRO-INV-VR')")
  public ResponseEntity<Page<ProyectoEquipo>> findAllProyectoEquipo(@PathVariable Long id,
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
   * @return el listado de entidades {@link ProyectoProrroga} paginadas y
   *         filtradas del {@link Proyecto}.
   */
  @GetMapping(PATH_PRORROGAS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E', 'CSP-PRO-INV-VR')")
  public ResponseEntity<Page<ProyectoProrroga>> findAllProyectoProrroga(@PathVariable Long id,
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
  @RequestMapping(path = PATH_PRORROGAS, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E','CSP-PRO-INV-VR' )")
  public ResponseEntity<Proyecto> hasProyectoProrrogas(@PathVariable Long id) {
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
   * @return el listado de entidades {@link EstadoProyecto} paginadas del
   *         {@link Proyecto}.
   */

  @GetMapping("/{id}/estadoproyectos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  public ResponseEntity<Page<EstadoProyecto>> findAllEstadoProyecto(@PathVariable Long id,
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
  @PostMapping(PATH_SOLICITUD)
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
  public ResponseEntity<Proyecto> hasProyectoDocumentos(@PathVariable Long id) {
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
  public ResponseEntity<Proyecto> hasProyectoFases(@PathVariable Long id) {
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
  public ResponseEntity<Proyecto> hasProyectoHitos(@PathVariable Long id) {
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
   * @return el listado de entidades {@link ProyectoClasificacion} paginadas y
   *         filtradas del {@link Proyecto}.
   */
  @GetMapping("/{id}/proyecto-clasificaciones")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoClasificacion>> findAllProyectoClasificaciones(@PathVariable Long id,
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
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ProyectoAreaConocimiento} paginadas y
   *         filtradas del {@link Proyecto}.
   */
  @GetMapping("/{id}/proyecto-areas-conocimiento")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoAreaConocimiento>> findAllByProyectoId(@PathVariable Long id,
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
   * @return el listado de entidades {@link ProyectoProyectoSge} paginadas y
   *         filtradas del {@link Proyecto}.
   */
  @GetMapping("/{id}/proyectossge")
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-csp')) or hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E', 'CSP-PRO-MOD-V','CSP-PRO-INV-VR')")
  public ResponseEntity<Page<ProyectoProyectoSge>> findAllProyectoProyectosSge(@PathVariable Long id,
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
   * @return el listado de entidades {@link ProyectoAnualidadResumen} del
   *         {@link Proyecto}.
   */
  @GetMapping("/{id}/anualidades")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoAnualidadResumen>> findAllProyectoAnualidadResumen(@PathVariable Long id,
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
   * @return el listado de entidades {@link AnualidadGasto} del {@link Proyecto}.
   */
  @GetMapping("/{id}/anualidadesgasto")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  public ResponseEntity<Page<AnualidadGasto>> findAllProyectoAnualidadGasto(@PathVariable Long id,
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
   * @return el listado de entidades {@link ProyectoPartida}
   *         paginados y filtrados.
   */
  @GetMapping("/{id}/proyecto-partidas")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoPartida>> findAllProyectoPartida(@PathVariable Long id,
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
   * @return el listado de {@link ProyectoConceptoGasto} permitidos.
   */
  @GetMapping("/{id}/proyectoconceptosgasto/permitidos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoConceptoGasto>> findAllProyectoGastosPermitidos(@PathVariable Long id,
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
   * Devuelve una lista paginada y filtrada de {@link ProyectoConceptoGasto}
   * no permitidos del {@link Proyecto}.
   *
   * @param id     Identificador de {@link Proyecto}.
   * @param paging pageable.
   * @return el listado de {@link ProyectoConceptoGasto} no permitidos.
   */
  @GetMapping("/{id}/proyectoconceptosgasto/nopermitidos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoConceptoGasto>> findAllProyectoGastosNoPermitidos(@PathVariable Long id,
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
   * @param id             Identificador de {@link Proyecto}.
   * @param estadoProyecto nuevo {@link EstadoProyecto}.
   * @return {@link Proyecto} actualizado.
   */
  @PatchMapping(PATH_CAMBIAR_ESTADO)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public Proyecto cambiarEstado(@PathVariable Long id, @RequestBody EstadoProyecto estadoProyecto) {
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
  public ProyectoPresupuestoTotales getProyectoPresupuestoTotales(@PathVariable Long id) {
    log.debug("getProyectoPresupuestoTotales(Long id) - start");
    ProyectoPresupuestoTotales returnValue = service.getTotales(id);
    log.debug("getProyectoPresupuestoTotales(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista de {@link ProyectoResponsableEconomico} con una
   * {@link Proyecto} con id indicado.
   * 
   * @param id     Identificador de {@link ProyectoResponsableEconomico}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return Lista de {@link ProyectoResponsableEconomico} correspondiente al id
   */
  @GetMapping("/{id}/proyectoresponsableseconomicos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoResponsableEconomicoOutput>> findAllResponsablesEconomicosByProyecto(
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
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return Lista de {@link ProyectoAgrupacionGastoOutput} correspondiente al id
   */

  @GetMapping("/{id}/proyectoagrupaciongasto")
  public ResponseEntity<Page<ProyectoAgrupacionGastoOutput>> findAllProyectoAgrupacionGastoByProyectoId(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
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
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ProyectoPeriodoJustificacion} del
   *         {@link Proyecto}.
   */
  @GetMapping("/{id}/proyectoperiodojustificacion")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoPeriodoJustificacion>> findAllPeriodoJustificacionByProyectoId(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V','CSP-PRO-INV-VR')")
  public ResponseEntity<Object> hasAnyProyectoSocio(@PathVariable(required = true) Long proyectoId) {

    return this.proyectoSocioService.hasAnyProyectoSocioWithProyectoId(proyectoId) ? ResponseEntity.ok().build()
        : ResponseEntity.noContent().build();
  }

  @RequestMapping(path = "/{proyectoId}/proyectosocios/coordinador", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-PRO-INV-VR')")
  public ResponseEntity<Object> hasAnyProyectoSocioWithRolCoordinador(@PathVariable(required = true) Long proyectoId) {

    return this.proyectoSocioService.hasAnyProyectoSocioWithRolCoordinador(proyectoId) ? ResponseEntity.ok().build()
        : ResponseEntity.noContent().build();
  }

  @RequestMapping(path = "/{proyectoId}/proyectosocios/periodospago", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-PRO-INV-VR')")
  public ResponseEntity<Object> existsProyectoSocioPeriodoPagoByProyectoSocioId(
      @PathVariable(required = true) Long proyectoId) {

    return this.proyectoSocioService.existsProyectoSocioPeriodoPagoByProyectoSocioId(proyectoId)
        ? ResponseEntity.ok().build()
        : ResponseEntity.noContent().build();
  }

  @RequestMapping(path = "/{proyectoId}/proyectosocios/periodosjustificacion", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-PRO-INV-VR')")
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
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());

  }

  private ProyectoAgrupacionGastoOutput convertProyectoAgrupacionGastoOutput(
      ProyectoAgrupacionGasto proyectoAgrupacionGasto) {
    return modelMapper.map(proyectoAgrupacionGasto, ProyectoAgrupacionGastoOutput.class);
  }

  private Page<ProyectoAgrupacionGastoOutput> convertProyectoAgrupacionGastoOutput(Page<ProyectoAgrupacionGasto> page) {
    List<ProyectoAgrupacionGastoOutput> content = page.getContent().stream()
        .map(this::convertProyectoAgrupacionGastoOutput)
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
  @RequestMapping(path = PATH_MODIFICABLE, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E', 'CSP-PRO-V', 'CSP-PRO-INV-VR')")
  public ResponseEntity<Void> modificable(@PathVariable Long id) {
    log.debug("modificable(Long id) - start");
    boolean returnValue = service.modificable(id);
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
  public ResponseEntity<Proyecto> hasProyectosSge(@PathVariable Long id) {
    log.debug("hasProyectosSge(Long id) - start");
    boolean returnValue = proyectoProyectoSgeService.existsByProyecto(id);
    log.debug("hasProyectosSge(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Obtiene los ids de {@link Proyecto} modificados que esten
   * activos y con {@link Proyecto#confidencial} a <code>false</code> que cumplan
   * las condiciones indicadas en el filtro de búsqueda
   * 
   * @param query filtro de búsqueda.
   * @return lista de ids de {@link Proyecto}.
   */
  @GetMapping("/modificados-ids")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  public ResponseEntity<List<Long>> findIdsProyectosModificados(
      @RequestParam(name = "q", required = false) String query) {
    log.debug("findIdsProyectosModificados(String query) - start");
    List<Long> returnValue = service.findIdsProyectosModificados(query);
    log.debug("findIdsProyectosModificados(String query) - end");
    return returnValue.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Devuelve una lista de {@link ProyectoAnualidad}
   * 
   * @param id Identificador del {@link Proyecto}.
   * @return el listado de entidades {@link ProyectoAnualidad} del
   *         {@link Proyecto}.
   */
  @GetMapping("/{id}/proyectoanualidades")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  public ResponseEntity<List<ProyectoAnualidad>> findAllProyectoAnualidad(@PathVariable Long id) {

    List<ProyectoAnualidad> anualidades = proyectoAnualidadService.findByProyectoId(id);

    return anualidades.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(anualidades);
  }

  /**
   * Devuelve una lista de {@link AnualidadGastoOutput} asociados a un
   * {@link Proyecto}
   * 
   * @param id Identificador del {@link Proyecto}.
   * @return el listado de entidades {@link AnualidadGastoOutput}.
   */
  @GetMapping("/{id}/anualidadesgastos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  public ResponseEntity<List<AnualidadGastoOutput>> findProyectoAnualidadesGasto(@PathVariable Long id) {

    List<AnualidadGastoOutput> anualidades = this
        .convertListAnualidadGastoOutput(this.anualidadGastoService.findAnualidadesGastosByProyectoId(id));

    return anualidades.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(anualidades);
  }

  private List<AnualidadGastoOutput> convertListAnualidadGastoOutput(List<AnualidadGasto> anualidadesGasto) {
    return anualidadesGasto.stream().map(anualidadGasto -> modelMapper.map(anualidadGasto, AnualidadGastoOutput.class))
        .collect(Collectors.toList());
  }

  /*
   * Devuelve una lista paginada y filtrada de {@link ProyectoFacturacion} del
   * {@link Proyecto}.
   *
   * @param query filtro de búsqueda.
   * 
   * @param id Identificador del {@link Proyecto}.
   */
  @GetMapping("/{id}/proyectosfacturacion")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E', 'CSP-PRO-INV-VR')")
  public ResponseEntity<Page<ProyectoFacturacionOutput>> findAllProyectoFacturacion(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {

    Page<ProyectoFacturacion> page = this.service.findAllProyectoFacturacionByProyectoId(id, query, paging);
    return page.isEmpty() ? ResponseEntity.noContent().build()
        : ResponseEntity.ok(convertToProyectoFacturacionOutputPage(page));
  }

  ProyectoFacturacionOutput convert(ProyectoFacturacion entity) {
    return this.modelMapper.map(entity, ProyectoFacturacionOutput.class);
  }

  private Page<ProyectoFacturacionOutput> convertToProyectoFacturacionOutputPage(Page<ProyectoFacturacion> page) {

    return new PageImpl<>(page.getContent().stream().map(this::convert).collect(Collectors.toList()),
        page.getPageable(), page.getTotalElements());

  }

  /**
   * Devuelve las {@link ProyectoPalabraClave} asociadas a la entidad
   * {@link Proyecto} con el id indicado
   * 
   * @param proyectoId Identificador de {@link Proyecto}
   * @param query      filtro de búsqueda.
   * @param paging     pageable.
   * @return {@link ProyectoPalabraClave} correspondientes al id de la entidad
   *         {@link Proyecto}
   */
  @GetMapping("/{proyectoId}/palabrasclave")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E', 'CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-INV-VR')")
  public Page<ProyectoPalabraClaveOutput> findPalabrasClave(@PathVariable Long proyectoId,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findPalabrasClave(@PathVariable Long proyectoId, String query, Pageable paging) - start");
    Page<ProyectoPalabraClaveOutput> returnValue = convertProyectoPalabraClave(
        proyectoPalabraClaveService.findByProyectoId(proyectoId, query, paging));
    log.debug("findPalabrasClave(@PathVariable Long proyectoId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link ProyectoPalabraClave} asociadas a la entidad
   * {@link Proyecto} con el id indicado
   * 
   * @param proyectoId    identificador de {@link Proyecto}
   * @param palabrasClave nueva lista de {@link ProyectoPalabraClave} de
   *                      la entidad {@link Proyecto}
   * @return la nueva lista de {@link ProyectoPalabraClave} asociadas a la entidad
   *         {@link Proyecto}
   */
  @PatchMapping("/{proyectoId}/palabrasclave")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E', 'CSP-PRO-C')")
  public ResponseEntity<List<ProyectoPalabraClaveOutput>> updatePalabrasClave(@PathVariable Long proyectoId,
      @Valid @RequestBody List<ProyectoPalabraClaveInput> palabrasClave) {
    log.debug("updatePalabrasClave(Long proyectoId, List<ProyectoPalabraClaveInput> palabrasClave) - start");

    palabrasClave.stream().forEach(palabraClave -> {
      if (!palabraClave.getProyectoId().equals(proyectoId)) {
        throw new NoRelatedEntitiesException(ProyectoPalabraClave.class, Proyecto.class);
      }
    });

    List<ProyectoPalabraClaveOutput> returnValue = convertProyectoPalabraClave(
        proyectoPalabraClaveService.updatePalabrasClave(proyectoId,
            convertProyectoPalabraClaveInputs(proyectoId, palabrasClave)));
    log.debug("updatePalabrasClave(Long proyectoId, List<ProyectoPalabraClaveInput> palabrasClave) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Devuelve una lista de {@link NotificacionProyectoExternoCVN}
   * 
   * @param id Identificador del {@link Proyecto}.
   * @return el listado de entidades {@link NotificacionProyectoExternoCVN} del
   *         {@link Proyecto}.
   */
  @GetMapping("/{id}/notificacionesproyectos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  public ResponseEntity<List<NotificacionProyectoExternoCVNOutput>> findAllNotificacionProyectoExternoCVN(
      @PathVariable Long id) {

    List<NotificacionProyectoExternoCVNOutput> notificacionProyectoExternoCVN = convert(
        notificacionProyectoExternoCVNService.findByProyectoId(id));

    return notificacionProyectoExternoCVN.isEmpty() ? ResponseEntity.noContent().build()
        : ResponseEntity.ok(
            notificacionProyectoExternoCVN);
  }

  /**
   * Obtiene todas las entidades {@link Proyecto} activas, que no estén en estado
   * borrador, en las que el usuario logueado está dentro del equipo o es un
   * responsable economico, paginadas y filtradas
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas.
   */
  @GetMapping(PATH_INVESTIGADOR)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-INV-VR')")
  public ResponseEntity<Page<Proyecto>> findAllInvestigador(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllInvestigador(String query, Pageable paging) - start");

    Page<Proyecto> page = service.findAllActivosInvestigador(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllInvestigador(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllInvestigador(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve la {@link Convocatoria} asociada al {@link Proyecto} con el id
   * indicado si el usuario tiene permisos de investigador.
   * 
   * @param id Identificador de {@link Proyecto}.
   * @return {@link Convocatoria}
   */
  @GetMapping("/{id}/convocatoria")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-INV-VR')")
  public ResponseEntity<ConvocatoriaTituloOutput> findConvocatoriaByProyectoIdAndUserIsInvestigador(
      @PathVariable Long id) {
    log.debug("findConvocatoriaByProyectoIdAndUserIsInvestigador(Long id) - start");

    ConvocatoriaTituloOutput returnValue = convert(
        convocatoriaService.findConvocatoriaByProyectoIdAndUserIsInvestigador(id));

    if (returnValue == null) {
      log.debug("findConvocatoriaByProyectoIdAndUserIsInvestigador(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findConvocatoriaByProyectoIdAndUserIsInvestigador(Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Devuelve una lista de {@link ProyectoDto} que se incorporarán a la baremación
   * de producción científica
   *
   * @param anioInicio año inicio de baremación
   * @param anioFin    año fin de baremación
   * @return lista de {@link ProyectoDto}
   */
  @GetMapping("/produccioncientifica/{anioInicio}/{anioFin}")
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-csp')) or hasAuthority('CSP-PRO-PRC-V')")
  public ResponseEntity<List<ProyectoDto>> findProyectosProduccionCientifica(@PathVariable Integer anioInicio,
      @PathVariable Integer anioFin) {
    log.debug("findProyectosProduccionCientifica(anioInicio, anioFin) - start");
    List<ProyectoDto> proyectos = service.findProyectosProduccionCientifica(anioInicio, anioFin);

    if (proyectos.isEmpty()) {
      log.debug("findProyectosProduccionCientifica(anioInicio, anioFin) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findProyectosProduccionCientifica(anioInicio, anioFin) - end");
    return new ResponseEntity<>(proyectos, HttpStatus.OK);
  }

  /**
   * Devuelve una lista de {@link ProyectoEquipoDto} que se incorporarán a la
   * baremación de producción científica
   *
   * @param proyectoId id de {@link Proyecto}
   * 
   * @return lista de {@link ProyectoEquipoDto}
   */
  @GetMapping("/produccioncientifica/equipo/{proyectoId}")
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-csp')) or hasAuthority('CSP-PRO-PRC-V')")
  public ResponseEntity<List<ProyectoEquipoDto>> findAllProyectoEquipoByProyectoId(@PathVariable Long proyectoId) {
    log.debug("findAllProyectoEquipoByProyectoId(proyectoId) - start");
    List<ProyectoEquipo> proyectos = proyectoEquipoService.findAllByProyectoId(proyectoId);

    if (proyectos.isEmpty()) {
      log.debug("findAllProyectoEquipoByProyectoId(proyectoId) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoEquipoByProyectoId(proyectoId) - end");
    return new ResponseEntity<>(convertProyectoEquipoToProyectoEquipoDto(proyectos), HttpStatus.OK);
  }

  /**
   * Obtiene la suma de importe concedido de cada {@link AnualidadGasto}
   * asociados a un {@link Proyecto} cuyo id coincide con el indicado.
   * 
   * @param proyectoId el identificador del {@link Proyecto}
   * @return suma de puntos del campo importeConcedido
   */
  @GetMapping("/produccioncientifica/totalimporteconcedido/{proyectoId}")
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-csp')) or hasAuthority('CSP-PRO-PRC-V')")
  public BigDecimal getTotalImporteConcedidoAnualidadGasto(@PathVariable Long proyectoId) {
    log.debug("findProyectosProduccionCientifica(proyectoId) - start");
    return proyectoAnualidadService.getTotalImporteConcedidoAnualidadGasto(proyectoId);
  }

  /**
   * Obtiene la suma de importe concedido de cada {@link AnualidadGasto} de costes
   * indirectos asociados a un {@link Proyecto} cuyo id coincide con el indicado.
   * 
   * @param proyectoId el identificador del {@link Proyecto}
   * @return suma de puntos del campo importeConcedido
   */
  @GetMapping("/produccioncientifica/totalimporteconcedidocostesindirectos/{proyectoId}")
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-csp')) or hasAuthority('CSP-PRO-PRC-V')")
  public BigDecimal getTotalImporteConcedidoAnualidadGastoCostesIndirectos(@PathVariable Long proyectoId) {
    log.debug("getTotalImporteConcedidoAnualidadGastoCostesIndirectos(proyectoId) - start");
    return proyectoAnualidadService.getTotalImporteConcedidoAnualidadGastoCostesIndirectos(proyectoId);
  }

  /**
   * Devuelve una lista filtrada de investigadores principales del
   * {@link Proyecto} en el momento actual.
   *
   * Son investiador principales los {@link ProyectoEquipo} que a fecha actual
   * tiene el {@link RolProyecto} con el flag {@link RolProyecto#rolPrincipal} a
   * <code>true</code>.
   * 
   * @param id Identificador del {@link Proyecto}.
   * @return la lista de personaRef de los investigadores principales del
   *         {@link Proyecto} en el momento actual.
   */
  @GetMapping(PATH_INVESTIGADORES_PRINCIPALES)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-EJEC-V', 'CSP-EJEC-E', 'CSP-EJEC-INV-VR')")
  public ResponseEntity<List<String>> findPersonaRefInvestigadoresPrincipales(@PathVariable Long id) {
    log.debug("findPersonaRefInvestigadoresPrincipales(Long id) - start");
    List<String> returnValue = proyectoEquipoService.findPersonaRefInvestigadoresPrincipales(id);
    log.debug("findPersonaRefInvestigadoresPrincipales(Long id) - end");
    return returnValue.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Obtiene los datos de proyectos competitivos de la persona.
   *
   * @param personasRef        Id de la persona.
   * @param onlyAsRolPrincipal Indica si solo se comprueba la participacion con un
   *                           rol principal
   * @param exludedProyectoId  Excluye el {@link Proyecto} de la consulta
   * @return el {@link ProyectosCompetitivosPersonas}.
   */
  @GetMapping(PATH_PROYECTOS_COMPETITIVOS_PERSONAS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-C', 'CSP-PRO-E', 'CSP-SOL-C', 'CSP-SOL-E', 'CSP-SOL-INV-C', 'CSP-SOL-INV-ER')")
  public ResponseEntity<ProyectosCompetitivosPersonas> getProyectosCompetitivosPersona(
      @RequestParam List<String> personasRef,
      @RequestParam Boolean onlyAsRolPrincipal,
      @RequestParam(required = false) Long exludedProyectoId) {
    log.debug(
        "getProyectosCompetitivosPersona(List<String> personasRef, Boolean onlyAsRolPrincipal, Long exludedProyectoId) - start");
    ProyectosCompetitivosPersonas returnValue = service.getProyectosCompetitivosPersonas(personasRef,
        onlyAsRolPrincipal, exludedProyectoId);
    log.debug(
        "getProyectosCompetitivosPersona(List<String> personasRef, Boolean onlyAsRolPrincipal, Long exludedProyectoId) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link AnualidadGasto}
   *
   * @param id Id del {@link Proyecto}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = PATH_ANUALIDAD_GASTOS, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E', 'CSP-PRO-INV-VR' )")
  public ResponseEntity<Void> hasAnualidadGastos(@PathVariable Long id) {
    log.debug("hasAnualidadGastos(Long id) - start");
    boolean returnValue = anualidadGastoService.existsByProyecto(id);
    log.debug("hasAnualidadGastos(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link AnualidadIngreso}
   *
   * @param id Id del {@link Proyecto}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = PATH_ANUALIDAD_INGRESOS, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E', 'CSP-PRO-INV-VR' )")
  public ResponseEntity<Void> hasAnualidadIngresos(@PathVariable Long id) {
    log.debug("hasAnualidadIngresos(Long id) - start");
    boolean returnValue = anualidadIngresoService.existsByProyecto(id);
    log.debug("hasAnualidadIngresos(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link GastoProyecto}
   *
   * @param id Id del {@link Proyecto}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = PATH_GASTOS_PROYECTO, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E', 'CSP-PRO-INV-VR' )")
  public ResponseEntity<Void> hasGastosProyecto(@PathVariable Long id) {
    log.debug("hasGastosProyecto(Long id) - start");
    boolean returnValue = gastoProyectoService.existsByProyecto(id);
    log.debug("hasGastosProyecto(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  private Page<ProyectoPalabraClaveOutput> convertProyectoPalabraClave(Page<ProyectoPalabraClave> page) {
    List<ProyectoPalabraClaveOutput> content = page.getContent().stream()
        .map(this::convert)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private List<ProyectoPalabraClaveOutput> convertProyectoPalabraClave(List<ProyectoPalabraClave> list) {
    return list.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  private ProyectoPalabraClaveOutput convert(ProyectoPalabraClave proyectoPalabraClave) {
    return modelMapper.map(proyectoPalabraClave, ProyectoPalabraClaveOutput.class);
  }

  private List<ProyectoPalabraClave> convertProyectoPalabraClaveInputs(Long proyectoId,
      List<ProyectoPalabraClaveInput> inputs) {
    return inputs.stream().map(input -> convert(proyectoId, input)).collect(Collectors.toList());
  }

  private ProyectoPalabraClave convert(Long proyectoId, ProyectoPalabraClaveInput input) {
    ProyectoPalabraClave entity = modelMapper.map(input, ProyectoPalabraClave.class);
    entity.setProyectoId(proyectoId);
    entity.setId(null);
    return entity;
  }

  private NotificacionProyectoExternoCVNOutput convert(NotificacionProyectoExternoCVN notificacionProyectoExternoCVN) {
    return modelMapper.map(notificacionProyectoExternoCVN, NotificacionProyectoExternoCVNOutput.class);
  }

  private List<NotificacionProyectoExternoCVNOutput> convert(List<NotificacionProyectoExternoCVN> list) {
    return list.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  private ConvocatoriaTituloOutput convert(Convocatoria convocatoria) {
    return modelMapper.map(convocatoria, ConvocatoriaTituloOutput.class);
  }

  private List<ProyectoEquipoDto> convertProyectoEquipoToProyectoEquipoDto(List<ProyectoEquipo> list) {
    return list.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  private ProyectoEquipoDto convert(ProyectoEquipo proyectoEquipo) {
    ProyectoEquipoDto proyectoEquipoDto = modelMapper.map(proyectoEquipo, ProyectoEquipoDto.class);
    Boolean ip = null != proyectoEquipo.getRolProyecto()
        ? ObjectUtils.defaultIfNull(proyectoEquipo.getRolProyecto().getRolPrincipal(), Boolean.FALSE)
        : Boolean.FALSE;
    proyectoEquipoDto.setIp(ip);
    return proyectoEquipoDto;
  }

  /**
   * Devuelve una lista paginada y/o filtrada {@link RequerimientoJustificacion}
   * que pertenezcan a un {@link Proyecto}.
   * 
   * @param id     identificador de {@link Proyecto}
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link RequerimientoJustificacion} activas
   *         paginadas y/o filtradas.
   */
  @GetMapping(PATH_REQUERIMIENTOS_JUSTIFICACION)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SJUS-V', 'CSP-SJUS-E')")
  public ResponseEntity<Page<RequerimientoJustificacionOutput>> findRequerimientosJustificacion(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findRequerimientosJustificacion(Long id, String query, Pageable paging) - start");

    Page<RequerimientoJustificacionOutput> page = requerimientoJustificacionConverter
        .convert(requerimientoJustificacionService.findAllByProyectoId(id, query, paging));

    if (page.isEmpty()) {
      log.debug("findRequerimientosJustificacion(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findRequerimientosJustificacion(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ProyectoConceptoGastoCodigoEc} permitidos del
   * {@link Proyecto}.
   *
   * @param id     Identificador de {@link Proyecto}.
   * @param paging pageable.
   * @return el listado de entidades {@link ProyectoConceptoGastoCodigoEc}
   *         paginadas y filtradas de la {@link Proyecto}.
   */
  @GetMapping(PATH_CODIGOS_ECONOMICOS_PERMITIDOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E', 'CSP-PRO-V')")
  public ResponseEntity<Page<ProyectoConceptoGastoCodigoEc>> findAllProyectoGastosCodigoEcPermitidos(
      @PathVariable Long id, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoGastosCodigoEcPermitidos(Long id, Pageable paging) - start");
    Page<ProyectoConceptoGastoCodigoEc> page = proyectoConceptoGastoCodigoEcService
        .findAllByProyectoAndPermitidoTrue(id, paging);
    if (page.isEmpty()) {
      log.debug("findAllProyectoGastosCodigoEcPermitidos(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoGastosCodigoEcPermitidos(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ProyectoConceptoGastoCodigoEc} no permitidos del
   * {@link Proyecto}.
   *
   * @param id     Identificador de {@link Proyecto}.
   * @param paging pageable.
   * @return el listado de entidades {@link ProyectoConceptoGastoCodigoEc}
   *         paginadas y filtradas de la {@link Proyecto}.
   */
  @GetMapping(PATH_CODIGOS_ECONOMICOS_NO_PERMITIDOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E','CSP-PRO-V')")
  public ResponseEntity<Page<ProyectoConceptoGastoCodigoEc>> findAllProyectoGastosCodigoEcNoPermitidos(
      @PathVariable Long id, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoGastosCodigoEcNoPermitidos(Long id, Pageable paging) - start");
    Page<ProyectoConceptoGastoCodigoEc> page = proyectoConceptoGastoCodigoEcService
        .findAllByProyectoAndPermitidoFalse(id, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoGastosCodigoEcNoPermitidos(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoGastosCodigoEcNoPermitidos(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
