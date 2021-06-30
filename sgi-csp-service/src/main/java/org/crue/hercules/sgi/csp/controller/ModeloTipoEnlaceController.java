package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.service.ModeloTipoEnlaceService;
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
 * ModeloTipoEnlaceController
 */
@RestController
@RequestMapping("/modelotipoenlaces")
@Slf4j
public class ModeloTipoEnlaceController {

  /** ModeloTipoEnlace service */
  private final ModeloTipoEnlaceService service;

  public ModeloTipoEnlaceController(ModeloTipoEnlaceService service) {
    log.debug("ModeloTipoEnlaceController(ModeloTipoEnlaceService service) - start");
    this.service = service;
    log.debug("ModeloTipoEnlaceController(ModeloTipoEnlaceService service) - end");
  }

  /**
   * Crea nuevo {@link ModeloTipoEnlace}.
   * 
   * @param modeloTipoEnlace {@link ModeloTipoEnlace} que se quiere crear.
   * @return Nuevo {@link ModeloTipoEnlace} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-ME-C', 'CSP-ME-E')")
  public ResponseEntity<ModeloTipoEnlace> create(@Valid @RequestBody ModeloTipoEnlace modeloTipoEnlace) {
    log.debug("create(ModeloTipoEnlace modeloTipoEnlace) - start");
    ModeloTipoEnlace returnValue = service.create(modeloTipoEnlace);
    log.debug("create(ModeloTipoEnlace modeloTipoEnlace) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Desactiva {@link ModeloTipoEnlace} con id indicado.
   * 
   * @param id Identificador de {@link ModeloTipoEnlace}.
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
   * Devuelve el {@link ModeloTipoEnlace} con el id indicado.
   * 
   * @param id Identificador de {@link ModeloTipoEnlace}.
   * @return ModeloTipoEnlace {@link ModeloTipoEnlace} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  ModeloTipoEnlace findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ModeloTipoEnlace returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }
}
