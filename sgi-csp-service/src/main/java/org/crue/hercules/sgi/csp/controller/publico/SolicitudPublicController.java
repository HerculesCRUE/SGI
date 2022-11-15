package org.crue.hercules.sgi.csp.controller.publico;

import java.util.UUID;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.SolicitanteExternoConverter;
import org.crue.hercules.sgi.csp.converter.SolicitudRrhhConverter;
import org.crue.hercules.sgi.csp.dto.SolicitanteExternoOutput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhOutput;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.SolicitanteExterno;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.model.SolicitudRrhh;
import org.crue.hercules.sgi.csp.service.EstadoSolicitudService;
import org.crue.hercules.sgi.csp.service.SolicitanteExternoService;
import org.crue.hercules.sgi.csp.service.SolicitudDocumentoService;
import org.crue.hercules.sgi.csp.service.SolicitudExternaService;
import org.crue.hercules.sgi.csp.service.SolicitudModalidadService;
import org.crue.hercules.sgi.csp.service.SolicitudRrhhService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(SolicitudPublicController.REQUEST_MAPPING)
@Slf4j
public class SolicitudPublicController {

  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER + "solicitudes";

  public static final String PATH_ID = PATH_DELIMITER + "{publicId}";

  public static final String PATH_DOCUMENTOS = PATH_ID + PATH_DELIMITER + "solicituddocumentos";
  public static final String PATH_HISTORICO_ESTADOS = PATH_ID + PATH_DELIMITER + "estadosolicitudes";
  public static final String PATH_MODALIDADES = PATH_ID + PATH_DELIMITER + "solicitudmodalidades";
  public static final String PATH_MODIFICABLE = PATH_ID + PATH_DELIMITER + "modificable";
  public static final String PATH_MODIFICABLE_ESTADO_DOCUMENTOS = PATH_ID + PATH_DELIMITER
      + "modificableestadoanddocumentos";
  public static final String PATH_PUBLIC_ID = PATH_DELIMITER + "publicId";

  /** Solicitud service */
  private final SolicitudService service;

  /** SolicitudModalidad service */
  private final SolicitudModalidadService solicitudModalidadService;

  /** EstadoSolicitud service */
  private final EstadoSolicitudService estadoSolicitudService;

  /** SolicitudDocumento service */
  private final SolicitudDocumentoService solicitudDocumentoService;

  /** SolicitudRrhh Service */
  private final SolicitudRrhhService solicitudRrhhService;

  /** SolicitudRrhh Converter */
  private final SolicitudRrhhConverter solicitudRrhhConverter;

  /** SolicitanteExterno Service */
  private final SolicitanteExternoService solicitanteExternoService;

  /** SolicitanteExterno Converter */
  private final SolicitanteExternoConverter solicitanteExternoConverter;

  /** SolicitudExterna Service */
  private final SolicitudExternaService solicitudExternaService;

  public SolicitudPublicController(
      SolicitudService solicitudService,
      SolicitudModalidadService solicitudModalidadService,
      EstadoSolicitudService estadoSolicitudService,
      SolicitudDocumentoService solicitudDocumentoService,
      SolicitudRrhhService solicitudRrhhService,
      SolicitudRrhhConverter solicitudRrhhConverter,
      SolicitanteExternoService solicitanteExternoService,
      SolicitanteExternoConverter solicitanteExternoConverter,
      SolicitudExternaService solicitudExternaService) {
    this.service = solicitudService;
    this.solicitudModalidadService = solicitudModalidadService;
    this.estadoSolicitudService = estadoSolicitudService;
    this.solicitudDocumentoService = solicitudDocumentoService;
    this.solicitudRrhhService = solicitudRrhhService;
    this.solicitudRrhhConverter = solicitudRrhhConverter;
    this.solicitanteExternoService = solicitanteExternoService;
    this.solicitanteExternoConverter = solicitanteExternoConverter;
    this.solicitudExternaService = solicitudExternaService;
  }

