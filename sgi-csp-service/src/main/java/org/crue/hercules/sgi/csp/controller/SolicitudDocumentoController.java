package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.service.SolicitudDocumentoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
 * SolicitudDocumentoController
 */
@RestController
@RequestMapping("/solicituddocumentos")
@Slf4j
public class SolicitudDocumentoController {

  /** SolicitudDocumentoService service */
  private final SolicitudDocumentoService service;

  /**
   * Instancia un nuevo SolicitudDocumentoController.
   * 
   * @param solicitudDocumentoService {@link SolicitudDocumentoService}.
   */
  public SolicitudDocumentoController(SolicitudDocumentoService solicitudDocumentoService) {
    this.service = solicitudDocumentoService;
  }

  /**
   * Crea nuevo {@link SolicitudDocumento}
   * 
   * @param solicitudDocumento {@link SolicitudDocumento}. que se quiere crear.
   * @return Nuevo {@link SolicitudDocumento} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public ResponseEntity<SolicitudDocumento> create(@Valid @RequestBody SolicitudDocumento solicitudDocumento) {
    log.debug("create(SolicitudDocumento solicitudDocumento) - start");
    SolicitudDocumento returnValue = service.create(solicitudDocumento);
    log.debug("create(SolicitudDocumento solicitudDocumento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudDocumento}.
   * 
   * @param solicitudDocumento {@link SolicitudDocumento} a actualizar.
   * @param id                 Identificador {@link SolicitudDocumento} a
   *                           actualizar.
   * @param authentication     Datos autenticación.
   * @return SolicitudDocumento {@link SolicitudDocumento} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public SolicitudDocumento update(@Valid @RequestBody SolicitudDocumento solicitudDocumento, @PathVariable Long id,
      Authentication authentication) {
    log.debug("update(SolicitudDocumento solicitudDocumento, Long id) - start");
    solicitudDocumento.setId(id);
    SolicitudDocumento returnValue = service.update(solicitudDocumento);
    log.debug("update(SolicitudDocumento solicitudDocumento, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link SolicitudDocumento} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudDocumento}.
   * @return SolicitudDocumento {@link SolicitudDocumento} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  SolicitudDocumento findById(@PathVariable Long id) {
    log.debug("SolicitudDocumento findById(Long id) - start");
    SolicitudDocumento returnValue = service.findById(id);
    log.debug("SolicitudDocumento findById(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link SolicitudDocumento} con id indicado.
   * 
   * @param id Identificador de {@link SolicitudDocumento}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

}
