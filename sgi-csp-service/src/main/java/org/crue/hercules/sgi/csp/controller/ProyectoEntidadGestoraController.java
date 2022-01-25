package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadGestoraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoEntidadGestoraController
 */

@RestController
@RequestMapping("/proyectoentidadgestoras")
@Slf4j
public class ProyectoEntidadGestoraController {

  /** ProyectoEntidadGestora service */
  private final ProyectoEntidadGestoraService service;

  public ProyectoEntidadGestoraController(ProyectoEntidadGestoraService proyectoEntidadGestoraService) {
    log.debug("ProyectoEntidadGestoraController(ProyectoEntidadGestoraService proyectoEntidadGestoraService) - start");
    this.service = proyectoEntidadGestoraService;
    log.debug("ProyectoEntidadGestoraController(ProyectoEntidadGestoraService proyectoEntidadGestoraService) - end");
  }

  /**
   * Crea nuevo {@link ProyectoEntidadGestora}.
   * 
   * @param proyectoEntidadGestora {@link ProyectoEntidadGestora}. que se quiere
   *                               crear.
   * @return Nuevo {@link ProyectoEntidadGestora} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoEntidadGestora> create(
      @Valid @RequestBody ProyectoEntidadGestora proyectoEntidadGestora) {
    log.debug("create(ProyectoEntidadGestora proyectoEntidadGestora) - start");
    ProyectoEntidadGestora returnValue = service.create(proyectoEntidadGestora);
    log.debug("create(ProyectoEntidadGestora proyectoEntidadGestora) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoEntidadGestora update(@RequestBody ProyectoEntidadGestora modeloTipoHito, @PathVariable Long id) {
    log.debug("update(ProyectoEntidadGestora modeloTipoHito, Long id) - start");
    modeloTipoHito.setId(id);
    ProyectoEntidadGestora returnValue = service.update(modeloTipoHito);
    log.debug("update(ProyectoEntidadGestora modeloTipoHito, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link ProyectoEntidadGestora} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoEntidadGestora}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }
}
