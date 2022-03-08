package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoAreaConocimiento;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoClasificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoAreaConocimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * SolicitudProyectoAreaConocimientoController
 */
@RestController
@RequestMapping(SolicitudProyectoAreaConocimientoController.REQUEST_MAPPING)
@Slf4j
public class SolicitudProyectoAreaConocimientoController {

  public static final String REQUEST_MAPPING = "/solicitud-proyecto-areas-conocimiento";

  /** SolicitudProyectoAreasConocimientoService service */
  private final SolicitudProyectoAreaConocimientoService service;

  /**
   * Instancia de un nuevo SolicitudProyectoAreaConocimientoController.
   * 
   * @param solicitudProyectoAreasConocimientoService servicio de areas
   *                                                  conocimiento.
   */
  public SolicitudProyectoAreaConocimientoController(
      SolicitudProyectoAreaConocimientoService solicitudProyectoAreasConocimientoService) {
    this.service = solicitudProyectoAreasConocimientoService;
  }

  /**
   * Crea nuevo {@link SolicitudProyectoAreaConocimiento}.
   * 
   * @param solicitudProyectoAreaConocimiento {@link SolicitudProyectoAreaConocimiento}
   *                                          que se quiere crear.
   * @return Nuevo {@link SolicitudProyectoAreaConocimiento} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public ResponseEntity<SolicitudProyectoAreaConocimiento> create(
      @Valid @RequestBody SolicitudProyectoAreaConocimiento solicitudProyectoAreaConocimiento) {
    log.debug("create(SolicitudProyectoClasificacion solicitudProyectoClasificacion) - start");
    SolicitudProyectoAreaConocimiento returnValue = service.create(solicitudProyectoAreaConocimiento);
    log.debug("create(SolicitudProyectoClasificacion solicitudProyectoClasificacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Elimina la {@link SolicitudProyectoEntidadFinanciadoraAjena} con el id
   * indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoClasificacion}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }
}
