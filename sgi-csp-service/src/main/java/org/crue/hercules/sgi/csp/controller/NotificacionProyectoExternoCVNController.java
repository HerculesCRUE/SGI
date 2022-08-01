package org.crue.hercules.sgi.csp.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.NotificacionCVNEntidadFinanciadoraInput;
import org.crue.hercules.sgi.csp.dto.NotificacionCVNEntidadFinanciadoraOutput;
import org.crue.hercules.sgi.csp.dto.NotificacionProyectoExternoCVNAsociarAutorizacionInput;
import org.crue.hercules.sgi.csp.dto.NotificacionProyectoExternoCVNAsociarProyectoInput;
import org.crue.hercules.sgi.csp.dto.NotificacionProyectoExternoCVNInput;
import org.crue.hercules.sgi.csp.dto.NotificacionProyectoExternoCVNOutput;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.NotificacionCVNEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.service.NotificacionCVNEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.NotificacionProyectoExternoCVNService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * NotificacionProyectoExternoCVNController
 */
@RestController
@RequestMapping(NotificacionProyectoExternoCVNController.MAPPING)
@Slf4j
public class NotificacionProyectoExternoCVNController {
  public static final String MAPPING = "/notificacionesproyectosexternoscvn";
  private ModelMapper modelMapper;

  private final NotificacionProyectoExternoCVNService service;
  private final NotificacionCVNEntidadFinanciadoraService notificacionCVNEntidadFinanciadoraService;

  public NotificacionProyectoExternoCVNController(
      NotificacionProyectoExternoCVNService notificacionProyectoExternoCVNService,
      NotificacionCVNEntidadFinanciadoraService notificacionCVNEntidadFinanciadoraService,
      ModelMapper modelMapper) {
    this.service = notificacionProyectoExternoCVNService;
    this.notificacionCVNEntidadFinanciadoraService = notificacionCVNEntidadFinanciadoraService;
    this.modelMapper = modelMapper;
  }

