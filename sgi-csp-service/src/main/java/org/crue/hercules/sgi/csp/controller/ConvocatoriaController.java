package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.RequisitoIPCategoriaProfesionalOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoIPNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.service.ConvocatoriaAreaTematicaService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoCodigoEcService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaDocumentoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEnlaceService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadConvocanteService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadGestoraService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaFaseService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaHitoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPartidaService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoSeguimientoCientificoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.csp.service.RequisitoIPCategoriaProfesionalService;
import org.crue.hercules.sgi.csp.service.RequisitoIPNivelAcademicoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
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
 * ConvocatoriaController
 */
@RestController
@RequestMapping("/convocatorias")
@Slf4j
public class ConvocatoriaController {

  public static final String PATH_CATEGORIAS_PROFESIONALES = "/{id}/categoriasprofesionales";
  public static final String PATH_NIVELES = "/{id}/niveles";

  private ModelMapper modelMapper;

  /** ConvocatoriaService service */
  private final ConvocatoriaService service;

  /** ConvocatoriaEntidadFinanciadora service */
  private final ConvocatoriaEntidadFinanciadoraService convocatoriaEntidadFinanciadoraService;

  /** ConvocatoriaEntidadGestora service */
  private final ConvocatoriaEntidadGestoraService convocatoriaEntidadGestoraService;

  /** ConvocatoriaEntidadGestora service */
  private final ConvocatoriaAreaTematicaService convocatoriaAreaTematicaService;

  /** ConvocatoriaFase service */
  private final ConvocatoriaFaseService convocatoriaFaseService;

  /** ConvocatoriaDocumento service */
  private final ConvocatoriaDocumentoService convocatoriaDocumentoService;

  /** ConvocatoriaEnlace service */
  private final ConvocatoriaEnlaceService convocatoriaEnlaceService;

  /** ConvocatoriaEntidadConvocante service */
  private final ConvocatoriaEntidadConvocanteService convocatoriaEntidadConvocanteService;

  /** ConvocatoriaHitoservice */
  private final ConvocatoriaHitoService convocatoriaHitoService;

  /** ConvocatoriaPartidaservice */
  private final ConvocatoriaPartidaService convocatoriaPartidaService;

  /** ConvocatoriaPeriodoJustificacion service */
  private final ConvocatoriaPeriodoJustificacionService convocatoriaPeriodoJustificacionService;

  /** ConvocatoriaPeriodoSeguimientoCientifico service */
  private final ConvocatoriaPeriodoSeguimientoCientificoService convocatoriaPeriodoSeguimientoCientificoService;

  /** ConvocatoriaConceptoGastoService */
  private final ConvocatoriaConceptoGastoService convocatoriaConceptoGastoService;

  /** ConvocatoriaConceptoGastoCodigoEcService */
  private final ConvocatoriaConceptoGastoCodigoEcService convocatoriaConceptoGastoCodigoEcService;
  /** RequisitoIPNivelAcademicoService */
  private final RequisitoIPNivelAcademicoService requisitoIPNivelAcademicoService;

  /** RequisitoIPCategoriaProfesionalService */
  private final RequisitoIPCategoriaProfesionalService requisitoIPCategoriaProfesionalService;

