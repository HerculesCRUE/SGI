package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.service.ModeloTipoDocumentoService;
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
 * ModeloTipoDocumentoController
 */
@RestController
@RequestMapping("/modelotipodocumentos")
@Slf4j
public class ModeloTipoDocumentoController {

  /** ModeloTipoDocumento service */
  private final ModeloTipoDocumentoService service;

  public ModeloTipoDocumentoController(ModeloTipoDocumentoService service) {
    log.debug("ModeloTipoDocumentoController(ModeloTipoDocumentoService service) - start");
    this.service = service;
    log.debug("ModeloTipoDocumentoController(ModeloTipoDocumentoService service) - end");
  }

  /**
   * Crea nuevo {@link ModeloTipoDocumento}.
   * 
   * @param modeloTipoDocumento {@link ModeloTipoDocumento} que se quiere crear.
   * @return Nuevo {@link ModeloTipoDocumento} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-ME-C', 'CSP-ME-E')")
  public ResponseEntity<ModeloTipoDocumento> create(@Valid @RequestBody ModeloTipoDocumento modeloTipoDocumento) {
    log.debug("create(ModeloTipoDocumento modeloTipoDocumento) - start");
    ModeloTipoDocumento returnValue = service.create(modeloTipoDocumento);
    log.debug("create(ModeloTipoDocumento modeloTipoDocumento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Desactiva {@link ModeloTipoDocumento} con id indicado.
   * 
   * @param id Identificador de {@link ModeloTipoDocumento}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.disable(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve el {@link ModeloTipoDocumento} con el id indicado.
   * 
   * @param id Identificador de {@link ModeloTipoDocumento}.
   * @return {@link ModeloTipoDocumento} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ModeloTipoDocumento findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ModeloTipoDocumento returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
