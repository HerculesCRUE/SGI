package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoClasificacion;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoClasificacionService;
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
 * SolicitudProyectoClasificacionController
 */

@RestController
@RequestMapping("/solicitud-proyecto-clasificaciones")
@Slf4j
public class SolicitudProyectoClasificacionController {

  /** SolicitudProyectoClasificacion service */
  private final SolicitudProyectoClasificacionService service;

  public SolicitudProyectoClasificacionController(
      SolicitudProyectoClasificacionService solicitudProyectoClasificacioService) {
    this.service = solicitudProyectoClasificacioService;
  }

  /**
   * Crea nuevo {@link SolicitudProyectoClasificacion}.
   * 
   * @param solicitudProyectoClasificacion {@link SolicitudProyectoClasificacion}.
   *                                       que se quiere crear.
   * @return Nuevo {@link SolicitudProyectoClasificacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public ResponseEntity<SolicitudProyectoClasificacion> create(
      @Valid @RequestBody SolicitudProyectoClasificacion solicitudProyectoClasificacion) {
    log.debug("create(SolicitudProyectoClasificacion solicitudProyectoClasificacion) - start");
    SolicitudProyectoClasificacion returnValue = service.create(solicitudProyectoClasificacion);
    log.debug("create(SolicitudProyectoClasificacion solicitudProyectoClasificacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Elimina el {@link SolicitudProyectoClasificacion} con el id indicado.
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
