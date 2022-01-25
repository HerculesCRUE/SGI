package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ContextoProyecto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.service.ContextoProyectoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ContextoProyectoController
 */
@RestController
@RequestMapping("/proyecto-contextoproyectos")
@Slf4j
public class ContextoProyectoController {

  /** ContextoProyecto service */
  private final ContextoProyectoService service;

  /**
   * Instancia un nuevo ContextoProyectoController.
   * 
   * @param service {@link ContextoProyectoService}
   */
  public ContextoProyectoController(ContextoProyectoService service) {
    this.service = service;
  }

  /**
   * Crea un nuevo {@link ContextoProyecto}.
   * 
   * @param contextoProyecto {@link ContextoProyecto} que se quiere crear.
   * @return Nuevo {@link ContextoProyecto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ContextoProyecto> create(@Valid @RequestBody ContextoProyecto contextoProyecto) {
    log.debug("create(ContextoProyecto contextoProyecto) - start");
    ContextoProyecto returnValue = service.create(contextoProyecto);
    log.debug("create(ContextoProyecto contextoProyecto) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ContextoProyecto} con el id indicado.
   * 
   * @param contextoProyecto {@link ContextoProyecto} a actualizar.
   * @param id               identificador de la {@link Convocatoria} a
   *                         actualizar.
   * @return {@link ContextoProyecto} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ContextoProyecto update(
      @Validated({ Update.class, Default.class }) @RequestBody ContextoProyecto contextoProyecto,
      @PathVariable Long id) {
    log.debug("update(ContextoProyecto contextoProyecto, Long id) - start");
    ContextoProyecto returnValue = service.update(contextoProyecto, id);
    log.debug("update(ContextoProyecto contextoProyecto, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link ContextoProyecto} de la {@link Convocatoria}.
   * 
   * @param id Identificador de la {@link Convocatoria}.
   * @return el {@link ContextoProyecto}
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V','CSP-PRO-E')")
  public ResponseEntity<ContextoProyecto> findByProyecto(@PathVariable Long id) {
    log.debug("ContextoProyecto findByProyecto(Long id) - start");
    ContextoProyecto returnValue = service.findByProyecto(id);

    if (returnValue == null) {
      log.debug("ContextoProyecto findByProyecto(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("ContextoProyecto findByProyecto(Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

}
