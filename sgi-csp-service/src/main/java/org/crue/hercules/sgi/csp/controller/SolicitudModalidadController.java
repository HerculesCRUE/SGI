package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.service.SolicitudModalidadService;
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
 * SolicitudModalidadController
 */
@RestController
@RequestMapping("/solicitudmodalidades")
@Slf4j
public class SolicitudModalidadController {

  /** SolicitudModalidad service */
  private final SolicitudModalidadService service;

  /**
   * Instancia un nuevo SolicitudModalidadController.
   * 
   * @param solicitudModalidadService {@link SolicitudModalidadService}.
   */
  public SolicitudModalidadController(SolicitudModalidadService solicitudModalidadService) {
    this.service = solicitudModalidadService;
  }

  /**
   * Crea nuevo {@link SolicitudModalidad}
   * 
   * @param solicitudModalidad {@link SolicitudModalidad}. que se quiere crear.
   * @return Nuevo {@link SolicitudModalidad} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-INV-C', 'CSP-SOL-E')")
  public ResponseEntity<SolicitudModalidad> create(@Valid @RequestBody SolicitudModalidad solicitudModalidad) {
    log.debug("create(SolicitudModalidad solicitudModalidad) - start");
    SolicitudModalidad returnValue = service.create(solicitudModalidad);
    log.debug("create(SolicitudModalidad solicitudModalidad) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudModalidad}.
   * 
   * @param solicitudModalidad {@link SolicitudModalidad} a actualizar.
   * @param id                 Identificador {@link SolicitudModalidad} a
   *                           actualizar.
   * @return SolicitudModalidad {@link SolicitudModalidad} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public SolicitudModalidad update(@Valid @RequestBody SolicitudModalidad solicitudModalidad, @PathVariable Long id) {
    log.debug("update(SolicitudModalidad solicitudModalidad, Long id) - start");
    solicitudModalidad.setId(id);
    SolicitudModalidad returnValue = service.update(solicitudModalidad);
    log.debug("update(SolicitudModalidad solicitudModalidad, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link SolicitudModalidad} con id indicado.
   * 
   * @param id Identificador de {@link SolicitudModalidad}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve el {@link SolicitudModalidad} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudModalidad}.
   * @return SolicitudModalidad {@link SolicitudModalidad} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public SolicitudModalidad findById(@PathVariable Long id) {
    log.debug("SolicitudModalidad findById(Long id) - start");
    SolicitudModalidad returnValue = service.findById(id);
    log.debug("SolicitudModalidad findById(Long id) - end");
    return returnValue;
  }

}
