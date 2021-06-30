package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento;
import org.crue.hercules.sgi.csp.service.ConvocatoriaDocumentoService;
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
 * ConvocatoriaDocumentoController
 */
@RestController
@RequestMapping("/convocatoriadocumentos")
@Slf4j
public class ConvocatoriaDocumentoController {

  /** ConvocatoriaDocumento service */
  private final ConvocatoriaDocumentoService service;

  /**
   * Instancia un nuevo ConvocatoriaDocumentoController.
   * 
   * @param service {@link ConvocatoriaDocumentoService}
   */
  public ConvocatoriaDocumentoController(ConvocatoriaDocumentoService service) {
    this.service = service;
  }

  /**
   * Crea un nuevo {@link ConvocatoriaDocumento}.
   * 
   * @param convocatoriaDocumento {@link ConvocatoriaDocumento} que se quiere
   *                              crear.
   * @return Nuevo {@link ConvocatoriaDocumento} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C','CSP-CON-E')")
  ResponseEntity<ConvocatoriaDocumento> create(@Valid @RequestBody ConvocatoriaDocumento convocatoriaDocumento) {
    log.debug("create(ConvocatoriaDocumento convocatoriaDocumento) - start");
    ConvocatoriaDocumento returnValue = service.create(convocatoriaDocumento);
    log.debug("create(ConvocatoriaDocumento convocatoriaDocumento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ConvocatoriaDocumento} con el id indicado.
   * 
   * @param convocatoriaDocumento {@link ConvocatoriaDocumento} a actualizar.
   * @param id                    id {@link ConvocatoriaDocumento} a actualizar.
   * @return {@link ConvocatoriaDocumento} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  ConvocatoriaDocumento update(
      @Validated({ Update.class, Default.class }) @RequestBody ConvocatoriaDocumento convocatoriaDocumento,
      @PathVariable Long id) {
    log.debug("update(ConvocatoriaDocumento convocatoriaDocumento, Long id) - start");
    convocatoriaDocumento.setId(id);
    ConvocatoriaDocumento returnValue = service.update(convocatoriaDocumento);
    log.debug("update(ConvocatoriaDocumento convocatoriaDocumento, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ConvocatoriaDocumento} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaDocumento}.
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
   * Devuelve el {@link ConvocatoriaDocumento} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaDocumento}.
   * @return {@link ConvocatoriaDocumento} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  ConvocatoriaDocumento findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaDocumento returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }
}
