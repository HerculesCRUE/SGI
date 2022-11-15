package org.crue.hercules.sgi.csp.controller.publico;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.converter.ConvocatoriaFaseConverter;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaFaseOutput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaHitoOutput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaPalabraClaveOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoEquipoCategoriaProfesionalOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoEquipoNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoIPCategoriaProfesionalOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoIPNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPalabraClave;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoNivelAcademico;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.service.ConvocatoriaAreaTematicaService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaDocumentoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEnlaceService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadConvocanteService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadGestoraService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaFaseService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaHitoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPalabraClaveService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPartidaService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoSeguimientoCientificoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.csp.service.RequisitoEquipoCategoriaProfesionalService;
import org.crue.hercules.sgi.csp.service.RequisitoEquipoNivelAcademicoService;
import org.crue.hercules.sgi.csp.service.RequisitoIPCategoriaProfesionalService;
import org.crue.hercules.sgi.csp.service.RequisitoIPNivelAcademicoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaController
 */
@RestController
@RequestMapping(ConvocatoriaPublicController.REQUEST_MAPPING)
@Slf4j
public class ConvocatoriaPublicController {

  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER + "convocatorias";

  public static final String PATH_INVESTIGADOR = PATH_DELIMITER + "investigador";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  public static final String PATH_AREAS_TEMATICAS = PATH_ID + PATH_DELIMITER + "convocatoriaareatematicas";
  public static final String PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_EQUIPO = PATH_ID + PATH_DELIMITER
      + "categoriasprofesionalesrequisitosequipo";
  public static final String PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_IP = PATH_ID + PATH_DELIMITER
      + "categoriasprofesionalesrequisitosip";
  public static final String PATH_DOCUMENTOS = PATH_ID + PATH_DELIMITER + "convocatoriadocumentos";
  public static final String PATH_ENLACES = PATH_ID + PATH_DELIMITER + "convocatoriaenlaces";
  public static final String PATH_ENTIDADES_CONVOCANTES = PATH_ID + PATH_DELIMITER + "convocatoriaentidadconvocantes";
  public static final String PATH_ENTIDADES_FINANCIADORAS = PATH_ID + PATH_DELIMITER
      + "convocatoriaentidadfinanciadoras";
  public static final String PATH_ENTIDADES_GESTORAS = PATH_ID + PATH_DELIMITER + "convocatoriaentidadgestoras";
  public static final String PATH_FASES = PATH_ID + PATH_DELIMITER + "convocatoriafases";
  public static final String PATH_GASTOS = PATH_ID + PATH_DELIMITER + "convocatoriagastos";
  public static final String PATH_GASTOS_NO_PERMITIDOS = PATH_GASTOS + PATH_DELIMITER + "nopermitidos";
  public static final String PATH_GASTOS_PERMITIDOS = PATH_GASTOS + PATH_DELIMITER + "permitidos";
  public static final String PATH_HITOS = PATH_ID + PATH_DELIMITER + "convocatoriahitos";
  public static final String PATH_NIVELES_REQUISITOS_EQUIPO = PATH_ID + PATH_DELIMITER + "nivelesrequisitosequipo";
  public static final String PATH_NIVELES_REQUISITOS_IP = PATH_ID + PATH_DELIMITER + "nivelesrequisitosip";
  public static final String PATH_PALABRAS_CLAVE = PATH_ID + PATH_DELIMITER + "palabrasclave";
  public static final String PATH_PARTIDAS_PRESUPUESTARIAS = PATH_ID + PATH_DELIMITER
      + "convocatoria-partidas-presupuestarias";
  public static final String PATH_PERIODOS_JUSTIFICACION = PATH_ID + PATH_DELIMITER
      + "convocatoriaperiodojustificaciones";
  public static final String PATH_PERIODOS_SEGUIMIENTO_CIENTIFICO = PATH_ID + PATH_DELIMITER
      + "convocatoriaperiodoseguimientocientificos";
  public static final String PATH_TRAMITABLE = PATH_ID + PATH_DELIMITER + "tramitable";

