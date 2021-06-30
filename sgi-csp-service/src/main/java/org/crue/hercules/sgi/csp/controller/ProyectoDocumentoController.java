package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento;
import org.crue.hercules.sgi.csp.service.ProyectoDocumentoService;
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
 * ProyectoDocumentoController
 */

@RestController
@RequestMapping("/proyectodocumentos")
@Slf4j
public class ProyectoDocumentoController {

  /** ProyectoDocumento service */
  private final ProyectoDocumentoService service;

  public ProyectoDocumentoController(ProyectoDocumentoService proyectoDocumentoService) {
    log.debug("ProyectoDocumentoController(ProyectoDocumentoService proyectoDocumentoService) - start");
    this.service = proyectoDocumentoService;
    log.debug("ProyectoDocumentoController(ProyectoDocumentoService proyectoDocumentoService) - end");
  }

  /**
   * Crea un nuevo {@link ProyectoDocumento}.
   * 
   * @param proyectoDocumento {@link ProyectoDocumento} que se quiere crear.
   * @return Nuevo {@link ProyectoDocumento} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<ProyectoDocumento> create(@Valid @RequestBody ProyectoDocumento proyectoDocumento) {
    log.debug("create(ProyectoDocumento proyectoDocumento) - start");
    ProyectoDocumento returnValue = service.create(proyectoDocumento);
    log.debug("create(ProyectoDocumento proyectoDocumento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ProyectoDocumento} con el id indicado.
   * 
   * @param proyectoDocumento {@link ProyectoDocumento} a actualizar.
   * @param id                id {@link ProyectoDocumento} a actualizar.
   * @return {@link ProyectoDocumento} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ProyectoDocumento update(@Validated({ Update.class, Default.class }) @RequestBody ProyectoDocumento proyectoDocumento,
      @PathVariable Long id) {
    log.debug("update(ProyectoDocumento proyectoDocumento, Long id) - start");
    proyectoDocumento.setId(id);
    ProyectoDocumento returnValue = service.update(proyectoDocumento);
    log.debug("update(ProyectoDocumento proyectoDocumento, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ProyectoDocumento} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoDocumento}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve el {@link ProyectoDocumento} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoDocumento}.
   * @return ProyectoDocumento {@link ProyectoDocumento} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  ProyectoDocumento findById(@PathVariable Long id) {
    log.debug("ProyectoDocumento findById(Long id) - start");
    ProyectoDocumento returnValue = service.findById(id);
    log.debug("ProyectoDocumento findById(Long id) - end");
    return returnValue;
  }

}
