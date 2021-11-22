package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPresupuestoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
 * SolicitudProyectoController
 */
@RestController
@RequestMapping("/solicitudproyecto")
@Slf4j
public class SolicitudProyectoController {

  /** SolicitudProyectoService service */
  private final SolicitudProyectoService service;

  /** SolicitudProyectoPresupuestoService service */
  private final SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService;

  /** SolicitudProyectoPresupuestoService service */
  private final SolicitudProyectoSocioService solicitudProyectoSocioService;

  /**
   * Instancia un nuevo SolicitudProyectoController.
   * 
   * @param solicitudProyectoService            {@link SolicitudProyectoService}.
   * @param solicitudProyectoPresupuestoService {@link SolicitudProyectoPresupuestoService}.
   * @param solicitudProyectoSocioService       {@link SolicitudProyectoSocioService}.
   */
  public SolicitudProyectoController(SolicitudProyectoService solicitudProyectoService,
      SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService,
      SolicitudProyectoSocioService solicitudProyectoSocioService) {

    this.service = solicitudProyectoService;
    this.solicitudProyectoPresupuestoService = solicitudProyectoPresupuestoService;
    this.solicitudProyectoSocioService = solicitudProyectoSocioService;
  }

  /**
   * Crea nuevo {@link SolicitudProyecto}
   * 
   * @param solicitudProyecto {@link SolicitudProyecto}. que se quiere crear.
   * @return Nuevo {@link SolicitudProyecto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public ResponseEntity<SolicitudProyecto> create(@Valid @RequestBody SolicitudProyecto solicitudProyecto) {
    log.debug("create(SolicitudProyecto solicitudProyecto) - start");
    SolicitudProyecto returnValue = service.create(solicitudProyecto);
    log.debug("create(SolicitudProyecto solicitudProyecto) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudProyecto}.
   * 
   * @param solicitudProyecto {@link SolicitudProyecto} a actualizar.
   * @param id                Identificador {@link SolicitudProyecto} a
   *                          actualizar.
   * @param authentication    Datos autenticación.
   * @return SolicitudProyecto {@link SolicitudProyecto} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-INV-C', 'CSP-SOL-INV-ER')")
  public SolicitudProyecto update(@Valid @RequestBody SolicitudProyecto solicitudProyecto, @PathVariable Long id,
      Authentication authentication) {
    log.debug("update(SolicitudProyecto solicitudProyecto, Long id) - start");
    solicitudProyecto.setId(id);
    SolicitudProyecto returnValue = service.update(solicitudProyecto);
    log.debug("update(SolicitudProyecto solicitudProyecto, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link SolicitudProyecto} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyecto}.
   * @return SolicitudProyecto {@link SolicitudProyecto} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  SolicitudProyecto findById(@PathVariable Long id) {
    log.debug("SolicitudProyecto findById(Long id) - start");
    SolicitudProyecto returnValue = service.findById(id);
    log.debug("SolicitudProyecto findById(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link SolicitudProyecto} con id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyecto}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Comprueba si existen datos vinculados a la {@link SolicitudProyecto} de
   * {@link SolicitudProyectoPresupuesto}
   *
   * @param id Id del {@link SolicitudProyectoPresupuesto}.
   * @return
   */
  @RequestMapping(path = "/{id}/solicitudpresupuesto", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V')")
  ResponseEntity<SolicitudProyecto> hasSolicitudPresupuesto(@PathVariable Long id) {
    log.debug("hasSolicitudPresupuesto(Long id) - start");
    Boolean returnValue = solicitudProyectoPresupuestoService.hasSolicitudPresupuesto(id);
    log.debug("hasSolicitudPresupuesto(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si existen datos vinculados a la {@link SolicitudProyecto} de
   * {@link SolicitudProyectoSocio}
   *
   * @param id Id del {@link SolicitudProyectoSocio}.
   * @return
   */
  @RequestMapping(path = "/{id}/solicitudsocio", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V')")
  ResponseEntity<SolicitudProyecto> hasSolicitudSocio(@PathVariable Long id) {
    log.debug("hasSolicitudSocio(Long id) - start");
    Boolean returnValue = solicitudProyectoSocioService.hasSolicitudSocio(id);
    log.debug("hasSolicitudSocio(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @RequestMapping(path = "/{solicitudProyectoId}/solicitudproyectosocios/periodospago", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  public ResponseEntity<Object> hasSolicitudProyectoSocioPeriodosPago(
      @PathVariable(required = true) Long solicitudProyectoId) {

    return this.solicitudProyectoSocioService.existsSolicitudProyectoSocioPeriodoPagoBySolicitudProyectoSocioId(
        solicitudProyectoId) ? ResponseEntity.ok().build() : ResponseEntity.noContent().build();
  }

  @RequestMapping(path = "/{solicitudProyectoId}/solicitudproyectosocios/periodosjustificacion", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  public ResponseEntity<Object> hasSolicitudProyectoSocioPeriodosJustificacion(
      @PathVariable(required = true) Long solicitudProyectoId) {

    return this.solicitudProyectoSocioService
        .existsSolicitudProyectoSocioPeriodoJustificacionBySolicitudProyectoSocioId(solicitudProyectoId)
            ? ResponseEntity.ok().build()
            : ResponseEntity.noContent().build();
  }

  @RequestMapping(path = "/{solicitudProyectoId}/solicitudproyectosocios/coordinador", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  public ResponseEntity<Object> hasAnySolicitudProyectoSocioWithRolCoordinador(
      @PathVariable(required = true) Long solicitudProyectoId) {

    return this.solicitudProyectoSocioService.hasAnySolicitudProyectoSocioWithRolCoordinador(solicitudProyectoId)
        ? ResponseEntity.ok().build()
        : ResponseEntity.noContent().build();
  }

}