package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ProyectoAreaConocimiento;
import org.crue.hercules.sgi.csp.service.ProyectoAreaConocimientoService;
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
 * ProyectoAreaConocimientoController
 */
@RestController
@RequestMapping("/proyecto-areas-conocimiento")
@Slf4j
public class ProyectoAreaConocimientoController {

  /** ProyectoAreasConocimientoService service */
  private final ProyectoAreaConocimientoService service;

  /**
   * Instancia de un nuevo ProyectoAreaConocimientoController.
   * 
   * @param proyectoAreaConocimientoService {@link ProyectoAreaConocimientoService}
   * 
   */
  public ProyectoAreaConocimientoController(ProyectoAreaConocimientoService proyectoAreaConocimientoService) {
    this.service = proyectoAreaConocimientoService;
  }

  /**
   * Crea nuevo {@link ProyectoAreaConocimiento}.
   * 
   * @param proyectoAreaConocimiento {@link ProyectoAreaConocimiento}. que se
   *                                 quiere crear.
   * @return Nuevo {@link ProyectoAreaConocimiento} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoAreaConocimiento> create(
      @Valid @RequestBody ProyectoAreaConocimiento proyectoAreaConocimiento) {
    log.debug("create(ProyectoAreaConocimiento proyectoAreaConocimiento) - start");
    ProyectoAreaConocimiento returnValue = service.create(proyectoAreaConocimiento);
    log.debug("create(ProyectoAreaConocimiento proyectoAreaConocimiento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Elimina la {@link ProyectoAreaConocimiento} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoAreaConocimiento}.
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
