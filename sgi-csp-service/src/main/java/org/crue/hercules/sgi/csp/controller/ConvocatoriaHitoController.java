package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.service.ConvocatoriaHitoService;
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
 * ConvocatoriaHitoController
 */
@RestController
@RequestMapping("/convocatoriahitos")
@Slf4j
public class ConvocatoriaHitoController {

  /** ConvocatoriaHito service */
  private final ConvocatoriaHitoService service;

  /**
   * Instancia un nuevo ConvocatoriaHitoController.
   * 
   * @param service {@link ConvocatoriaHitoService}
   */
  public ConvocatoriaHitoController(ConvocatoriaHitoService service) {
    this.service = service;
  }

  /**
   * Devuelve el {@link ConvocatoriaHito} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaHito}.
   * @return {@link ConvocatoriaHito} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ConvocatoriaHito findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaHito returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link ConvocatoriaHito}.
   * 
   * @param convocatoriaHito {@link ConvocatoriaHito} que se quiere crear.
   * @return Nuevo {@link ConvocatoriaHito} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-E')")
  public ResponseEntity<ConvocatoriaHito> create(@Valid @RequestBody ConvocatoriaHito convocatoriaHito) {
    log.debug("create(ConvocatoriaHito convocatoriaHito) - start");
    ConvocatoriaHito returnValue = service.create(convocatoriaHito);
    log.debug("create(ConvocatoriaHito convocatoriaHito) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ConvocatoriaHito} con el id indicado.
   * 
   * @param convocatoriaHito {@link ConvocatoriaHito} a actualizar.
   * @param id               id {@link ConvocatoriaHito} a actualizar.
   * @return {@link ConvocatoriaHito} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ConvocatoriaHito update(
      @Validated({ Update.class, Default.class }) @RequestBody ConvocatoriaHito convocatoriaHito,
      @PathVariable Long id) {
    log.debug("update(ConvocatoriaHito convocatoriaHito, Long id) - start");
    convocatoriaHito.setId(id);
    ConvocatoriaHito returnValue = service.update(convocatoriaHito);
    log.debug("update(ConvocatoriaHito convocatoriaHito, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ConvocatoriaHito} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaHito}.
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
