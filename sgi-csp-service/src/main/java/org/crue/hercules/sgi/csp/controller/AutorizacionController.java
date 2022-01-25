package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.AutorizacionInput;
import org.crue.hercules.sgi.csp.dto.AutorizacionOutput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaTituloOutput;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.CertificadoAutorizacion;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.crue.hercules.sgi.csp.service.AutorizacionService;
import org.crue.hercules.sgi.csp.service.CertificadoAutorizacionService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.csp.service.EstadoAutorizacionService;
import org.crue.hercules.sgi.csp.service.NotificacionProyectoExternoCVNService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * AutorizacionController
 */
@RestController
@RequestMapping(AutorizacionController.REQUEST_MAPPING)
@Slf4j
public class AutorizacionController {

  /** El path que gestiona este controlador */
  public static final String REQUEST_MAPPING = "/autorizaciones";

  private ModelMapper modelMapper;

  private final AutorizacionService service;
  private final EstadoAutorizacionService estadoAutorizacionService;
  private final NotificacionProyectoExternoCVNService notificacionProyectoExternoService;
  private final CertificadoAutorizacionService certificadoAutorizacionService;
  private final ConvocatoriaService convocatoriaService;

  public AutorizacionController(ModelMapper modelMapper,
      AutorizacionService service,
      NotificacionProyectoExternoCVNService notificacionProyectoExternoService,
      EstadoAutorizacionService estadoAutorizacionService,
      CertificadoAutorizacionService certificadoAutorizacionService,
      ConvocatoriaService convocatoriaService) {
    this.modelMapper = modelMapper;
    this.service = service;
    this.notificacionProyectoExternoService = notificacionProyectoExternoService;
    this.estadoAutorizacionService = estadoAutorizacionService;
    this.certificadoAutorizacionService = certificadoAutorizacionService;
    this.convocatoriaService = convocatoriaService;
  }

