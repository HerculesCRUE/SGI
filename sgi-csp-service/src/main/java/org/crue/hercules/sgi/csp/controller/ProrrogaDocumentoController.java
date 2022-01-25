package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumento;
import org.crue.hercules.sgi.csp.service.ProrrogaDocumentoService;
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
 * ProrrogaDocumentoController
 */
@RestController
@RequestMapping("/prorrogadocumentos")
@Slf4j
public class ProrrogaDocumentoController {

  /** ProrrogaDocumento service */
  private final ProrrogaDocumentoService service;

  /**
   * Instancia un nuevo ProrrogaDocumentoController.
   * 
   * @param service {@link ProrrogaDocumentoService}
   */
  public ProrrogaDocumentoController(ProrrogaDocumentoService service) {
    this.service = service;
  }

  /**
   * Crea un nuevo {@link ProrrogaDocumento}.
   * 
   * @param prorrogaDocumento {@link ProrrogaDocumento} que se quiere crear.
   * @return Nuevo {@link ProrrogaDocumento} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProrrogaDocumento> create(@Valid @RequestBody ProrrogaDocumento prorrogaDocumento) {
    log.debug("create(ProrrogaDocumento prorrogaDocumento) - start");
    ProrrogaDocumento returnValue = service.create(prorrogaDocumento);
    log.debug("create(ProrrogaDocumento prorrogaDocumento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ProrrogaDocumento} con el id indicado.
   * 
   * @param prorrogaDocumento {@link ProrrogaDocumento} a actualizar.
   * @param id                id {@link ProrrogaDocumento} a actualizar.
   * @return {@link ProrrogaDocumento} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProrrogaDocumento update(
      @Validated({ Update.class, Default.class }) @RequestBody ProrrogaDocumento prorrogaDocumento,
      @PathVariable Long id) {
    log.debug("update(ProrrogaDocumento prorrogaDocumento, Long id) - start");
    prorrogaDocumento.setId(id);
    ProrrogaDocumento returnValue = service.update(prorrogaDocumento);
    log.debug("update(ProrrogaDocumento prorrogaDocumento, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ProrrogaDocumento} con id indicado.
   * 
   * @param id Identificador de {@link ProrrogaDocumento}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve el {@link ProrrogaDocumento} con el id indicado.
   * 
   * @param id Identificador de {@link ProrrogaDocumento}.
   * @return {@link ProrrogaDocumento} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ProrrogaDocumento findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProrrogaDocumento returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }
}