  private ModelMapper modelMapper;
  private final ConvocatoriaService service;
  private final ConvocatoriaEntidadFinanciadoraService convocatoriaEntidadFinanciadoraService;
  private final ConvocatoriaFaseService convocatoriaFaseService;
  private final ConvocatoriaEntidadConvocanteService convocatoriaEntidadConvocanteService;
  private final ConvocatoriaFaseConverter convocatoriaFaseConverter;
  private final ConvocatoriaPeriodoJustificacionService convocatoriaPeriodoJustificacionService;
  private final ConvocatoriaHitoService convocatoriaHitoService;
  private final ConvocatoriaDocumentoService convocatoriaDocumentoService;
  private final ConvocatoriaPeriodoSeguimientoCientificoService convocatoriaPeriodoSeguimientoCientificoService;
  private final ConvocatoriaEnlaceService convocatoriaEnlaceService;
  private final ConvocatoriaPartidaService convocatoriaPartidaService;
  private final ConvocatoriaConceptoGastoService convocatoriaConceptoGastoService;
  private final ConvocatoriaEntidadGestoraService convocatoriaEntidadGestoraService;
  private final ConvocatoriaPalabraClaveService convocatoriaPalabraClaveService;
  private final ConvocatoriaAreaTematicaService convocatoriaAreaTematicaService;
  private final RequisitoEquipoCategoriaProfesionalService requisitoEquipoCategoriaProfesionalService;
  private final RequisitoIPCategoriaProfesionalService requisitoIPCategoriaProfesionalService;
  private final RequisitoEquipoNivelAcademicoService requisitoEquipoNivelAcademicoService;
  private final RequisitoIPNivelAcademicoService requisitoIPNivelAcademicoService;

  public ConvocatoriaPublicController(ConvocatoriaService convocatoriaService,
      ConvocatoriaEntidadConvocanteService convocatoriaEntidadConvocanteService,
      ConvocatoriaEntidadFinanciadoraService convocatoriaEntidadFinanciadoraService,
      ConvocatoriaFaseService convocatoriaFaseService,
      ConvocatoriaFaseConverter convocatoriaFaseConverter,
      ConvocatoriaPeriodoJustificacionService convocatoriaPeriodoJustificacionService,
      ConvocatoriaHitoService convocatoriaHitoService,
      ConvocatoriaDocumentoService convocatoriaDocumentoService,
      ConvocatoriaPeriodoSeguimientoCientificoService convocatoriaPeriodoSeguimientoCientificoService,
      ConvocatoriaEnlaceService convocatoriaEnlaceService,
      ConvocatoriaPartidaService convocatoriaPartidaService,
      ConvocatoriaConceptoGastoService convocatoriaConceptoGastoService,
      ConvocatoriaEntidadGestoraService convocatoriaEntidadGestoraService,
      ConvocatoriaPalabraClaveService convocatoriaPalabraClaveService,
      ConvocatoriaAreaTematicaService convocatoriaAreaTematicaService,
      RequisitoEquipoCategoriaProfesionalService requisitoEquipoCategoriaProfesionalService,
      RequisitoIPCategoriaProfesionalService requisitoIPCategoriaProfesionalService,
      RequisitoEquipoNivelAcademicoService requisitoEquipoNivelAcademicoService,
      RequisitoIPNivelAcademicoService requisitoIPNivelAcademicoService,
      ModelMapper modelMapper) {
    this.service = convocatoriaService;
    this.convocatoriaEntidadConvocanteService = convocatoriaEntidadConvocanteService;
    this.convocatoriaEntidadFinanciadoraService = convocatoriaEntidadFinanciadoraService;
    this.convocatoriaFaseService = convocatoriaFaseService;
    this.convocatoriaFaseConverter = convocatoriaFaseConverter;
    this.convocatoriaPeriodoJustificacionService = convocatoriaPeriodoJustificacionService;
    this.convocatoriaHitoService = convocatoriaHitoService;
    this.convocatoriaDocumentoService = convocatoriaDocumentoService;
    this.convocatoriaPeriodoSeguimientoCientificoService = convocatoriaPeriodoSeguimientoCientificoService;
    this.convocatoriaEnlaceService = convocatoriaEnlaceService;
    this.convocatoriaPartidaService = convocatoriaPartidaService;
    this.convocatoriaConceptoGastoService = convocatoriaConceptoGastoService;
    this.convocatoriaEntidadGestoraService = convocatoriaEntidadGestoraService;
    this.convocatoriaPalabraClaveService = convocatoriaPalabraClaveService;
    this.convocatoriaAreaTematicaService = convocatoriaAreaTematicaService;
    this.requisitoEquipoCategoriaProfesionalService = requisitoEquipoCategoriaProfesionalService;
    this.requisitoIPCategoriaProfesionalService = requisitoIPCategoriaProfesionalService;
    this.requisitoEquipoNivelAcademicoService = requisitoEquipoNivelAcademicoService;
    this.requisitoIPNivelAcademicoService = requisitoIPNivelAcademicoService;
    this.modelMapper = modelMapper;
  }

