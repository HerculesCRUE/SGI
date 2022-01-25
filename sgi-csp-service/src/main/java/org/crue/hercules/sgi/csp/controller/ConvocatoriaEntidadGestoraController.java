package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadGestoraService;
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
 * ConvocatoriaEntidadGestoraController
 */

@RestController
@RequestMapping("/convocatoriaentidadgestoras")
@Slf4j
public class ConvocatoriaEntidadGestoraController {

  /** ConvocatoriaEntidadGestora service */
  private final ConvocatoriaEntidadGestoraService service;

  public ConvocatoriaEntidadGestoraController(ConvocatoriaEntidadGestoraService convocatoriaEntidadGestoraService) {
    log.debug(
        "ConvocatoriaEntidadGestoraController(ConvocatoriaEntidadGestoraService convocatoriaEntidadGestoraService) - start");
    this.service = convocatoriaEntidadGestoraService;
    log.debug(
        "ConvocatoriaEntidadGestoraController(ConvocatoriaEntidadGestoraService convocatoriaEntidadGestoraService) - end");
  }

  /**
   * Crea nuevo {@link ConvocatoriaEntidadGestora}.
   * 
   * @param convocatoriaEntidadGestora {@link ConvocatoriaEntidadGestora}. que se
   *                                   quiere crear.
   * @return Nuevo {@link ConvocatoriaEntidadGestora} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-C')")
  public ResponseEntity<ConvocatoriaEntidadGestora> create(
      @Valid @RequestBody ConvocatoriaEntidadGestora convocatoriaEntidadGestora) {
    log.debug("create(ConvocatoriaEntidadGestora convocatoriaEntidadGestora) - start");
    ConvocatoriaEntidadGestora returnValue = service.create(convocatoriaEntidadGestora);
    log.debug("create(ConvocatoriaEntidadGestora convocatoriaEntidadGestora) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ConvocatoriaEntidadGestora update(@RequestBody ConvocatoriaEntidadGestora modeloTipoHito,
      @PathVariable Long id) {
    log.debug("update(ConvocatoriaEntidadGestora modeloTipoHito, Long id) - start");
    modeloTipoHito.setId(id);
    ConvocatoriaEntidadGestora returnValue = service.update(modeloTipoHito);
    log.debug("update(ConvocatoriaEntidadGestora modeloTipoHito, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link ConvocatoriaEntidadGestora} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaEntidadGestora}.
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
