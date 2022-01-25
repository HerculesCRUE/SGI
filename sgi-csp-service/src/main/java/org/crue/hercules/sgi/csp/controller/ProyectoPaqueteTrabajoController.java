package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.service.ProyectoPaqueteTrabajoService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoPaqueteTrabajoController
 */
@RestController
@RequestMapping("/proyectopaquetetrabajos")
@Slf4j
public class ProyectoPaqueteTrabajoController {

  /** ProyectoPaqueteTrabajo service */
  private final ProyectoPaqueteTrabajoService service;

  /**
   * Instancia un nuevo ProyectoPaqueteTrabajoController.
   * 
   * @param service {@link ProyectoPaqueteTrabajoService}
   */
  public ProyectoPaqueteTrabajoController(ProyectoPaqueteTrabajoService service) {
    this.service = service;
  }

  /**
   * Crea un nuevo {@link ProyectoPaqueteTrabajo}.
   * 
   * @param proyectoPaqueteTrabajo {@link ProyectoPaqueteTrabajo} que se quiere
   *                               crear.
   * @return Nuevo {@link ProyectoPaqueteTrabajo} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoPaqueteTrabajo> create(
      @Valid @RequestBody ProyectoPaqueteTrabajo proyectoPaqueteTrabajo) {
    log.debug("create(ProyectoPaqueteTrabajo proyectoPaqueteTrabajo) - start");
    ProyectoPaqueteTrabajo returnValue = service.create(proyectoPaqueteTrabajo);
    log.debug("create(ProyectoPaqueteTrabajo proyectoPaqueteTrabajo) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ProyectoPaqueteTrabajo} con el id indicado.
   * 
   * @param proyectoPaqueteTrabajo {@link ProyectoPaqueteTrabajo} a actualizar.
   * @param id                     id {@link ProyectoPaqueteTrabajo} a actualizar.
   * @return {@link ProyectoPaqueteTrabajo} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoPaqueteTrabajo update(
      @Validated({ Update.class, Default.class }) @RequestBody ProyectoPaqueteTrabajo proyectoPaqueteTrabajo,
      @PathVariable Long id) {
    log.debug("update(ProyectoPaqueteTrabajo proyectoPaqueteTrabajo, Long id) - start");
    proyectoPaqueteTrabajo.setId(id);
    ProyectoPaqueteTrabajo returnValue = service.update(proyectoPaqueteTrabajo);
    log.debug("update(ProyectoPaqueteTrabajo proyectoPaqueteTrabajo, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ProyectoPaqueteTrabajo} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoPaqueteTrabajo}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve el {@link ProyectoPaqueteTrabajo} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoPaqueteTrabajo}.
   * @return {@link ProyectoPaqueteTrabajo} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ProyectoPaqueteTrabajo findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoPaqueteTrabajo returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
