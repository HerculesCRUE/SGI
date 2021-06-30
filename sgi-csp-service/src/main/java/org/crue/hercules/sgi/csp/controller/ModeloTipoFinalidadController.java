package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.service.ModeloTipoFinalidadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ModeloTipoFinalidadController
 */

@RestController
@RequestMapping("/modelotipofinalidades")
@Slf4j
public class ModeloTipoFinalidadController {

  /** ModeloTipoFinalidad service */
  private final ModeloTipoFinalidadService service;

  public ModeloTipoFinalidadController(ModeloTipoFinalidadService modeloTipoFinalidadService) {
    log.debug("ModeloTipoFinalidadController(ModeloTipoFinalidadService modeloTipoFinalidadService) - start");
    this.service = modeloTipoFinalidadService;
    log.debug("ModeloTipoFinalidadController(ModeloTipoFinalidadService modeloTipoFinalidadService) - end");
  }

  /**
   * Crea nuevo {@link ModeloTipoFinalidad}.
   * 
   * @param modeloTipoFinalidad {@link ModeloTipoFinalidad}. que se quiere crear.
   * @return Nuevo {@link ModeloTipoFinalidad} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-ME-C', 'CSP-ME-E')")
  public ResponseEntity<ModeloTipoFinalidad> create(@Valid @RequestBody ModeloTipoFinalidad modeloTipoFinalidad) {
    log.debug("create(ModeloTipoFinalidad modeloTipoFinalidad) - start");
    ModeloTipoFinalidad returnValue = service.create(modeloTipoFinalidad);
    log.debug("create(ModeloTipoFinalidad modeloTipoFinalidad) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Desactiva {@link ModeloTipoFinalidad} con id indicado.
   * 
   * @param id Identificador de {@link ModeloTipoFinalidad}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.disable(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve el {@link ModeloTipoFinalidad} con el id indicado.
   * 
   * @param id Identificador de {@link ModeloTipoFinalidad}.
   * @return ModeloTipoFinalidad {@link ModeloTipoFinalidad} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  ModeloTipoFinalidad findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ModeloTipoFinalidad returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }
}
