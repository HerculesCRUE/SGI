package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadFinanciadoraService;
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
 * ProyectoEntidadFinanciadoraController
 */

@RestController
@RequestMapping("/proyectoentidadfinanciadoras")
@Slf4j
public class ProyectoEntidadFinanciadoraController {

  /** ConvocatoriaEntidadFinanciadora service */
  private final ProyectoEntidadFinanciadoraService service;

  public ProyectoEntidadFinanciadoraController(ProyectoEntidadFinanciadoraService proyectoEntidadFinanciadoraService) {
    log.debug(
        "ProyectoEntidadFinanciadoraController(ProyectoEntidadFinanciadoraService proyectoEntidadFinanciadoraService) - start");
    this.service = proyectoEntidadFinanciadoraService;
    log.debug(
        "ProyectoEntidadFinanciadoraController(ProyectoEntidadFinanciadoraService proyectoEntidadFinanciadoraService) - end");
  }

  /**
   * Devuelve el {@link ProyectoEntidadFinanciadora} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoEntidadFinanciadora}.
   * @return {@link ProyectoEntidadFinanciadora} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoEntidadFinanciadora findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoEntidadFinanciadora returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea nuevo {@link ProyectoEntidadFinanciadora}.
   * 
   * @param proyectoEntidadFinanciadora {@link ProyectoEntidadFinanciadora}. que
   *                                    se quiere crear.
   * @return Nuevo {@link ProyectoEntidadFinanciadora} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoEntidadFinanciadora> create(
      @Valid @RequestBody ProyectoEntidadFinanciadora proyectoEntidadFinanciadora) {
    log.debug("create(ProyectoEntidadFinanciadora proyectoEntidadFinanciadora) - start");
    ProyectoEntidadFinanciadora returnValue = service.create(proyectoEntidadFinanciadora);
    log.debug("create(ProyectoEntidadFinanciadora proyectoEntidadFinanciadora) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ProyectoEntidadFinanciadora} con el id indicado.
   * 
   * @param proyectoEntidadFinanciadora {@link ProyectoEntidadFinanciadora} a
   *                                    actualizar.
   * @param id                          id {@link ProyectoEntidadFinanciadora} a
   *                                    actualizar.
   * @return {@link ProyectoEntidadFinanciadora} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoEntidadFinanciadora update(
      @Validated({ Update.class, Default.class }) @RequestBody ProyectoEntidadFinanciadora proyectoEntidadFinanciadora,
      @PathVariable Long id) {
    log.debug("update(ProyectoEntidadFinanciadora proyectoEntidadFinanciadora, Long id) - start");
    proyectoEntidadFinanciadora.setId(id);
    ProyectoEntidadFinanciadora returnValue = service.update(proyectoEntidadFinanciadora);
    log.debug("update(ProyectoEntidadFinanciadora proyectoEntidadFinanciadora, Long id) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link ProyectoEntidadFinanciadora} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoEntidadFinanciadora}.
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