  /**
   * Devuelve el {@link NotificacionProyectoExternoCVN} con el id indicado.
   * 
   * @param id Identificador de {@link NotificacionProyectoExternoCVN}.
   * @return {@link NotificacionProyectoExternoCVN} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CVPR-V', 'CSP-CVPR-E')")
  public NotificacionProyectoExternoCVNOutput findById(@PathVariable Long id) {
    log.debug("Autorizacion findById(Long id) - start");
    NotificacionProyectoExternoCVNOutput returnValue = convert(service.findById(id));
    log.debug("Autorizacion findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y/o filtrada
   * {@link NotificacionProyectoExternoCVN}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return la lista de entidades {@link NotificacionProyectoExternoCVN}
   *         paginadas y/o filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CVPR-V', 'CSP-CVPR-E')")
  public ResponseEntity<Page<NotificacionProyectoExternoCVNOutput>> findAll(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<NotificacionProyectoExternoCVN> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);

  }

  /**
   * Crea nuevo {@link NotificacionProyectoExternoCVN}
   * 
   * @param notificacionProyectoExternoCVN {@link NotificacionProyectoExternoCVN}
   *                                       que se quiere crear.
   * @return Nuevo {@link NotificacionProyectoExternoCVN} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CVPR-E')")
  public ResponseEntity<NotificacionProyectoExternoCVN> create(
      @Valid @RequestBody NotificacionProyectoExternoCVNInput notificacionProyectoExternoCVN) {
    log.debug("create(NotificacionProyectoExternoCVN notificacionProyectoExternoCVN) - start");

    service.create(convert(notificacionProyectoExternoCVN),
        convert(null, notificacionProyectoExternoCVN.getNotificacionesEntidadFinanciadoras()));

    log.debug("create(NotificacionProyectoExternoCVN notificacionProyectoExternoCVN) - end");

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  /**
   * Devuelve una lista paginada y filtrada
   * {@link NotificacionCVNEntidadFinanciadora} pertenecientes a la
   * {@link NotificacionProyectoExternoCVN}.
   *
   * @param id     Identificador de {@link NotificacionProyectoExternoCVN}.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link NotificacionProyectoExternoCVN}
   *         activas paginadas y
   *         filtradas.
   */
  @GetMapping("/{id}/notificacionescvnentidadfinanciadora")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CVPR-E' , 'CSP-CVPR-V')")
  public ResponseEntity<Page<NotificacionCVNEntidadFinanciadoraOutput>> findAllNotificacionCVNEntidadFinanciadora(
      @PathVariable Long id, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllNotificacionCVNEntidadFinanciadoraAll(Long id,Pageable paging) - start");
    Page<NotificacionCVNEntidadFinanciadoraOutput> page = convertPageEntidadFinancidaoraOutput(
        notificacionCVNEntidadFinanciadoraService
            .findAllByNotificacionProyectoExternoCvnId(id, paging));

    if (page.isEmpty()) {
      log.debug("findAllNotificacionCVNEntidadFinanciadora(Long id,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllNotificacionCVNEntidadFinanciadora(Long id,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Actualiza la {@link Autorizacion} de la
   * {@link NotificacionProyectoExternoCVN}
   * 
   * @param id                  Identificador de
   *                            {@link NotificacionProyectoExternoCVN}.
   * @param autorizacionAsociar la {@link Autorizacion} a asociar
   * @return {@link NotificacionProyectoExternoCVN} con la {@link Autorizacion}
   *         actualizada
   */
  @PatchMapping("/{id}/asociarautorizacion")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CVPR-E')")
  public NotificacionProyectoExternoCVNOutput asociarAutorizacion(@PathVariable Long id,
      @RequestBody NotificacionProyectoExternoCVNAsociarAutorizacionInput autorizacionAsociar) {
    log.debug(
        "asociarAutorizacion(Long id, NotificacionProyectoExternoCVNAsociarAutorizacionInput autorizacionAsociar) - start");

    NotificacionProyectoExternoCVNOutput returnValue = convert(
        service.asociarAutorizacion(convert(id, autorizacionAsociar)));

    log.debug(
        "asociarAutorizacion(Long id, NotificacionProyectoExternoCVNAsociarAutorizacionInput autorizacionAsociar) - end");
    return returnValue;
  }

  /**
   * Actualiza la {@link Autorizacion} de la
   * {@link NotificacionProyectoExternoCVN}
   * 
   * @param id Identificador de
   *           {@link NotificacionProyectoExternoCVN}.
   * @return {@link NotificacionProyectoExternoCVN} con la {@link Autorizacion}
   *         actualizada
   */
  @PatchMapping("/{id}/desasociarautorizacion")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CVPR-E')")
  public NotificacionProyectoExternoCVNOutput desasociarAutorizacion(@PathVariable Long id) {
    log.debug(
        "desasociarAutorizacion(Long id) - start");

    NotificacionProyectoExternoCVNOutput returnValue = convert(service.desasociarAutorizacion(id));

    log.debug(
        "desasociarAutorizacion(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el {@link Proyecto} de la {@link NotificacionProyectoExternoCVN}
   * 
   * @param id              Identificador de
   *                        {@link NotificacionProyectoExternoCVN}.
   * @param proyectoAsociar el {@link Proyecto} a asociar
   * @return {@link NotificacionProyectoExternoCVN} con el {@link Proyecto}
   *         actualizada
   */
  @PatchMapping("/{id}/asociarproyecto")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CVPR-E')")
  public NotificacionProyectoExternoCVNOutput asociarProyecto(@PathVariable Long id,
      @RequestBody NotificacionProyectoExternoCVNAsociarProyectoInput proyectoAsociar) {
    log.debug("asociarProyecto(Long id, NotificacionProyectoExternoCVNAsociarProyectoInput proyectoAsociar) - start");

    NotificacionProyectoExternoCVNOutput returnValue = convert(
        service.asociarProyecto(convert(id, proyectoAsociar)));

    log.debug("asociarProyecto(Long id, NotificacionProyectoExternoCVNAsociarProyectoInput proyectoAsociar) - end");
    return returnValue;
  }

  /**
   * Actualiza el {@link Proyecto} de la {@link NotificacionProyectoExternoCVN}
   * 
   * @param id Identificador de
   *           {@link NotificacionProyectoExternoCVN}.
   * @return {@link NotificacionProyectoExternoCVN} con el {@link Proyecto}
   *         actualizada
   */
  @PatchMapping("/{id}/desasociarproyecto")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CVPR-E')")
  public NotificacionProyectoExternoCVNOutput desasociarProyecto(@PathVariable Long id) {
    log.debug(
        "desasociarProyecto(Long id, NotificacionProyectoExternoCVNAsociarProyectoInput proyectoAsociar) - start");

    NotificacionProyectoExternoCVNOutput returnValue = convert(
        service.desasociarProyecto(id));

    log.debug("desasociarProyecto(Long id, NotificacionProyectoExternoCVNAsociarProyectoInput proyectoAsociar) - end");
    return returnValue;
  }

  private Page<NotificacionProyectoExternoCVNOutput> convert(Page<NotificacionProyectoExternoCVN> page) {
    List<NotificacionProyectoExternoCVNOutput> content = page.getContent().stream()
        .map(this::convert)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private NotificacionProyectoExternoCVNOutput convert(NotificacionProyectoExternoCVN notificacionProyectoExternoCVN) {
    return modelMapper.map(notificacionProyectoExternoCVN, NotificacionProyectoExternoCVNOutput.class);
  }

  private NotificacionProyectoExternoCVN convert(Long id,
      NotificacionProyectoExternoCVNInput notificacionProyectoExternoCVNInput) {
    NotificacionProyectoExternoCVN notificacionProyectoExternoCVN = modelMapper.map(
        notificacionProyectoExternoCVNInput, NotificacionProyectoExternoCVN.class);
    notificacionProyectoExternoCVN.setId(id);
    return notificacionProyectoExternoCVN;
  }

  private NotificacionProyectoExternoCVN convert(
      NotificacionProyectoExternoCVNInput notificacionProyectoExternoCVNInput) {
    return convert(null, notificacionProyectoExternoCVNInput);
  }

  private NotificacionProyectoExternoCVN convert(Long id,
      NotificacionProyectoExternoCVNAsociarAutorizacionInput notificacionProyectoExternoCVNAsociaAutorizacionInput) {
    NotificacionProyectoExternoCVN notificacionProyectoExternoCVN = modelMapper.map(
        notificacionProyectoExternoCVNAsociaAutorizacionInput, NotificacionProyectoExternoCVN.class);
    notificacionProyectoExternoCVN.setId(id);
    return notificacionProyectoExternoCVN;
  }

  private NotificacionProyectoExternoCVN convert(Long id,
      NotificacionProyectoExternoCVNAsociarProyectoInput notificacionProyectoExternoCVNAsociarProyectoInput) {
    NotificacionProyectoExternoCVN notificacionProyectoExternoCVN = modelMapper.map(
        notificacionProyectoExternoCVNAsociarProyectoInput, NotificacionProyectoExternoCVN.class);
    notificacionProyectoExternoCVN.setId(id);
    return notificacionProyectoExternoCVN;
  }

  private NotificacionCVNEntidadFinanciadora convert(Long id, NotificacionCVNEntidadFinanciadoraInput input) {
    NotificacionCVNEntidadFinanciadora entity = modelMapper.map(input, NotificacionCVNEntidadFinanciadora.class);
    entity.setId(id);
    return entity;
  }

  private List<NotificacionCVNEntidadFinanciadora> convert(Long notificacionId,
      List<NotificacionCVNEntidadFinanciadoraInput> inputs) {
    return inputs == null ? new LinkedList<>()
        : inputs.stream().map(input -> convert(notificacionId, input)).collect(Collectors.toList());
  }

  private NotificacionCVNEntidadFinanciadoraOutput convert(
      NotificacionCVNEntidadFinanciadora notificacionCVNEntidadFinanciadora) {
    return modelMapper.map(notificacionCVNEntidadFinanciadora, NotificacionCVNEntidadFinanciadoraOutput.class);
  }

  private Page<NotificacionCVNEntidadFinanciadoraOutput> convertPageEntidadFinancidaoraOutput(
      Page<NotificacionCVNEntidadFinanciadora> page) {
    List<NotificacionCVNEntidadFinanciadoraOutput> content = page.getContent().stream().map(this::convert)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}
