package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadFinanciadoraAjenaService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPresupuestoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * SolicitudProyectoEntidadFinanciadoraAjenaController
 */

@RestController
@RequestMapping("/solicitudproyectoentidadfinanciadoraajenas")
@Slf4j
public class SolicitudProyectoEntidadFinanciadoraAjenaController {

  /** SolicitudProyectoEntidadFinanciadoraAjena service */
  private final SolicitudProyectoEntidadFinanciadoraAjenaService service;
  /** SolicitudProyectoPresupuesto service */
  private final SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService;
  /** SolicitudProyectoEntidad service */
  private final SolicitudProyectoEntidadService solicitudProyectoEntidadService;

  public SolicitudProyectoEntidadFinanciadoraAjenaController(
      SolicitudProyectoEntidadFinanciadoraAjenaService solicitudProyectoEntidadFinanciadoraAjenaService,
      SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService,
      SolicitudProyectoEntidadService solicitudProyectoEntidadService) {
    this.service = solicitudProyectoEntidadFinanciadoraAjenaService;
    this.solicitudProyectoPresupuestoService = solicitudProyectoPresupuestoService;
    this.solicitudProyectoEntidadService = solicitudProyectoEntidadService;
  }

  /**
   * Devuelve el {@link SolicitudProyectoEntidadFinanciadoraAjena} con el id
   * indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * @return {@link SolicitudProyectoEntidadFinanciadoraAjena} correspondiente al
   *         id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public SolicitudProyectoEntidadFinanciadoraAjena findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    SolicitudProyectoEntidadFinanciadoraAjena returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea nuevo {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * 
   * @param solicitudProyectoEntidadFinanciadoraAjena {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   *                                                  que se quiere crear.
   * @return Nuevo {@link SolicitudProyectoEntidadFinanciadoraAjena} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public ResponseEntity<SolicitudProyectoEntidadFinanciadoraAjena> create(
      @Valid @RequestBody SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena) {
    log.debug("create(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena) - start");
    SolicitudProyectoEntidadFinanciadoraAjena returnValue = service.create(solicitudProyectoEntidadFinanciadoraAjena);
    log.debug("create(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link SolicitudProyectoEntidadFinanciadoraAjena} con el id
   * indicado.
   * 
   * @param solicitudProyectoEntidadFinanciadoraAjena {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *                                                  a actualizar.
   * @param id                                        id
   *                                                  {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *                                                  a actualizar.
   * @return {@link SolicitudProyectoEntidadFinanciadoraAjena} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public SolicitudProyectoEntidadFinanciadoraAjena update(@Validated({ Update.class,
      Default.class }) @RequestBody SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena,
      @PathVariable Long id) {
    log.debug(
        "update(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena, Long id) - start");
    solicitudProyectoEntidadFinanciadoraAjena.setId(id);
    SolicitudProyectoEntidadFinanciadoraAjena returnValue = service.update(solicitudProyectoEntidadFinanciadoraAjena);
    log.debug(
        "update(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena, Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link SolicitudProyectoEntidadFinanciadoraAjena} con el id
   * indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Comprueba la existencia de {@link SolicitudProyectoPresupuesto} asociados a
   * una {@link SolicitudProyectoEntidadFinanciadoraAjena}
   * 
   * @param id Id de la Solicitud
   * @return {@link HttpStatus#OK} si existe alguna relación,
   *         {@link HttpStatus#NO_CONTENT} en cualquier otro caso
   */
  @RequestMapping(path = "/{id}/solicitudproyectopresupuestos", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V')")
  public ResponseEntity<Void> hasSolicitudProyectoPresupuestoEntidad(@PathVariable Long id) {
    log.debug("hasSolicitudProyectoPresupuestoEntidad(Long id) - start");
    boolean returnValue = solicitudProyectoPresupuestoService.existsBySolicitudProyectoEntidadFinanciadoraAjena(id);

    log.debug("hasSolicitudProyectoPresupuestoEntidad(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link SolicitudProyectoEntidadFinanciadoraAjena} con el id
   * indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * @return {@link SolicitudProyectoEntidadFinanciadoraAjena} correspondiente al
   *         id.
   */
  @GetMapping("/{id}/solicitudproyectoentidad")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public SolicitudProyectoEntidad findBySolicitudProyectoEntidadFinanciadoraAjena(@PathVariable Long id) {
    log.debug("findBySolicitudProyectoEntidadFinanciadoraAjena(Long id) - start");
    SolicitudProyectoEntidad returnValue = solicitudProyectoEntidadService
        .findBySolicitudProyectoEntidadFinanciadoraAjena(id);
    log.debug("findBySolicitudProyectoEntidadFinanciadoraAjena(Long id) - end");
    return returnValue;
  }

}
