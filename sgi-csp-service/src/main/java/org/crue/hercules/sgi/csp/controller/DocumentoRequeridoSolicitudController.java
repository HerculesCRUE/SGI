package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.service.DocumentoRequeridoSolicitudService;
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
 * DocumentoRequeridoSolicitudController
 */

@RestController
@RequestMapping("/documentorequiridosolicitudes")
@Slf4j
public class DocumentoRequeridoSolicitudController {

  /** DocumentoRequeridoSolicitud service */
  private final DocumentoRequeridoSolicitudService service;

  public DocumentoRequeridoSolicitudController(DocumentoRequeridoSolicitudService documentoRequeridoSolicitudService) {
    log.debug(
        "DocumentoRequeridoSolicitudController(DocumentoRequeridoSolicitudService documentoRequeridoSolicitudService) - start");
    this.service = documentoRequeridoSolicitudService;
    log.debug(
        "DocumentoRequeridoSolicitudController(DocumentoRequeridoSolicitudService documentoRequeridoSolicitudService) - end");
  }

  /**
   * Crea nuevo {@link DocumentoRequeridoSolicitud}.
   * 
   * @param documentoRequeridoSolicitud {@link DocumentoRequeridoSolicitud}. que
   *                                    se quiere crear.
   * @return Nuevo {@link DocumentoRequeridoSolicitud} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-E')")
  public ResponseEntity<DocumentoRequeridoSolicitud> create(
      @Valid @RequestBody DocumentoRequeridoSolicitud documentoRequeridoSolicitud) {
    log.debug("create(DocumentoRequeridoSolicitud documentoRequeridoSolicitud) - start");
    DocumentoRequeridoSolicitud returnValue = service.create(documentoRequeridoSolicitud);
    log.debug("create(DocumentoRequeridoSolicitud documentoRequeridoSolicitud) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link DocumentoRequeridoSolicitud} con el id indicado.
   * 
   * @param documentoRequeridoSolicitud {@link DocumentoRequeridoSolicitud} a
   *                                    actualizar.
   * @param id                          id {@link DocumentoRequeridoSolicitud} a
   *                                    actualizar.
   * @return {@link DocumentoRequeridoSolicitud} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public DocumentoRequeridoSolicitud update(@Valid @RequestBody DocumentoRequeridoSolicitud documentoRequeridoSolicitud,
      @PathVariable Long id) {
    log.debug("update(DocumentoRequeridoSolicitud documentoRequeridoSolicitud, Long id) - start");
    documentoRequeridoSolicitud.setId(id);
    DocumentoRequeridoSolicitud returnValue = service.update(documentoRequeridoSolicitud);
    log.debug("update(DocumentoRequeridoSolicitud documentoRequeridoSolicitud, Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link DocumentoRequeridoSolicitud} con id indicado.
   * 
   * @param id Identificador de {@link DocumentoRequeridoSolicitud}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve el {@link DocumentoRequeridoSolicitud} con el id indicado.
   * 
   * @param id Identificador de {@link DocumentoRequeridoSolicitud}.
   * @return {@link DocumentoRequeridoSolicitud} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public DocumentoRequeridoSolicitud findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    DocumentoRequeridoSolicitud returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }
}
