package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.service.ModeloTipoFaseService;
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
 * ModeloTipoFaseController
 */
@RestController
@RequestMapping("/modelotipofases")
@Slf4j
public class ModeloTipoFaseController {

  /** ModeloTipoFase service */
  private final ModeloTipoFaseService modeloTipoFaseService;

  /**
   * Instancia un nuevo ModeloTipoFaseController.
   * 
   * @param modeloTipoFaseService {@link ModeloTipoFaseService}.
   */
  public ModeloTipoFaseController(ModeloTipoFaseService modeloTipoFaseService) {

    this.modeloTipoFaseService = modeloTipoFaseService;

  }

  /**
   * Devuelve el {@link ModeloTipoFase} con el id indicado.
   * 
   * @param id Identificador de {@link ModeloTipoFase}.
   * @return {@link ModeloTipoFase} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ModeloTipoFase findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ModeloTipoFase returnValue = modeloTipoFaseService.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link ModeloTipoFase}.
   * 
   * @param modeloTipoFase {@link ModeloTipoFase} que se quiere crear.
   * @return Nuevo {@link ModeloTipoFase} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-ME-C', 'CSP-ME-E')")
  public ResponseEntity<ModeloTipoFase> create(@Valid @RequestBody ModeloTipoFase modeloTipoFase) {
    log.debug("create(ModeloTipoFase modeloTipoFase) - start");
    ModeloTipoFase returnValue = modeloTipoFaseService.create(modeloTipoFase);
    log.debug("create(ModeloTipoFase modeloTipoFase) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ModeloTipoFase} con el id indicado.
   * 
   * @param modeloTipoFase {@link ModeloTipoFase} a actualizar.
   * @param id             id {@link ModeloTipoFase} a actualizar.
   * @return {@link ModeloTipoFase} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-E')")
  public ModeloTipoFase update(@Validated @RequestBody ModeloTipoFase modeloTipoFase, @PathVariable Long id) {
    log.debug("update(ModeloTipoFase modeloTipoFase, Long id) - start");
    modeloTipoFase.setId(id);
    ModeloTipoFase returnValue = modeloTipoFaseService.update(modeloTipoFase);
    log.debug("update(ModeloTipoFase modeloTipoFase, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ModeloTipoFase} con id indicado.
   * 
   * @param id Identificador de {@link ModeloTipoFase}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    modeloTipoFaseService.disable(id);
    log.debug("deleteById(Long id) - end");
  }

}