  /**
   * Devuelve el {@link Convocatoria} con el id indicado.
   *
   * @param id Identificador de {@link Convocatoria}.
   * @return Convocatoria {@link Convocatoria} correspondiente al id
   */
  @GetMapping(PATH_ID)
  public Convocatoria findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    Convocatoria returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link Convocatoria} con el id indicado.
   *
   * @param id Identificador de {@link Convocatoria}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = PATH_ID, method = RequestMethod.HEAD)
  public ResponseEntity<Void> exists(@PathVariable Long id) {
    log.debug("exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Convocatoria} activas.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Convocatoria} activas paginadas y
   *         filtradas.
   */
  @GetMapping(PATH_INVESTIGADOR)
  public ResponseEntity<Page<Convocatoria>> findAllPublicas(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllPublicas(String query,Pageable paging) - start");
    Page<Convocatoria> page = service.findAllPublicas(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllPublicas(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllPublicas(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaEntidadFinanciadora} de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaEntidadFinanciadora}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_ENTIDADES_FINANCIADORAS)
  public ResponseEntity<Page<ConvocatoriaEntidadFinanciadora>> findAllConvocatoriaEntidadFinanciadora(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
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
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaFase} de la
   * {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaFase}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_FASES)
  public ResponseEntity<Page<ConvocatoriaFaseOutput>> findAllConvocatoriaFases(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaFase(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaFaseOutput> page = this.convocatoriaFaseConverter
        .convert(convocatoriaFaseService.findAllByConvocatoria(id, query, paging));

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaFase(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaFase(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaEntidadConvocante} de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaEntidadConvocante}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_ENTIDADES_CONVOCANTES)
  public ResponseEntity<Page<ConvocatoriaEntidadConvocante>> findAllConvocatoriaEntidadConvocantes(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
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
   * Hace las comprobaciones necesarias para determinar si la {@link Convocatoria}
   * puede tramitarse.
   *
   * @param id Id del {@link Convocatoria}.
   * @return HTTP-200 si puede ser tramitada / HTTP-204 si no puede ser tramitada
   */
  @RequestMapping(path = PATH_TRAMITABLE, method = RequestMethod.HEAD)
  public ResponseEntity<Void> tramitable(@PathVariable Long id) {
    log.debug("registrable(Long id) - start");
    boolean returnValue = service.tramitable(id);
    log.debug("registrable(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaPeriodoJustificacion} de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaPeriodoJustificacion}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_PERIODOS_JUSTIFICACION)
  public ResponseEntity<Page<ConvocatoriaPeriodoJustificacion>> findAllConvocatoriaPeriodoJustificacion(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
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
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaHito} de la
   * {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaHito}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_HITOS)
  public ResponseEntity<Page<ConvocatoriaHitoOutput>> findAllConvocatoriaHito(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaHito(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaHitoOutput> page = this.convert(convocatoriaHitoService.findAllByConvocatoria(id, query, paging));

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaHito(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaHito(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaDocumento} de la
   * {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaDocumento}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_DOCUMENTOS)
  public ResponseEntity<Page<ConvocatoriaDocumento>> findAllConvocatoriaDocumento(@PathVariable Long id,
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
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaPeriodoSeguimientoCientifico} de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_PERIODOS_SEGUIMIENTO_CIENTIFICO)
  public ResponseEntity<Page<ConvocatoriaPeriodoSeguimientoCientifico>> findAllConvocatoriaPeriodoSeguimientoCientifico(
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
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaEnlace} de la
   * {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaEnlace}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_ENLACES)
  public ResponseEntity<Page<ConvocatoriaEnlace>> findAllConvocatoriaEnlace(@PathVariable Long id,
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
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaPartida} de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaPartida}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_PARTIDAS_PRESUPUESTARIAS)
  public ResponseEntity<Page<ConvocatoriaPartida>> findAllConvocatoriaPartida(@PathVariable Long id,
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
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaConceptoGasto}
   * permitidos de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaConceptoGasto}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_GASTOS_PERMITIDOS)
  public ResponseEntity<Page<ConvocatoriaConceptoGasto>> findAllConvocatoriaGastosPermitidos(@PathVariable Long id,
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
   * @return el listado de entidades {@link ConvocatoriaConceptoGasto}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_GASTOS_NO_PERMITIDOS)
  public ResponseEntity<Page<ConvocatoriaConceptoGasto>> findAllConvocatoriaGastosNoPermitidos(@PathVariable Long id,
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
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaEntidadGestora}
   * de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaEntidadGestora}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_ENTIDADES_GESTORAS)
  public ResponseEntity<Page<ConvocatoriaEntidadGestora>> findAllConvocatoriaEntidadGestora(@PathVariable Long id,
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
   * Devuelve las {@link ConvocatoriaPalabraClave} asociadas a la entidad
   * {@link Convocatoria} con el id indicado
   * 
   * @param id     Identificador de {@link Convocatoria}
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return {@link ConvocatoriaPalabraClave} correspondientes al id de la entidad
   *         {@link Convocatoria}
   */
  @GetMapping(PATH_PALABRAS_CLAVE)
  public Page<ConvocatoriaPalabraClaveOutput> findPalabrasClave(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findPalabrasClave(@PathVariable Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaPalabraClaveOutput> returnValue = convertConvocatoriaPalabraClave(
        convocatoriaPalabraClaveService.findByConvocatoriaId(id, query, paging));
    log.debug("findPalabrasClave(@PathVariable Long id, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaAreaTematica} de
   * la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaAreaTematica}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_AREAS_TEMATICAS)
  public ResponseEntity<Page<ConvocatoriaAreaTematica>> findAllConvocatoriaAreaTematica(@PathVariable Long id,
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
   * Devuelve los {@link RequisitoIPCategoriaProfesionalOutput} asociados a la
   * {@link Convocatoria} con el id indicado
   * 
   * @param id Identificador de {@link Convocatoria}
   * @return el {@link RequisitoIPCategoriaProfesionalOutput} correspondiente al
   *         id
   */
  @GetMapping(PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_IP)
  public List<RequisitoIPCategoriaProfesionalOutput> findRequisitosIpCategoriasProfesionales(@PathVariable Long id) {
    log.debug("findRequisitosIpCategoriasProfesionales(@PathVariable Long id) - start");
    List<RequisitoIPCategoriaProfesionalOutput> returnValue = convertRequisitoIPCategoriasProfesionales(
        requisitoIPCategoriaProfesionalService.findByConvocatoria(id));
    log.debug("findRequisitosIpCategoriasProfesionales(@PathVariable Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve las {@link RequisitoEquipoCategoriaProfesional} asociadas al
   * {@link RequisitoEquipo} con el id indicado
   * 
   * @param id Identificador de {@link RequisitoEquipoCategoriaProfesional}
   * @return RequisitoEquipoCategoriaProfesional
   *         {@link RequisitoEquipoCategoriaProfesional} correspondiente al id
   */
  @GetMapping(PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_EQUIPO)
  public List<RequisitoEquipoCategoriaProfesionalOutput> findCategoriasProfesionalesEquipo(@PathVariable Long id) {
    log.debug("findCategoriasProfesionalesEquipo(@PathVariable Long id) - start");
    List<RequisitoEquipoCategoriaProfesionalOutput> returnValue = convertRequisitoEquipoCategoriaProfesionales(
        requisitoEquipoCategoriaProfesionalService.findByConvocatoria(id));
    log.debug("findCategoriasProfesionalesEquipo(@PathVariable Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve los {@link RequisitoIPNivelAcademicoOutput} asociados al
   * {@link RequisitoIP} con el id indicado
   * 
   * @param id Identificador de la {@link Convocatoria}
   * @return los {@link RequisitoIPNivelAcademicoOutput} correspondiente a la
   *         {@link Convocatoria}
   */
  @GetMapping(PATH_NIVELES_REQUISITOS_IP)
  public List<RequisitoIPNivelAcademicoOutput> findRequisitoIPNivelesAcademicos(@PathVariable Long id) {
    log.debug("findRequisitoIPNivelesAcademicos(Long id) - start");
    List<RequisitoIPNivelAcademicoOutput> returnValue = convertRequisitoIPNivelesAcademicos(
        requisitoIPNivelAcademicoService.findByConvocatoria(id));
    log.debug("findRequisitoIPNivelesAcademicos(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve los {@link RequisitoEquipoNivelAcademicoOutput} asociados al
   * {@link RequisitoIP} con el id indicado
   * 
   * @param id Identificador de la {@link Convocatoria}
   * @return los {@link RequisitoEquipoNivelAcademicoOutput} correspondiente a la
   *         {@link Convocatoria}
   */
  @GetMapping(PATH_NIVELES_REQUISITOS_EQUIPO)
  public List<RequisitoEquipoNivelAcademicoOutput> findRequisitosEquipoNivelesAcademicos(@PathVariable Long id) {
    log.debug("findRequisitosEquipoNivelesAcademicos(Long id) - start");
    List<RequisitoEquipoNivelAcademicoOutput> returnValue = convertRequisitosEquipoNivelesAcademicos(
        requisitoEquipoNivelAcademicoService.findByConvocatoria(id));
    log.debug("findRequisitosEquipoNivelesAcademicos(Long id) - end");
    return returnValue;
  }

  private ConvocatoriaHitoOutput convert(ConvocatoriaHito convocatoriaHito) {
    return modelMapper.map(convocatoriaHito, ConvocatoriaHitoOutput.class);
  }

  private Page<ConvocatoriaHitoOutput> convert(Page<ConvocatoriaHito> page) {
    List<ConvocatoriaHitoOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private Page<ConvocatoriaPalabraClaveOutput> convertConvocatoriaPalabraClave(Page<ConvocatoriaPalabraClave> page) {
    List<ConvocatoriaPalabraClaveOutput> content = page.getContent().stream()
        .map(this::convert)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private ConvocatoriaPalabraClaveOutput convert(ConvocatoriaPalabraClave convocatoriaPalabraClave) {
    return modelMapper.map(convocatoriaPalabraClave, ConvocatoriaPalabraClaveOutput.class);
  }

  private List<RequisitoEquipoCategoriaProfesionalOutput> convertRequisitoEquipoCategoriaProfesionales(
      List<RequisitoEquipoCategoriaProfesional> entities) {
    return entities.stream().map(this::convert).collect(Collectors.toList());
  }

  private RequisitoEquipoCategoriaProfesionalOutput convert(RequisitoEquipoCategoriaProfesional entity) {
    return modelMapper.map(entity, RequisitoEquipoCategoriaProfesionalOutput.class);
  }

  private RequisitoEquipoNivelAcademicoOutput convert(RequisitoEquipoNivelAcademico entity) {
    return modelMapper.map(entity, RequisitoEquipoNivelAcademicoOutput.class);
  }

  private List<RequisitoEquipoNivelAcademicoOutput> convertRequisitosEquipoNivelesAcademicos(
      List<RequisitoEquipoNivelAcademico> entities) {
    return entities.stream().map(this::convert).collect(Collectors.toList());
  }

  private RequisitoIPNivelAcademicoOutput convert(RequisitoIPNivelAcademico entity) {
    return modelMapper.map(entity, RequisitoIPNivelAcademicoOutput.class);
  }

  private List<RequisitoIPNivelAcademicoOutput> convertRequisitoIPNivelesAcademicos(
      List<RequisitoIPNivelAcademico> entities) {
    return entities.stream().map(this::convert).collect(Collectors.toList());
  }

  private List<RequisitoIPCategoriaProfesionalOutput> convertRequisitoIPCategoriasProfesionales(
      List<RequisitoIPCategoriaProfesional> entities) {
    return entities.stream().map(this::convertCategoriaProfesional).collect(Collectors.toList());
  }

  private RequisitoIPCategoriaProfesionalOutput convertCategoriaProfesional(RequisitoIPCategoriaProfesional entity) {
    return modelMapper.map(entity, RequisitoIPCategoriaProfesionalOutput.class);
  }

}