  /**
   * Instancia un nuevo ConvocatoriaController.
   *
   * @param convocatoriaService                             {@link ConvocatoriaService}.
   * @param convocatoriaAreaTematicaService                 {@link ConvocatoriaAreaTematicaService}.
   * @param convocatoriaDocumentoService                    {@link ConvocatoriaDocumentoService}.
   * @param convocatoriaEnlaceService                       {@link ConvocatoriaEnlaceService}.
   * @param convocatoriaEntidadConvocanteService            {@link ConvocatoriaEntidadConvocanteService}.
   * @param convocatoriaEntidadFinanciadoraService          {@link ConvocatoriaEntidadFinanciadoraService}.
   * @param convocatoriaEntidadGestoraService               {@link ConvocatoriaEntidadGestoraService}.
   * @param convocatoriaFaseService                         {@link ConvocatoriaFaseService}
   * @param convocatoriaHitoService                         {@link ConvocatoriaHitoService}
   * @param convocatoriaPartidaService                      {@link ConvocatoriaPartidaService}
   * @param convocatoriaPeriodoJustificacionService         {@link ConvocatoriaPeriodoJustificacionService}.
   * @param convocatoriaPeriodoSeguimientoCientificoService {@link ConvocatoriaPeriodoSeguimientoCientificoService}
   * @param convocatoriaConceptoGastoService                {@link ConvocatoriaConceptoGastoService}
   * @param convocatoriaConceptoGastoCodigoEcService        {@link ConvocatoriaConceptoGastoCodigoEcService}
   * @param requisitoIPNivelAcademicoService                {@link RequisitoIPNivelAcademicoService}.
   * @param requisitoIPCategoriaProfesionalService          {@link RequisitoIPCategoriaProfesionalService}.
   * @param modelMapper                                     {@link ModelMapper}
   */
  public ConvocatoriaController(ConvocatoriaService convocatoriaService,
      ConvocatoriaAreaTematicaService convocatoriaAreaTematicaService,
      ConvocatoriaDocumentoService convocatoriaDocumentoService, ConvocatoriaEnlaceService convocatoriaEnlaceService,
      ConvocatoriaEntidadConvocanteService convocatoriaEntidadConvocanteService,
      ConvocatoriaEntidadFinanciadoraService convocatoriaEntidadFinanciadoraService,
      ConvocatoriaEntidadGestoraService convocatoriaEntidadGestoraService,
      ConvocatoriaFaseService convocatoriaFaseService, ConvocatoriaHitoService convocatoriaHitoService,
      ConvocatoriaPartidaService convocatoriaPartidaService,
      ConvocatoriaPeriodoJustificacionService convocatoriaPeriodoJustificacionService,
      ConvocatoriaPeriodoSeguimientoCientificoService convocatoriaPeriodoSeguimientoCientificoService,
      ConvocatoriaConceptoGastoService convocatoriaConceptoGastoService,
      ConvocatoriaConceptoGastoCodigoEcService convocatoriaConceptoGastoCodigoEcService,
      RequisitoIPNivelAcademicoService requisitoIPNivelAcademicoService,
      RequisitoIPCategoriaProfesionalService requisitoIPCategoriaProfesionalService, ModelMapper modelMapper) {
    this.service = convocatoriaService;
    this.convocatoriaAreaTematicaService = convocatoriaAreaTematicaService;
    this.convocatoriaDocumentoService = convocatoriaDocumentoService;
    this.convocatoriaEnlaceService = convocatoriaEnlaceService;
    this.convocatoriaEntidadConvocanteService = convocatoriaEntidadConvocanteService;
    this.convocatoriaEntidadFinanciadoraService = convocatoriaEntidadFinanciadoraService;
    this.convocatoriaEntidadGestoraService = convocatoriaEntidadGestoraService;
    this.convocatoriaFaseService = convocatoriaFaseService;
    this.convocatoriaHitoService = convocatoriaHitoService;
    this.convocatoriaPartidaService = convocatoriaPartidaService;
    this.convocatoriaPeriodoJustificacionService = convocatoriaPeriodoJustificacionService;
    this.convocatoriaPeriodoSeguimientoCientificoService = convocatoriaPeriodoSeguimientoCientificoService;
    this.convocatoriaConceptoGastoService = convocatoriaConceptoGastoService;
    this.convocatoriaConceptoGastoCodigoEcService = convocatoriaConceptoGastoCodigoEcService;
    this.requisitoIPNivelAcademicoService = requisitoIPNivelAcademicoService;
    this.requisitoIPCategoriaProfesionalService = requisitoIPCategoriaProfesionalService;
    this.modelMapper = modelMapper;
  }

