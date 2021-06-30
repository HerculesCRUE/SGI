package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.service.ProyectoSocioEquipoService;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoPagoService;
import org.crue.hercules.sgi.csp.service.ProyectoSocioService;
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
 * ProyectoSocioController
 */
@RestController
@RequestMapping("/proyectosocios")
@Slf4j
public class ProyectoSocioController {

  /** ProyectoSocioService service */
  private final ProyectoSocioService service;

  /** ProyectoSocioEquipoService service */
  private final ProyectoSocioEquipoService proyectoSocioEquipoService;

  /** ProyectoSocioPeriodoPagoService service */
  private final ProyectoSocioPeriodoPagoService proyectoSocioPeriodoPagoService;

  /** ProyectoSocioPeriodoJustificacionService service */
  private final ProyectoSocioPeriodoJustificacionService proyectoSocioPeriodoJustificacionService;

  /**
   * Instancia un nuevo ProyectoSocioController.
   * 
   * @param proyectoSocioService                     {@link ProyectoSocioService}.
   * @param proyectoSocioEquipoService               {@link ProyectoSocioEquipoService}
   * @param proyectoSocioService                     {@link ProyectoSocioService}.
   * @param proyectoSocioPeriodoPagoService          {@link ProyectoSocioPeriodoPagoService}.
   * @param proyectoSocioPeriodoJustificacionService {@link ProyectoSocioPeriodoJustificacionService}.
   */
  public ProyectoSocioController(ProyectoSocioService proyectoSocioService,
      ProyectoSocioEquipoService proyectoSocioEquipoService,
      ProyectoSocioPeriodoPagoService proyectoSocioPeriodoPagoService,
      ProyectoSocioPeriodoJustificacionService proyectoSocioPeriodoJustificacionService) {
    this.service = proyectoSocioService;
    this.proyectoSocioEquipoService = proyectoSocioEquipoService;
    this.proyectoSocioPeriodoPagoService = proyectoSocioPeriodoPagoService;
    this.proyectoSocioPeriodoJustificacionService = proyectoSocioPeriodoJustificacionService;
  }

  /**
   * Crea nuevo {@link ProyectoSocio}
   * 
   * @param proyectoSocio {@link ProyectoSocio} que se quiere crear.
   * @return Nuevo {@link ProyectoSocio} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoSocio> create(@Valid @RequestBody ProyectoSocio proyectoSocio) {
    log.debug("create(ProyectoSocio proyectoSocio) - start");
    ProyectoSocio returnValue = service.create(proyectoSocio);
    log.debug("create(ProyectoSocio proyectoSocio) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link ProyectoSocio}.
   * 
   * @param proyectoSocio {@link ProyectoSocio} a actualizar.
   * @param id            Identificador {@link ProyectoSocio} a actualizar.
   * @return {@link ProyectoSocio} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoSocio update(@Valid @RequestBody ProyectoSocio proyectoSocio, @PathVariable Long id) {
    log.debug("update(ProyectoSocio proyectoSocio, Long id) - start");
    proyectoSocio.setId(id);
    ProyectoSocio returnValue = service.update(proyectoSocio);
    log.debug("update(ProyectoSocio proyectoSocio, Long id) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link ProyectoSocio} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoSocio}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Comprueba la existencia del {@link ProyectoSocio} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoSocio}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link ProyectoSocio} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoSocio}.
   * @return {@link ProyectoSocio} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ProyectoSocio findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoSocio returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada de {@link ProyectoSocioEquipo}**
   * 
   * @param id     Identificador de {@link ProyectoSocio}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */

  @GetMapping("/{id}/proyectosocioequipos")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<Page<ProyectoSocioEquipo>> findAllProyectoSocioEquipo(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoSocioEquipo(Long id, String query, Pageable paging) - start");
    Page<ProyectoSocioEquipo> page = proyectoSocioEquipoService.findAllByProyectoSocio(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoSocioEquipo(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoSocioEquipo(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link SolicitudProyectoSocioPeriodoPago}
   * 
   * @param id     Identificador de {@link SolicitudProyectoSocioPeriodoPago}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectosocioperiodopagos")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<Page<ProyectoSocioPeriodoPago>> findAllProyectoSocioPeriodoPago(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoSocioPeriodoPago(Long id, String query, Pageable paging) - start");
    Page<ProyectoSocioPeriodoPago> page = proyectoSocioPeriodoPagoService.findAllByProyectoSocio(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoSocioPeriodoPago(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoSocioPeriodoPago(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ProyectoSocioPeriodoJustificacion} de la {@link ProyectoSocio}.
   * 
   * @param id     Identificador de {@link ProyectoSocio}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectosocioperiodojustificaciones")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<Page<ProyectoSocioPeriodoJustificacion>> findAllProyectoSocioPeriodoJustificaciones(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoSocioPeriodoJustificaciones(Long id, String query, Pageable paging) - start");
    Page<ProyectoSocioPeriodoJustificacion> page = proyectoSocioPeriodoJustificacionService.findAllByProyectoSocio(id,
        query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoSocioPeriodoJustificaciones(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoSocioPeriodoJustificaciones(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Indica si {@link ProyectoSocio} tiene {@link ProyectoSocioEquipo},
   * {@link ProyectoSocioPeriodoPago},
   * {@link ProyectoSocioPeriodoJustificacionDocumento} y/o
   * {@link ProyectoSocioPeriodoJustificacion} relacionadas.
   *
   * @param id Id de la {@link Proyecto}.
   * @return True si tiene {@link ProyectoSocioEquipo},
   *         {@link ProyectoSocioPeriodoPago},
   *         {@link ProyectoSocioPeriodoJustificacionDocumento} y/o
   *         {@link ProyectoSocioPeriodoJustificacion} relacionadas. En caso
   *         contrario false
   */
  @RequestMapping(path = "/{id}/vinculaciones", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<Boolean> vinculaciones(@PathVariable Long id) {
    log.debug("vinculaciones(Long id) - start");
    Boolean returnValue = service.vinculaciones(id);
    log.debug("vinculaciones(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