  /**
   * Crea nuevo {@link Autorizacion}
   * 
   * @param autorizacion {@link Autorizacion} que se quiere crear.
   * @return Nuevo {@link Autorizacion}creado.
   */

  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-INV-C')")
  public ResponseEntity<AutorizacionOutput> create(@Valid @RequestBody AutorizacionInput autorizacion) {
    log.debug("create(Autorizacion autorizacion) - start");

    AutorizacionOutput returnValue = convert(service.create(convert(autorizacion)));

    log.debug("create(Autorizacion autorizacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Autorizacion}.
   * 
   * @param autorizacion {@link Autorizacion} a actualizar.
   * @param id           Identificador {@link Autorizacion} a actualizar.
   * @return Proyecto {@link Autorizacion} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-E','CSP-AUT-INV-ER')")
  public AutorizacionOutput update(@Valid @RequestBody AutorizacionInput autorizacion, @PathVariable Long id) {
    log.debug("update(Autorizacion autorizacion, Long id) - start");
    AutorizacionOutput returnValue = convert(service.update(convert(id, autorizacion)));
    log.debug("update(Autorizacion autorizacion, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Autorizacion} con el id indicado.
   * 
   * @param id Identificador de {@link Autorizacion}.
   * @return {@link Autorizacion} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-E', 'CSP-AUT-V', 'CSP-AUT-INV-ER')")
  public AutorizacionOutput findById(@PathVariable Long id) {
    log.debug("Autorizacion findById(Long id) - start");
    AutorizacionOutput returnValue = convert(service.findById(id));
    log.debug("Autorizacion findById(Long id) - end");
    return returnValue;
  }

  @PatchMapping("/{id}/cambiar-estado")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-E', 'CSP-AUT-INV-ER')")
  public AutorizacionOutput cambiarEstado(@PathVariable Long id, @RequestBody EstadoAutorizacion estadoAutorizacion) {
    log.debug("cambiarEstado(Long id) - start");

    AutorizacionOutput returnValue = convert(service.cambiarEstado(id, estadoAutorizacion));

    log.debug("cambiarEstado(Long id) - end");
    return returnValue;
  }

  /**
   * Pasa la {@link Autorizacion} al estado presentada.
   * 
   * @param id Identificador de {@link Autorizacion}.
   * @return {@link Autorizacion} actualizado.
   */
  @PatchMapping("/{id}/presentar")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-INV-C', 'CSP-AUT-INV-ER')")
  public AutorizacionOutput presentar(@PathVariable Long id) {
    log.debug("presentar(Long id)) - start");

    AutorizacionOutput returnValue = convert(service.presentar(id));

    log.debug("presentar(Long id) - end");
    return returnValue;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Autorizacion}
   * puede pasar a estado 'Presentada'.
   *
   * @param id Id del {@link Autorizacion}.
   * @return HTTP-200 si puede ser presentada / HTTP-204 si no puede ser
   *         presentada
   */
  @RequestMapping(path = "/{id}/presentable", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-E', 'CSP-AUT-INV-C', 'CSP-AUT-INV-ER')")
  public ResponseEntity<AutorizacionOutput> presentable(@PathVariable Long id) {
    log.debug("presentable(Long id) - start");
    boolean returnValue = service.presentable(id);
    log.debug("presentable(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Autorizacion} activas.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Autorizacion} activas paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-E', 'CSP-AUT-V')")
  public ResponseEntity<Page<AutorizacionOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<AutorizacionOutput> page = convert(service.findAll(query, paging));

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-B','CSP-AUT-INV-BR')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Autorizacion} que puede
   * visualizar
   * un investigador paginadas y filtradas.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Autorizacion} que puede visualizar un
   *         investigador paginadas y filtradas.
   */
  @GetMapping("/investigador")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-INV-C', 'CSP-AUT-INV-ER')")
  public ResponseEntity<Page<AutorizacionOutput>> findAllInvestigador(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllInvestigador(String query, Pageable paging) - start");
    Page<AutorizacionOutput> page = convert(service.findAllInvestigador(query, paging));

    if (page.isEmpty()) {
      log.debug("findAllInvestigador(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllInvestigador(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Comprueba si existen datos vinculados a {@link Autorizacion} de
   * {@link NotificacionProyectoExternoCVN}
   *
   * @param id Id del {@link Autorizacion}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/vinculacionesnotificacionesproyectosexternos", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-E','CSP-AUT-INV-C', 'CSP-AUT-INV-ER','CSP-AUT-V')")
  public ResponseEntity<Void> hasAutorizacionNotificacionProyectoExterno(@PathVariable Long id) {
    log.debug("hasAutorizacionNotificacionProyectoExterno(Long id) - start");
    boolean returnValue = notificacionProyectoExternoService.existsByAutorizacionId(id);
    log.debug("hasAutorizacionNotificacionProyectoExterno(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link EstadoAutorizacion} de la
   * {@link Autorizacion}.
   * 
   * @param id     Identificador de {@link Autorizacion}.
   * @param paging pageable.
   * @return el listado de entidades {@link EstadoAutorizacion} paginados y
   *         filtrados.
   */
  @GetMapping("/{id}/estados")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-E','CSP-AUT-INV-C', 'CSP-AUT-INV-ER','CSP-AUT-V')")
  public ResponseEntity<Page<EstadoAutorizacion>> findAllEstadoAutorizacion(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllEstadoAutorizacion(Long id, Pageable paging) - start");
    Page<EstadoAutorizacion> page = estadoAutorizacionService.findAllByAutorizacion(id, paging);

    if (page.isEmpty()) {
      log.debug("findAllEstadoAutorizacion(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllEstadoAutorizacion(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * Devuelve una lista paginada y filtrada de {@link CertificadoAutorizacion} de
   * la
   * {@link Autorizacion}.
   * 
   * @param id     Identificador de {@link Autorizacion}.
   * @param paging pageable.
   * @return el listado de entidades {@link EstadoAutorizacion} paginados y
   *         filtrados.
   */
  @GetMapping("/{id}/certificados")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-E','CSP-AUT-INV-C', 'CSP-AUT-INV-ER','CSP-AUT-V')")
  public ResponseEntity<Page<CertificadoAutorizacion>> findAllCertificadoAutorizacion(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllEstadoAutorizacion(Long id, Pageable paging) - start");
    Page<CertificadoAutorizacion> page = certificadoAutorizacionService.findAllByAutorizacion(id, paging);

    if (page.isEmpty()) {
      log.debug("findAllEstadoAutorizacion(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllEstadoAutorizacion(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * Comprueba si la {@link Autorizacion} tiene asociado algun
   * {@link CertificadoAutorizacion} con el campo
   * visible a 'true'.
   * 
   * @param id identificador del {@link Autorizacion}
   * @return {@link ResponseEntity} conteniendo el estado de la respuesta, 200 si
   *         contiene, 204 si no contiene.
   */
  @RequestMapping(path = "/{id}/hascertificadoautorizacionvisible", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-E','CSP-AUT-INV-C', 'CSP-AUT-INV-ER','CSP-AUT-V')")
  public ResponseEntity<Void> hasCertificadoAutorizacionVisible(@PathVariable Long id) {
    log.debug("hasCertificadoAutorizacionVisible(Long id) - start");
    boolean returnValue = certificadoAutorizacionService.hasCertificadoAutorizacionVisible(id);

    log.debug("hasCertificadoAutorizacionVisible(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve la {@link Convocatoria} asociada a la {@link Autorizacion} con el id
   * indicado si el usuario que realiza la peticion es el solicitante de la
   * {@link Autorizacion}.
   * 
   * @param id Identificador de {@link Autorizacion}.
   * @return {@link Convocatoria} correspondiente a la {@link Autorizacion}.
   */
  @GetMapping("/{id}/convocatoria")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-AUT-INV-ER')")
  public ResponseEntity<ConvocatoriaTituloOutput> findConvocatoriaByAutorizacionId(@PathVariable Long id) {
    log.debug("findConvocatoriaByAutorizacionId(Long id) - start");

    ConvocatoriaTituloOutput returnValue = convert(convocatoriaService.findByAutorizacionIdAndUserIsSolicitante(id));

    if (returnValue == null) {
      log.debug("findConvocatoriaByAutorizacionId(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findConvocatoriaByAutorizacionId(Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  private AutorizacionOutput convert(Autorizacion autorizacion) {
    return modelMapper.map(autorizacion, AutorizacionOutput.class);
  }

  private Autorizacion convert(AutorizacionInput autorizacionInput) {
    return convert(null, autorizacionInput);
  }

  private Autorizacion convert(Long id, AutorizacionInput autorizacionInput) {
    Autorizacion autorizacion = modelMapper.map(autorizacionInput, Autorizacion.class);
    autorizacion.setId(id);
    return autorizacion;
  }

  private Page<AutorizacionOutput> convert(Page<Autorizacion> page) {
    List<AutorizacionOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private ConvocatoriaTituloOutput convert(Convocatoria convocatoria) {
    return modelMapper.map(convocatoria, ConvocatoriaTituloOutput.class);
  }

}
