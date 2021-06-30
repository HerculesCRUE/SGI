package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.service.ModeloTipoHitoService;
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
 * ModeloTipoHitoController
 */

@RestController
@RequestMapping("/modelotipohitos")
@Slf4j
public class ModeloTipoHitoController {

  /** ModeloTipoHito service */
  private final ModeloTipoHitoService service;

  public ModeloTipoHitoController(ModeloTipoHitoService modeloTipoHitoService) {
    log.debug("ModeloTipoHitoController(ModeloTipoHitoService modeloTipoHitoService) - start");
    this.service = modeloTipoHitoService;
    log.debug("ModeloTipoHitoController(ModeloTipoHitoService modeloTipoHitoService) - end");
  }

  /**
   * Crea nuevo {@link ModeloTipoHito}.
   *
   * @param modeloTipoHito {@link ModeloTipoHito}. que se quiere crear.
   * @return Nuevo {@link ModeloTipoHito} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-ME-C', 'CSP-ME-E')")
  public ResponseEntity<ModeloTipoHito> create(@Valid @RequestBody ModeloTipoHito modeloTipoHito) {
    log.debug("create(ModeloTipoHito modeloTipoHito) - start");
    ModeloTipoHito returnValue = service.create(modeloTipoHito);
    log.debug("create(ModeloTipoHito modeloTipoHito) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ModeloTipoHito} con el id indicado.
   * 
   * @param modeloTipoHito {@link ModeloTipoHito} a actualizar.
   * @param id             id {@link ModeloTipoHito} a actualizar.
   * @return {@link ModeloTipoHito} actualizado.
   */

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-E')")
  public ModeloTipoHito update(@RequestBody ModeloTipoHito modeloTipoHito, @PathVariable Long id) {
    log.debug("update(ModeloTipoHito modeloTipoHito, Long id) - start");
    modeloTipoHito.setId(id);
    ModeloTipoHito returnValue = service.update(modeloTipoHito);
    log.debug("update(ModeloTipoHito modeloTipoHito, Long id) - end");
    return returnValue;

  }

  /**
   * Desactiva {@link ModeloTipoHito} con id indicado.
   *
   * @param id Identificador de {@link ModeloTipoHito}.
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
   * Devuelve el {@link ModeloTipoHito} con el id indicado.
   *
   * @param id Identificador de {@link ModeloTipoHito}.
   * @return ModeloTipoHito {@link ModeloTipoHito} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  ModeloTipoHito findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ModeloTipoHito returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }
}
