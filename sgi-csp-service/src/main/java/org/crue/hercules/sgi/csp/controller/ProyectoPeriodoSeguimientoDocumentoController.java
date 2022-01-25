package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoDocumentoService;

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
 * ProyectoPeriodoSeguimientoDocumentoController
 */
@RestController
@RequestMapping("/proyectoperiodoseguimientodocumentos")
@Slf4j
public class ProyectoPeriodoSeguimientoDocumentoController {

  /** ProyectoPeriodoSeguimientoDocumentoService service */
  private final ProyectoPeriodoSeguimientoDocumentoService service;

  /**
   * Instancia un nuevo ProyectoPeriodoSeguimientoDocumentoController.
   * 
   * @param proyectoPeriodoSeguimientoDocumentoService {@link ProyectoPeriodoSeguimientoDocumentoService}.
   */
  public ProyectoPeriodoSeguimientoDocumentoController(
      ProyectoPeriodoSeguimientoDocumentoService proyectoPeriodoSeguimientoDocumentoService) {
    this.service = proyectoPeriodoSeguimientoDocumentoService;
  }

  /**
   * Crea nuevo {@link ProyectoPeriodoSeguimientoDocumento}
   * 
   * @param proyectoPeriodoSeguimientoDocumento {@link ProyectoPeriodoSeguimientoDocumento}.
   *                                            que se quiere crear.
   * @return Nuevo {@link ProyectoPeriodoSeguimientoDocumento} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoPeriodoSeguimientoDocumento> create(
      @Valid @RequestBody ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento) {
    log.debug("create(ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento) - start");
    ProyectoPeriodoSeguimientoDocumento returnValue = service.create(proyectoPeriodoSeguimientoDocumento);
    log.debug("create(ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link ProyectoPeriodoSeguimientoDocumento}.
   * 
   * @param proyectoPeriodoSeguimientoDocumento {@link ProyectoPeriodoSeguimientoDocumento}
   *                                            a actualizar.
   * @param id                                  Identificador
   *                                            {@link ProyectoPeriodoSeguimientoDocumento}
   *                                            a actualizar.
   * @param authentication                      Datos autenticación.
   * @return ProyectoPeriodoSeguimientoDocumento
   *         {@link ProyectoPeriodoSeguimientoDocumento} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoPeriodoSeguimientoDocumento update(
      @Valid @RequestBody ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento,
      @PathVariable Long id, Authentication authentication) {
    log.debug("update(ProyectoPeriodoSeguimientoDocumento ProyectoPeriodoSeguimientoDocumento, Long id) - start");
    proyectoPeriodoSeguimientoDocumento.setId(id);
    ProyectoPeriodoSeguimientoDocumento returnValue = service.update(proyectoPeriodoSeguimientoDocumento);
    log.debug("update(ProyectoPeriodoSeguimientoDocumento ProyectoPeriodoSeguimientoDocumento, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link ProyectoPeriodoSeguimientoDocumento} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoPeriodoSeguimientoDocumento}.
   * @return ProyectoPeriodoSeguimientoDocumento
   *         {@link ProyectoPeriodoSeguimientoDocumento} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ProyectoPeriodoSeguimientoDocumento findById(@PathVariable Long id) {
    log.debug("ProyectoPeriodoSeguimientoDocumento findById(Long id) - start");
    ProyectoPeriodoSeguimientoDocumento returnValue = service.findById(id);
    log.debug("ProyectoPeriodoSeguimientoDocumento findById(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link ProyectoPeriodoSeguimientoDocumento} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoPeriodoSeguimientoDocumento}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

}
