package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioEquipoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioPeriodoPagoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
 * SolicitudProyectoSocioController
 */
@RestController
@RequestMapping("/solicitudproyectosocio")
@Slf4j
public class SolicitudProyectoSocioController {

  /** SolicitudProyectoSocioService service */
  private final SolicitudProyectoSocioService service;

  /** SolicitudProyectoSocioPeriodoPagoService service */
  private final SolicitudProyectoSocioPeriodoPagoService solicitudProyectoSocioPeriodoPagoService;
  /** SolicitudProyectoSocioPeriodoJustificacionService service */
  private final SolicitudProyectoSocioPeriodoJustificacionService solicitudProyectoSocioPeriodoJustificacionService;

  /** SolicitudProyectoSocioEquipo service */
  private final SolicitudProyectoSocioEquipoService solicitudProyectoEquipoSocioService;

  /**
   * Instancia un nuevo SolicitudProyectoSocioController.
   * 
   * @param solicitudProyectoSocioService                     {@link SolicitudProyectoSocioService}.
   * @param solicitudProyectoSocioPeriodoPagoService          {@link SolicitudProyectoSocioPeriodoPagoService}.
   * @param solicitudProyectoEquipoSocioService               {@link SolicitudProyectoSocioEquipoService}.
   * @param solicitudProyectoSocioPeriodoJustificacionService {@link SolicitudProyectoSocioPeriodoJustificacionService}.
   */
  public SolicitudProyectoSocioController(SolicitudProyectoSocioService solicitudProyectoSocioService,
      SolicitudProyectoSocioPeriodoJustificacionService solicitudProyectoSocioPeriodoJustificacionService,
      SolicitudProyectoSocioPeriodoPagoService solicitudProyectoSocioPeriodoPagoService,
      SolicitudProyectoSocioEquipoService solicitudProyectoEquipoSocioService) {
    this.service = solicitudProyectoSocioService;
    this.solicitudProyectoSocioPeriodoPagoService = solicitudProyectoSocioPeriodoPagoService;
    this.solicitudProyectoEquipoSocioService = solicitudProyectoEquipoSocioService;
    this.solicitudProyectoSocioPeriodoJustificacionService = solicitudProyectoSocioPeriodoJustificacionService;

  }

  /**
   * Crea nuevo {@link SolicitudProyectoSocio}
   * 
   * @param solicitudProyectoSocio {@link SolicitudProyectoSocio}. que se quiere
   *                               crear.
   * @return Nuevo {@link SolicitudProyectoSocio} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public ResponseEntity<SolicitudProyectoSocio> create(
      @Valid @RequestBody SolicitudProyectoSocio solicitudProyectoSocio) {
    log.debug("create(SolicitudProyectoSocio solicitudProyectoSocio) - start");
    SolicitudProyectoSocio returnValue = service.create(solicitudProyectoSocio);
    log.debug("create(SolicitudProyectoSocio solicitudProyectoSocio) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudProyectoSocio}.
   * 
   * @param solicitudProyectoSocio {@link SolicitudProyectoSocio} a actualizar.
   * @param id                     Identificador {@link SolicitudProyectoSocio} a
   *                               actualizar.
   * @return SolicitudProyectoSocio {@link SolicitudProyectoSocio} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public SolicitudProyectoSocio update(@Valid @RequestBody SolicitudProyectoSocio solicitudProyectoSocio,
      @PathVariable Long id) {
    log.debug("update(SolicitudProyectoSocio solicitudProyectoSocio, Long id) - start");

    solicitudProyectoSocio.setId(id);
    SolicitudProyectoSocio returnValue = service.update(solicitudProyectoSocio);
    log.debug("update(SolicitudProyectoSocio solicitudProyectoSocio, Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoSocio} con el id
   * indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoSocio}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("SolicitudProyectoSocio exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("SolicitudProyectoSocio exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("SolicitudProyectoSocio exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link SolicitudProyectoSocio} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoSocio}.
   * @return SolicitudProyectoSocio {@link SolicitudProyectoSocio} correspondiente
   *         al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V')")
  SolicitudProyectoSocio findById(@PathVariable Long id) {
    log.debug("SolicitudProyectoSocio findById(Long id) - start");
    SolicitudProyectoSocio returnValue = service.findById(id);
    log.debug("SolicitudProyectoSocio findById(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link SolicitudProyectoSocio} con id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoSocio}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve una lista paginada de {@link SolicitudProyectoSocioPeriodoPago}
   * 
   * @param id     Identificador de {@link SolicitudProyectoSocioPeriodoPago}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectosocioperiodopago")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V')")
  ResponseEntity<Page<SolicitudProyectoSocioPeriodoPago>> findAllSolicitudProyectoSocioPeriodoPago(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoSocioPeriodoPago(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoSocioPeriodoPago> page = solicitudProyectoSocioPeriodoPagoService
        .findAllBySolicitudProyectoSocio(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoSocioPeriodoPago(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoSocioPeriodoPago(Long id, String query, Pageable paging) - end");

    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /*
   * Devuelve una lista paginada de {@link SolicitudProyectoSocioEquipo}
   * 
   * @param id Identificador de {@link SolicitudProyectoSocioEquipo}.
   * 
   * @param query filtro de búsqueda.
   * 
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectosocioequipo")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V')")
  ResponseEntity<Page<SolicitudProyectoSocioEquipo>> findAllSolicitudProyectoSocioEquipo(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoSocioEquipo(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoSocioEquipo> page = solicitudProyectoEquipoSocioService.findAllBySolicitudProyectoSocio(id,
        query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoSocioEquipo(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoSocioEquipo(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /*
   * Devuelve una lista paginada de {@link
   * SolicitudProyectoSocioPeriodoJustificacion}
   * 
   * @param id Identificador de {@link
   * SolicitudProyectoSocioPeriodoJustificacion}.
   * 
   * @param query filtro de búsqueda.
   * 
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectosocioperiodojustificaciones")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V')")
  ResponseEntity<Page<SolicitudProyectoSocioPeriodoJustificacion>> findAllSolicitudProyectoSocioPeriodoJustificacion(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoSocioPeriodoJustificacion(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoSocioPeriodoJustificacion> page = solicitudProyectoSocioPeriodoJustificacionService
        .findAllBySolicitudProyectoSocio(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoSocioPeriodoJustificacion(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoSocioPeriodoJustificacion(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Indica si {@link SolicitudProyectoSocio} tiene
   * {@link SolicitudProyectoSocioPeriodoJustificacion},
   * {@link SolicitudProyectoSocioPeriodoPago} y/o
   * {@link SolicitudProyectoSocioEquipo} relacionadas.
   *
   * @param id Id de la {@link SolicitudProyectoSocio}.
   * @return True si tiene {@link SolicitudProyectoSocioPeriodoJustificacion},
   *         {@link SolicitudProyectoSocioPeriodoPago} y/o
   *         {@link SolicitudProyectoSocioEquipo} relacionadas. En caso contrario
   *         false
   */
  @RequestMapping(path = "/{id}/vinculaciones", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  ResponseEntity<Boolean> vinculaciones(@PathVariable Long id) {
    log.debug("vinculaciones(Long id) - start");
    Boolean returnValue = service.vinculaciones(id);
    log.debug("vinculaciones(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}