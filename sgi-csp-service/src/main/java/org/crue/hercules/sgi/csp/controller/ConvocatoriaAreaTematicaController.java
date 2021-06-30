package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.service.ConvocatoriaAreaTematicaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * ConvocatoriaAreaTematicaController
 */

@RestController
@RequestMapping("/convocatoriaareatematicas")
@Slf4j
public class ConvocatoriaAreaTematicaController {

  /** ConvocatoriaAreaTematica service */
  private final ConvocatoriaAreaTematicaService service;

  public ConvocatoriaAreaTematicaController(ConvocatoriaAreaTematicaService convocatoriaAreaTematicaService) {
    log.debug(
        "ConvocatoriaAreaTematicaController(ConvocatoriaAreaTematicaService convocatoriaAreaTematicaService) - start");
    this.service = convocatoriaAreaTematicaService;
    log.debug(
        "ConvocatoriaAreaTematicaController(ConvocatoriaAreaTematicaService convocatoriaAreaTematicaService) - end");
  }

  /**
   * Crea nuevo {@link ConvocatoriaAreaTematica}.
   * 
   * @param convocatoriaAreaTematica {@link ConvocatoriaAreaTematica}. que se
   *                                 quiere crear.
   * @return Nuevo {@link ConvocatoriaAreaTematica} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-E')")
  public ResponseEntity<ConvocatoriaAreaTematica> create(
      @Valid @RequestBody ConvocatoriaAreaTematica convocatoriaAreaTematica) {
    log.debug("create(ConvocatoriaAreaTematica convocatoriaAreaTematica) - start");
    ConvocatoriaAreaTematica returnValue = service.create(convocatoriaAreaTematica);
    log.debug("create(ConvocatoriaAreaTematica convocatoriaAreaTematica) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link ConvocatoriaAreaTematica}.
   * 
   * @param convocatoriaAreaTematica {@link ConvocatoriaAreaTematica} a
   *                                 actualizar.
   * @param id                       Identificador
   *                                 {@link ConvocatoriaAreaTematica} a
   *                                 actualizar.
   * @return ConvocatoriaAreaTematica {@link ConvocatoriaAreaTematica} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ConvocatoriaAreaTematica update(@Valid @RequestBody ConvocatoriaAreaTematica convocatoriaAreaTematica,
      @PathVariable Long id) {
    log.debug("update(ConvocatoriaAreaTematica convocatoriaAreaTematica, Long id) - start");
    convocatoriaAreaTematica.setId(id);
    ConvocatoriaAreaTematica returnValue = service.update(convocatoriaAreaTematica);
    log.debug("update(ConvocatoriaAreaTematica convocatoriaAreaTematica, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link ConvocatoriaAreaTematica} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaAreaTematica}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve el {@link ConvocatoriaAreaTematica} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaAreaTematica}.
   * @return {@link ConvocatoriaAreaTematica} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  ConvocatoriaAreaTematica findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaAreaTematica returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
