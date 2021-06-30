package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoJustificacionDocumentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoSocioPeriodoJustificacionDocumentoController
 */

@RestController
@RequestMapping("/proyectosocioperiodojustificaciondocumentos")
@Slf4j
public class ProyectoSocioPeriodoJustificacionDocumentoController {

  /** ProyectoSocioPeriodoJustificacionDocumento service */
  private final ProyectoSocioPeriodoJustificacionDocumentoService service;

  public ProyectoSocioPeriodoJustificacionDocumentoController(
      ProyectoSocioPeriodoJustificacionDocumentoService proyectoSocioPeriodoJustificacionDocumentoService) {
    log.debug(
        "ProyectoSocioPeriodoJustificacionDocumentoController(ProyectoSocioPeriodoJustificacionDocumentoService proyectoSocioPeriodoJustificacionDocumentoService) - start");
    this.service = proyectoSocioPeriodoJustificacionDocumentoService;
    log.debug(
        "ProyectoSocioPeriodoJustificacionDocumentoController(ProyectoSocioPeriodoJustificacionDocumentoService proyectoSocioPeriodoJustificacionDocumentoService) - end");
  }

  /**
   * Devuelve el {@link ProyectoSocioPeriodoJustificacionDocumento} con el id
   * indicado.
   * 
   * @param id Identificador de
   *           {@link ProyectoSocioPeriodoJustificacionDocumento}.
   * @return {@link ProyectoSocioPeriodoJustificacionDocumento} correspondiente al
   *         id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  ProyectoSocioPeriodoJustificacionDocumento findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoSocioPeriodoJustificacionDocumento returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link ProyectoSocioPeriodoJustificacionDocumento} de
   * la {@link ProyectoSocioPeriodoJustificacion} con el listado
   * proyectoSocioPeriodoJustificacionDocumentoes añadiendo, editando o eliminando
   * los elementos segun proceda.
   * 
   * @param proyectoSocioPeriodoJustificacionId          Id de la
   *                                                     {@link ProyectoSocioPeriodoJustificacion}.
   * @param proyectoSocioPeriodoJustificacionDocumentoes lista con los nuevos
   *                                                     {@link ProyectoSocioPeriodoJustificacionDocumento}
   *                                                     a guardar.
   * @return Lista actualizada con los
   *         {@link ProyectoSocioPeriodoJustificacionDocumento}.
   */
  @PatchMapping("/{proyectoSocioPeriodoJustificacionId}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<List<ProyectoSocioPeriodoJustificacionDocumento>> update(
      @PathVariable Long proyectoSocioPeriodoJustificacionId,
      @Valid @RequestBody List<ProyectoSocioPeriodoJustificacionDocumento> proyectoSocioPeriodoJustificacionDocumentoes) {
    log.debug(
        "update(Long proyectoSocioPeriodoJustificacionId, List<ProyectoSocioPeriodoJustificacionDocumento> proyectoSocioPeriodoJustificacionDocumentoes) - start");
    List<ProyectoSocioPeriodoJustificacionDocumento> returnValue = service.update(proyectoSocioPeriodoJustificacionId,
        proyectoSocioPeriodoJustificacionDocumentoes);
    log.debug(
        "update(Long proyectoSocioPeriodoJustificacionId, List<ProyectoSocioPeriodoJustificacionDocumento> proyectoSocioPeriodoJustificacionDocumentoes) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