  /**
   * Crea nuevo {@link Solicitud}
   * 
   * @param solicitud {@link Solicitud} que se quiere crear.
   * @return Nuevo {@link Solicitud} creado.
   */
  @PostMapping
  public ResponseEntity<Solicitud> create(@Valid @RequestBody Solicitud solicitud) {
    log.debug("create(Solicitud solicitud) - start");
    Solicitud returnValue = service.createByExternalUser(solicitud);
    log.debug("create(Solicitud solicitud) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Solicitud}.
   * 
   * @param solicitud {@link Solicitud} a actualizar.
   * @param publicId  Identificador {@link Solicitud} a actualizar.
   * @return Solicitud {@link Solicitud} actualizado
   */
  @PutMapping(PATH_ID)
  public Solicitud update(@Valid @RequestBody Solicitud solicitud, @PathVariable String publicId) {
    log.debug("update(Solicitud solicitud, String publicId) - start");
    Solicitud returnValue = service.updateByExternalUser(publicId, solicitud);
    log.debug("update(Solicitud solicitud, String publicId) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Solicitud} con el id indicado.
   * 
   * @param publicId Identificador de {@link Solicitud}.
   * @return Solicitud {@link Solicitud} correspondiente al id
   */
  @GetMapping(PATH_ID)
  public Solicitud findById(@PathVariable String publicId) {
    log.debug("findById(String id) - start");
    Solicitud returnValue = service.findByPublicId(publicId);
    log.debug("findById(String id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link SolicitudModalidad} de la
   * {@link Solicitud}.
   * 
   * @param publicId Identificador de {@link Solicitud}.
   * @param query    filtro de búsqueda.
   * @param paging   pageable.
   * @return el listado de entidades {@link SolicitudModalidad} paginados y
   *         filtrados.
   */
  @GetMapping(PATH_MODALIDADES)
  public ResponseEntity<Page<SolicitudModalidad>> findAllSolicitudModalidad(@PathVariable String publicId,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudModalidad(String publicId, String query, Pageable paging) - start");
    Page<SolicitudModalidad> page = solicitudModalidadService.findAllBySolicitudPublicId(publicId, query, paging);
    log.debug("findAllSolicitudModalidad(String publicId, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link EstadoSolicitud} de la
   * {@link Solicitud}.
   * 
   * @param publicId Identificador de {@link Solicitud}.
   * @param paging   pageable.
   * @return el listado de entidades {@link EstadoSolicitud} paginados y
   *         filtrados.
   */
  @GetMapping(PATH_HISTORICO_ESTADOS)
  public ResponseEntity<Page<EstadoSolicitud>> findAllEstadoSolicitud(@PathVariable String publicId,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllEstadoSolicitud(Long publicId, Pageable paging) - start");
    Page<EstadoSolicitud> page = estadoSolicitudService.findAllBySolicitudPublicId(publicId, paging);
    log.debug("findAllEstadoSolicitud(Long publicId, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link SolicitudDocumento} de la
   * {@link Solicitud}.
   * 
   * @param publicId Identificador de {@link Solicitud}.
   * @param query    filtro de búsqueda.
   * @param paging   pageable.
   * @return el listado de entidades {@link SolicitudDocumento} paginados y
   *         filtrados.
   */
  @GetMapping(PATH_DOCUMENTOS)
  public ResponseEntity<Page<SolicitudDocumento>> findAllSolicitudDocumentos(@PathVariable String publicId,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudDocumentos(Long id, String query, Pageable paging) - start");
    Page<SolicitudDocumento> page = solicitudDocumentoService.findAllBySolicitudPublicId(publicId, query, paging);
    log.debug("findAllSolicitudDocumentos(Long id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Se hace el cambio de estado de "Borrador" a "Presentada".
   * 
   * @param id              Identificador de {@link Solicitud}.
   * @param estadoSolicitud nuevo {@link EstadoSolicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @PatchMapping("/{id}/cambiar-estado")
  public Solicitud cambiarEstado(@PathVariable String id, @RequestBody EstadoSolicitud estadoSolicitud) {
    log.debug("cambiarEstado(String id) - start");
    Solicitud returnValue = service.cambiarEstado(id, estadoSolicitud);
    log.debug("cambiarEstado(String id) - end");
    return returnValue;
  }

  /**
   * Recupera un {@link SolicitudRrhh} de una solicitud
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link SolicitudRrhh}
   */
  @GetMapping(path = "/{id}/solicitudrrhh")
  public ResponseEntity<SolicitudRrhhOutput> findSolicitudRrhh(@PathVariable String id) {
    log.debug("findSolicitudRrhh(String id) - start");
    SolicitudRrhh returnValue = solicitudRrhhService.findBySolicitudPublicId(id);
    log.debug("findSolicitudRrhh(String id) - end");
    return returnValue == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(solicitudRrhhConverter.convert(returnValue), HttpStatus.OK);
  }

  /**
   * Recupera un {@link SolicitanteExterno} de una solicitud
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link SolicitanteExterno}
   */
  @GetMapping(path = "/{id}/solicitanteexterno")
  public ResponseEntity<SolicitanteExternoOutput> findSolicitanteExterno(@PathVariable String id) {
    log.debug("findSolicitanteExterno(String id) - start");
    SolicitanteExterno returnValue = solicitanteExternoService.findBySolicitudPublicId(id);
    log.debug("findSolicitanteExterno(String id) - end");
    return returnValue == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(solicitanteExternoConverter.convert(returnValue), HttpStatus.OK);
  }

  /**
   * Devuelve el uuid de la {@link Solicitud}.
   *
   * @param uuid            codigo interno de la {@link Solicitud}.
   * @param numeroDocumento numero de documento del solicitante
   * @return el uuid de la {@link Solicitud}.
   */
  @GetMapping(PATH_PUBLIC_ID)
  public String getPublicId(@RequestParam String uuid, @RequestParam String numeroDocumento) {
    log.debug("getPublicId(String uuid, @RequestParam String numeroDocumento) - start");
    UUID returnValue = solicitudExternaService.findIdByUuidAndNumeroDocumento(uuid,
        numeroDocumento);
    log.debug("getPublicId(String uuid, @RequestParam String numeroDocumento) - end");
    return JSONObject.quote(returnValue.toString());
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Solicitud}
   * puede ser modificada por un investigador.
   * 
   * @param publicId Id del {@link Solicitud}.
   * @return HTTP-200 Si se permite modificación / HTTP-204 Si no se permite
   *         modificación
   */
  @RequestMapping(path = PATH_MODIFICABLE, method = RequestMethod.HEAD)
  public ResponseEntity<Solicitud> modificable(@PathVariable String publicId) {
    log.debug("modificable(String publicId) - start");
    Boolean returnValue = service.modificableByUsuarioExterno(publicId);
    log.debug("modificable(String publicId) - end");
    return returnValue.booleanValue() ? new ResponseEntity<>(HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Hace las comprobaciones necesarias para determinar si el estado y los
   * {@link SolicitudDocumento} de la {@link Solicitud}
   * pueden ser modificados por un investigador.
   * 
   * @param publicId Id del {@link Solicitud}.
   * @return HTTP-200 Si se permite modificación / HTTP-204 Si no se permite
   *         modificación
   */
  @RequestMapping(path = PATH_MODIFICABLE_ESTADO_DOCUMENTOS, method = RequestMethod.HEAD)
  public ResponseEntity<Void> modificableEstadoAndDocumentos(@PathVariable String publicId) {
    log.debug("modificableEstadoAndDocumentos(String publicId) - start");
    boolean returnValue = service.modificableEstadoAndDocumentosByUsuarioExterno(publicId);
    log.debug("modificableEstadoAndDocumentos(String publicId) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
