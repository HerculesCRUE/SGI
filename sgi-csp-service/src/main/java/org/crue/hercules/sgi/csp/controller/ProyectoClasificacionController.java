package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ProyectoClasificacion;
import org.crue.hercules.sgi.csp.service.ProyectoClasificacionService;
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
 * ProyectoClasificacionController
 */

@RestController
@RequestMapping("/proyecto-clasificaciones")
@Slf4j
public class ProyectoClasificacionController {

  /** ProyectoClasificacion service */
  private final ProyectoClasificacionService service;

  public ProyectoClasificacionController(ProyectoClasificacionService proyectoClasificacioService) {
    this.service = proyectoClasificacioService;
  }

  /**
   * Crea nuevo {@link ProyectoClasificacion}.
   * 
   * @param proyectoClasificacion {@link ProyectoClasificacion} que se quiere
   *                              crear.
   * @return Nuevo {@link ProyectoClasificacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoClasificacion> create(@Valid @RequestBody ProyectoClasificacion proyectoClasificacion) {
    log.debug("create(ProyectoClasificacion proyectoClasificacion) - start");
    ProyectoClasificacion returnValue = service.create(proyectoClasificacion);
    log.debug("create(ProyectoClasificacion proyectoClasificacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Elimina el {@link ProyectoClasificacion} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoClasificacion}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }
}