  /**
   * Crea nuevo {@link Convocatoria}
   *
   * @param convocatoria {@link Convocatoria}. que se quiere crear.
   * @return Nuevo {@link Convocatoria} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-C')")
  public ResponseEntity<Convocatoria> create(@Valid @RequestBody Convocatoria convocatoria) {
    log.debug("create(Convocatoria convocatoria) - start");

    Convocatoria returnValue = service.create(convocatoria);

    log.debug("create(Convocatoria convocatoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Convocatoria}.
   *
   * @param convocatoria {@link Convocatoria} a actualizar.
   * @param id           Identificador {@link Convocatoria} a actualizar.
   * @return Convocatoria {@link Convocatoria} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public Convocatoria update(@Valid @RequestBody Convocatoria convocatoria, @PathVariable Long id) {
    log.debug("update(Convocatoria convocatoria, Long id) - start");

    convocatoria.setId(id);
    Convocatoria returnValue = service.update(convocatoria);

    log.debug("update(Convocatoria convocatoria, Long id) - end");
    return returnValue;
  }

  /**
   * Registra la {@link Convocatoria} con id indicado.
   *
   * @param id Identificador de {@link Convocatoria}.
   * @return {@link Convocatoria} actualizado.
   */
  @PatchMapping("/{id}/registrar")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public Convocatoria registrar(@PathVariable Long id) {
    log.debug("registrar(Long id) - start");
    Convocatoria returnValue = service.registrar(id);
    log.debug("registrar(Long id) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link Convocatoria} con id indicado.
   *
   * @param id Identificador de {@link Convocatoria}.
   * @return {@link Convocatoria} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-R')")
  public Convocatoria reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    Convocatoria returnValue = service.enable(id);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link Convocatoria} con id indicado.
   *
   * @param id Identificador de {@link Convocatoria}.
   * @return {@link Convocatoria} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-B')")
  public Convocatoria desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    Convocatoria returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Convocatoria}
   * puede ser modificada.
   *
   * @param id Id del {@link Convocatoria}.
   * @return HTTP-200 Si se permite modificación / HTTP-204 Si no se permite
   *         modificación
   */
  @RequestMapping(path = "/{id}/modificable", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V')")
  ResponseEntity<Convocatoria> modificable(@PathVariable Long id) {
    log.debug("modificable(Long id) - start");
    boolean returnValue = service.isRegistradaConSolicitudesOProyectos(id, null,
        new String[] { "CSP-CON-E", "CSP-CON-V" });
    log.debug("modificable(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Convocatoria}
   * puede pasar a estado 'Registrada'.
   *
   * @param id Id del {@link Convocatoria}.
   * @return HTTP-200 si puede ser registrada / HTTP-204 si no puede ser
   *         registrada
   */
  @RequestMapping(path = "/{id}/registrable", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-V', 'CSP-CON-INV-V', 'CSP-CON-E')")
  ResponseEntity<Convocatoria> registrable(@PathVariable Long id) {
    log.debug("registrable(Long id) - start");
    boolean returnValue = service.registrable(id);
    log.debug("registrable(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba la existencia del {@link Convocatoria} con el id indicado.
   *
   * @param id Identificador de {@link Convocatoria}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-V', 'CSP-CON-INV-V', 'CSP-CON-E')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("Convocatoria exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("Convocatoria exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("Convocatoria exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link Convocatoria} con el id indicado.
   *
   * @param id Identificador de {@link Convocatoria}.
   * @return Convocatoria {@link Convocatoria} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-V', 'CSP-CON-E', 'CSP-CON-INV-V', 'CSP-SOL-C', 'CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-B', 'CSP-SOL-R', 'CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E')")
  Convocatoria findById(@PathVariable Long id) {
    log.debug("Convocatoria findById(Long id) - start");
    Convocatoria returnValue = service.findById(id);
    log.debug("Convocatoria findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Convocatoria} activas.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Convocatoria} activas paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  ResponseEntity<Page<Convocatoria>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Convocatoria> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Convocatoria} activas.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Convocatoria} activas paginadas y
   *         filtradas.
   */
  @GetMapping("/investigador")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-INV-V')")
  ResponseEntity<Page<Convocatoria>> findAllInvestigador(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllInvestigador(String query,Pageable paging) - start");
    Page<Convocatoria> page = service.findAllInvestigador(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllInvestigador(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllInvestigador(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Convocatoria} activas
   * registradas restringidas a las del usuario.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Convocatoria} paginadas y filtradas.
   */
  @GetMapping("/restringidos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-V', 'CSP-SOL-C', 'CSP-PRO-C')")
  ResponseEntity<Page<Convocatoria>> findAllRestringidos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllRestringidos(String query, Pageable paging) - start");

    Page<Convocatoria> page = service.findAllRestringidos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllRestringidos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllRestringidos(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Convocatoria}.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Convocatoria} paginadas y filtradas.
   */
  @GetMapping("/todos/restringidos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C','CSP-CON-V', 'CSP-CON-E', 'CSP-CON-INV-V','CSP-CON-B', 'CSP-CON-R')")
  ResponseEntity<Page<Convocatoria>> findAllTodosRestringidos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodosRestringidos(String query,Pageable paging,) - start");

    Page<Convocatoria> page = service.findAllTodosRestringidos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodosRestringidos(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllTodosRestringidos(String query,Pageable paging ) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   *
   * CONVOCATORIA HITO
   *
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaHito} de la
   * {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriahitos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
  ResponseEntity<Page<ConvocatoriaHito>> findAllConvocatoriaHito(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaHito(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaHito> page = convocatoriaHitoService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaHito(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaHito(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Comprueba la existencia de {@link ConvocatoriaHito} relacionados con el id de
   * Convocatoria recibido.
   *
   * @param id Identificador de {@link Convocatoria}.
   * @return HTTP 200 si existe alguna relación y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/convocatoriahitos", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CONV-V', 'CSP-CON-E')")
  public ResponseEntity<?> hasConvocatoriaHitos(@PathVariable Long id) {
    log.debug("Convocatoria hasConvocatoriaHitos(Long id) - start");
    if (convocatoriaHitoService.existsByConvocatoriaId(id)) {
      log.debug("Convocatoria hasConvocatoriaHitos(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("Convocatoria hasConvocatoriaHitos(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   *
   * CONVOCATORIA PARTIDA PRESUPUESTARIA
   *
   */

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaPartidaPresupuestaria} de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoria-partidas-presupuestarias")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
  ResponseEntity<Page<ConvocatoriaPartida>> findAllConvocatoriaPartida(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaPartida(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaPartida> page = convocatoriaPartidaService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaPartida(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaPartida(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   *
   * CONVOCATORIA ENTIDAD FINANCIADORA
   *
   */

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaEntidadFinanciadora} de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaentidadfinanciadoras")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-V', 'CSP-CON-B','CSP-CON-R','CSP-CON-INV-V', 'CSP-CON-E', 'CSP-SOL-C', 'CSP-SOL-E', 'CSP-SOL-V', 'CSP-PRO-C')")
  ResponseEntity<Page<ConvocatoriaEntidadFinanciadora>> findAllConvocatoriaEntidadFinanciadora(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaEntidadFinanciadora(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaEntidadFinanciadora> page = convocatoriaEntidadFinanciadoraService.findAllByConvocatoria(id, query,
        paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaEntidadFinanciadora(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaEntidadFinanciadora(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   *
   * CONVOCATORIA ENTIDAD GESTORA
   *
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaEntidadGestora}
   * de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaentidadgestoras")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-V', 'CSP-CON-INV-V', 'CSP-CON-E')")
  ResponseEntity<Page<ConvocatoriaEntidadGestora>> findAllConvocatoriaEntidadGestora(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaEntidadGestora(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaEntidadGestora> page = convocatoriaEntidadGestoraService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaEntidadGestora(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaEntidadGestora(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   *
   * CONVOCATORIA FASE
   *
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaFase} de la
   * {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriafases")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-V', 'CSP-CON-INV-V', 'CSP-CON-E', 'CSP-SOL-C', 'CSP-PRO-C', 'CSP-CON-B', 'CSP-CON-R', 'CSP-CON-C')")
  ResponseEntity<Page<ConvocatoriaFase>> findAllConvocatoriaFases(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaFase(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaFase> page = convocatoriaFaseService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaFase(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaFase(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Comprueba la existencia de {@link ConvocatoriaFase} relacionados con el id de
   * Convocatoria recibido.
   *
   * @param id Identificador de {@link Convocatoria}.
   * @return HTTP 200 si existe alguna relación y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/convocatoriafases", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CONV-V')")
  public ResponseEntity<?> hasConvocatoriaFases(@PathVariable Long id) {
    log.debug("Convocatoria hasConvocatoriaFases(Long id) - start");
    if (convocatoriaFaseService.existsByConvocatoriaId(id)) {
      log.debug("Convocatoria hasConvocatoriaFases(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("Convocatoria hasConvocatoriaFases(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   *
   * CONVOCATORIA AREA TEMATICA
   *
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaAreaTematica} de
   * la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaareatematicas")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-V', 'CSP-CON-E', 'CSP-CON-INV-V', 'CSP-SOL-E', 'CSP-SOL-V')")
  ResponseEntity<Page<ConvocatoriaAreaTematica>> findAllConvocatoriaAreaTematica(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaAreaTematica(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaAreaTematica> page = convocatoriaAreaTematicaService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaAreaTematica(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaAreaTematica(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   *
   * CONVOCATORIA DOCUMENTO
   *
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaDocumento} de la
   * {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriadocumentos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
  ResponseEntity<Page<ConvocatoriaDocumento>> findAllConvocatoriaDocumento(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaDocumento(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaDocumento> page = convocatoriaDocumentoService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaDocumento(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaDocumento(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Comprueba la existencia de {@link ConvocatoriaDocumento} relacionados con el
   * id de Convocatoria recibido.
   *
   * @param id Identificador de {@link Convocatoria}.
   * @return HTTP 200 si existe alguna relación y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/convocatoriadocumentos", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CONV-V', 'CSP-CON-E')")
  public ResponseEntity<?> hasConvocatoriaDocumentos(@PathVariable Long id) {
    log.debug("Convocatoria hasConvocatoriaDocumentos(Long id) - start");
    if (convocatoriaDocumentoService.existsByConvocatoriaId(id)) {
      log.debug("Convocatoria hasConvocatoriaDocumentos(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("Convocatoria hasConvocatoriaDocumentos(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   *
   * CONVOCATORIA ENLACE
   *
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaEnlace} de la
   * {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaenlaces")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
  ResponseEntity<Page<ConvocatoriaEnlace>> findAllConvocatoriaEnlace(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaEnlace(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaEnlace> page = convocatoriaEnlaceService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaEnlace(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaEnlace(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Comprueba la existencia de {@link ConvocatoriaEnlace} relacionados con el id
   * de Convocatoria recibido.
   *
   * @param id Identificador de {@link Convocatoria}.
   * @return HTTP 200 si existe alguna relación y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/convocatoriaenlaces", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CONV-V', 'CSP-CON-E')")
  public ResponseEntity<?> hasConvocatoriaEnlaces(@PathVariable Long id) {
    log.debug("Convocatoria hasConvocatoriaEnlaces(Long id) - start");
    if (convocatoriaEnlaceService.existsByConvocatoriaId(id)) {
      log.debug("Convocatoria hasConvocatoriaEnlaces(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("Convocatoria hasConvocatoriaEnlaces(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   *
   * CONVOCATORIA ENTIDAD CONVOCANTE
   *
   */

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaEntidadConvocante} de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaentidadconvocantes")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-V', 'CSP-CON-INV-V', 'CSP-SOL-INV-C', 'CSP-CON-C','CSP-CON-E', 'CSP-CON-R',  'CSP-CON-B', 'CSP-SOL-C', 'CSP-SOL-E', 'CSP-SOL-V', 'CSP-PRO-C')")
  ResponseEntity<Page<ConvocatoriaEntidadConvocante>> findAllConvocatoriaEntidadConvocantes(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaEntidadConvocantes(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaEntidadConvocante> page = convocatoriaEntidadConvocanteService.findAllByConvocatoria(id, query,
        paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaEntidadConvocantes(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaEntidadConvocantes(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   *
   * CONVOCATORIA PERIODO JUSTIFICACION
   *
   */

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaPeriodoJustificacion} de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaperiodojustificaciones")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
  ResponseEntity<Page<ConvocatoriaPeriodoJustificacion>> findAllConvocatoriaPeriodoJustificacion(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaPeriodoJustificacion(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaPeriodoJustificacion> page = convocatoriaPeriodoJustificacionService.findAllByConvocatoria(id,
        query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaPeriodoJustificacion(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaPeriodoJustificacion(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   *
   * CONVOCATORIA PERIODO SEGUIMIENTO CIENTIFICO
   *
   */

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link convocatoriaPeriodoSeguimientoCientifico} de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaperiodoseguimientocientificos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V', 'CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E')")
  ResponseEntity<Page<ConvocatoriaPeriodoSeguimientoCientifico>> findAllConvocatoriaPeriodoSeguimientoCientifico(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaPeriodoSeguimientoCientifico(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaPeriodoSeguimientoCientifico> page = convocatoriaPeriodoSeguimientoCientificoService
        .findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaPeriodoSeguimientoCientifico(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaPeriodoSeguimientoCientifico(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   *
   * CONVOCATORIA GASTOS
   *
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaConceptoGasto}
   * permitidos de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriagastos/permitidos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E','CSP-CON-V', 'CSP-CON-INV-V')")
  ResponseEntity<Page<ConvocatoriaConceptoGasto>> findAllConvocatoriaGastosPermitidos(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaGastosPermitidos(Long id, Pageable paging) - start");
    Page<ConvocatoriaConceptoGasto> page = convocatoriaConceptoGastoService.findAllByConvocatoriaAndPermitidoTrue(id,
        paging);
    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaGastosPermitidos(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaGastosPermitidos(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaConceptoGasto}
   * no permitidos de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriagastos/nopermitidos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
  ResponseEntity<Page<ConvocatoriaConceptoGasto>> findAllConvocatoriaGastosNoPermitidos(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaGastosNoPermitidos(Long id, Pageable paging) - start");
    Page<ConvocatoriaConceptoGasto> page = convocatoriaConceptoGastoService.findAllByConvocatoriaAndPermitidoFalse(id,
        paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaGastosNoPermitidos(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaGastosNoPermitidos(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   *
   * CONVOCATORIA GASTOS CÓDIGO ECONÓMICO
   *
   */

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaConceptoGastoCodigoEc} permitidos de la
   * {@link ConvocatoriaConceptoGasto}.
   *
   * @param id     Identificador de {@link ConvocatoriaConceptoGasto}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriagastocodigoec/permitidos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V')")
  ResponseEntity<Page<ConvocatoriaConceptoGastoCodigoEc>> findAllConvocatoriaGastosCodigoEcPermitidos(
      @PathVariable Long id, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaGastosCodigoEcPermitidos(Long id, Pageable paging) - start");
    Page<ConvocatoriaConceptoGastoCodigoEc> page = convocatoriaConceptoGastoCodigoEcService
        .findAllByConvocatoriaAndPermitidoTrue(id, paging);
    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaGastosCodigoEcPermitidos(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaGastosCodigoEcPermitidos(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaConceptoGastoCodigoEc} no permitidos de la
   * {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriagastocodigoec/nopermitidos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V')")
  ResponseEntity<Page<ConvocatoriaConceptoGastoCodigoEc>> findAllConvocatoriaGastosCodigoEcNoPermitidos(
      @PathVariable Long id, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaGastosCodigoEcNoPermitidos(Long id, Pageable paging) - start");
    Page<ConvocatoriaConceptoGastoCodigoEc> page = convocatoriaConceptoGastoCodigoEcService
        .findAllByConvocatoriaAndPermitidoFalse(id, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaGastosCodigoEcNoPermitidos(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaGastosCodigoEcNoPermitidos(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Convocatoria}
   * puede tramitarse.
   *
   * @param id Id del {@link Convocatoria}.
   * @return HTTP-200 si puede ser tramitada / HTTP-204 si no puede ser tramitada
   */
  @RequestMapping(path = "/{id}/tramitable", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthority('CSP-SOL-INV-C')")
  ResponseEntity<Convocatoria> tramitable(@PathVariable Long id) {
    log.debug("registrable(Long id) - start");
    boolean returnValue = service.tramitable(id);
    log.debug("registrable(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Verifica si la convocatoria tiene asociada alguna solicitud
   * 
   * @param id Id de la {@link Convocatoria}
   * @return HTTP-200 si tinene alguna {@link Solicitud} asociada
   */
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  @RequestMapping(path = "/{id}/solicitudesreferenced", method = RequestMethod.HEAD)
  public ResponseEntity<Object> hasSolicitudesReferenced(@PathVariable Long id) {

    return this.service.hasAnySolicitudReferenced(id) ? ResponseEntity.ok().build()
        : ResponseEntity.noContent().build();
  }

  /**
   * verifica si la convocatoria tiene asociada algún {@link Proyecto}
   * 
   * @param id Id de la {@link Convocatoria}
   * @return HTTP-200 si tinene algún {@link Proyecto} asociado
   */
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  @RequestMapping(path = "/{id}/proyectosreferenced", method = RequestMethod.HEAD)
  public ResponseEntity<Object> hasProyectosReferenced(@PathVariable Long id) {

    return this.service.hasAnyProyectoReferenced(id) ? ResponseEntity.ok().build() : ResponseEntity.noContent().build();
  }

  /**
   * Devuelve los {@link RequisitoIPNivelAcademico} asociados al*
   * {@link RequisitoIP} con el id indicado**
   * 
   * @param id Identificador de {@link RequisitoIPNivelAcademico}*@return
   *           RequisitoIPNivelAcademico {@link RequisitoIPNivelAcademico}*
   *           correspondiente al id
   */

  @GetMapping(PATH_NIVELES)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-INV-C')")
  public List<RequisitoIPNivelAcademicoOutput> findNivelesAcademicos(@PathVariable Long id) {
    log.debug("findNivelesAcademicos(@PathVariable Long id) - start");
    List<RequisitoIPNivelAcademicoOutput> returnValue = convertRequisitoIPNivelesAcademicos(
        requisitoIPNivelAcademicoService.findByConvocatoria(id));
    log.debug("findNivelesAcademicos(@PathVariable Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve los {@link RequisitoIPNivelAcademico} asociados al
   * {@link RequisitoIP} con el id indicado
   * 
   * @param id Identificador de {@link RequisitoIPNivelAcademico}
   * @return RequisitoIPNivelAcademico {@link RequisitoIPNivelAcademico}
   *         correspondiente al id
   */
  @GetMapping(PATH_CATEGORIAS_PROFESIONALES)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-INV-C')")
  public List<RequisitoIPCategoriaProfesionalOutput> findCategoriasProfesionales(@PathVariable Long id) {
    log.debug("findCategoriasProfesionales(@PathVariable Long id) - start");
    List<RequisitoIPCategoriaProfesionalOutput> returnValue = convertRequisitoIPCategoriasProfesionales(
        requisitoIPCategoriaProfesionalService.findByConvocatoria(id));
    log.debug("findCategoriasProfesionales(@PathVariable Long id) - end");
    return returnValue;
  }

  private RequisitoIPNivelAcademicoOutput convert(RequisitoIPNivelAcademico entity) {
    return modelMapper.map(entity, RequisitoIPNivelAcademicoOutput.class);
  }

  private List<RequisitoIPNivelAcademicoOutput> convertRequisitoIPNivelesAcademicos(
      List<RequisitoIPNivelAcademico> entities) {
    return entities.stream().map((entity) -> convert(entity)).collect(Collectors.toList());
  }

  private List<RequisitoIPCategoriaProfesionalOutput> convertRequisitoIPCategoriasProfesionales(
      List<RequisitoIPCategoriaProfesional> entities) {
    return entities.stream().map((entity) -> convertCategoriaProfesional(entity)).collect(Collectors.toList());
  }

  private RequisitoIPCategoriaProfesionalOutput convertCategoriaProfesional(RequisitoIPCategoriaProfesional entity) {
    return modelMapper.map(entity, RequisitoIPCategoriaProfesionalOutput.class);
  }

  /**
   * Clona la convocatoria cuya fuente es la correspondiente al id pasado por el
   * path
   * 
   * @param id id de la {@link Convocatoria}
   * @return Convocatoria devuelve una {@link Convocatoria}
   */
  @PostMapping("/{id}/clone")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-C')")
  public ResponseEntity<Long> clone(@PathVariable Long id) {
    return new ResponseEntity<>(service.clone(id).getId(), HttpStatus.CREATED);
  }

}