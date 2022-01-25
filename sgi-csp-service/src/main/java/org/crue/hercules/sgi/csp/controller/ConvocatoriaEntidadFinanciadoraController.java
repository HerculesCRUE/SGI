package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadFinanciadoraService;
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
 * ConvocatoriaEntidadFinanciadoraController
 */

@RestController
@RequestMapping("/convocatoriaentidadfinanciadoras")
@Slf4j
public class ConvocatoriaEntidadFinanciadoraController {

  /** ConvocatoriaEntidadFinanciadora service */
  private final ConvocatoriaEntidadFinanciadoraService service;

  public ConvocatoriaEntidadFinanciadoraController(
      ConvocatoriaEntidadFinanciadoraService convocatoriaEntidadFinanciadoraService) {
    log.debug(
        "ConvocatoriaEntidadFinanciadoraController(ConvocatoriaEntidadFinanciadoraService convocatoriaEntidadFinanciadoraService) - start");
    this.service = convocatoriaEntidadFinanciadoraService;
    log.debug(
        "ConvocatoriaEntidadFinanciadoraController(ConvocatoriaEntidadFinanciadoraService convocatoriaEntidadFinanciadoraService) - end");
  }

  /**
   * Devuelve el {@link ConvocatoriaEntidadFinanciadora} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaEntidadFinanciadora}.
   * @return {@link ConvocatoriaEntidadFinanciadora} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ConvocatoriaEntidadFinanciadora findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaEntidadFinanciadora returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea nuevo {@link ConvocatoriaEntidadFinanciadora}.
   * 
   * @param convocatoriaEntidadFinanciadora {@link ConvocatoriaEntidadFinanciadora}.
   *                                        que se quiere crear.
   * @return Nuevo {@link ConvocatoriaEntidadFinanciadora} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-E')")
  public ResponseEntity<ConvocatoriaEntidadFinanciadora> create(
      @Valid @RequestBody ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora) {
    log.debug("create(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora) - start");
    ConvocatoriaEntidadFinanciadora returnValue = service.create(convocatoriaEntidadFinanciadora);
    log.debug("create(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ConvocatoriaEntidadFinanciadora} con el id indicado.
   * 
   * @param convocatoriaEntidadFinanciadora {@link ConvocatoriaEntidadFinanciadora}
   *                                        a actualizar.
   * @param id                              id
   *                                        {@link ConvocatoriaEntidadFinanciadora}
   *                                        a actualizar.
   * @return {@link ConvocatoriaEntidadFinanciadora} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ConvocatoriaEntidadFinanciadora update(
      @Validated({ Update.class,
          Default.class }) @RequestBody ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora,
      @PathVariable Long id) {
    log.debug("update(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora, Long id) - start");
    convocatoriaEntidadFinanciadora.setId(id);
    ConvocatoriaEntidadFinanciadora returnValue = service.update(convocatoriaEntidadFinanciadora);
    log.debug("update(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link ConvocatoriaEntidadFinanciadora} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaEntidadFinanciadora}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }
}
