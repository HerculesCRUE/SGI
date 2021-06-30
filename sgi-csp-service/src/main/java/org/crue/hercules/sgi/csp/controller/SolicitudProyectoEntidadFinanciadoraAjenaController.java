package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadFinanciadoraAjenaService;
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
 * SolicitudProyectoEntidadFinanciadoraAjenaController
 */

@RestController
@RequestMapping("/solicitudproyectoentidadfinanciadoraajenas")
@Slf4j
public class SolicitudProyectoEntidadFinanciadoraAjenaController {

  /** SolicitudProyectoEntidadFinanciadoraAjena service */
  private final SolicitudProyectoEntidadFinanciadoraAjenaService service;

  public SolicitudProyectoEntidadFinanciadoraAjenaController(
      SolicitudProyectoEntidadFinanciadoraAjenaService solicitudProyectoEntidadFinanciadoraAjenaService) {
    log.debug(
        "SolicitudProyectoEntidadFinanciadoraAjenaController(SolicitudProyectoEntidadFinanciadoraAjenaService solicitudProyectoEntidadFinanciadoraAjenaService) - start");
    this.service = solicitudProyectoEntidadFinanciadoraAjenaService;
    log.debug(
        "SolicitudProyectoEntidadFinanciadoraAjenaController(SolicitudProyectoEntidadFinanciadoraAjenaService solicitudProyectoEntidadFinanciadoraAjenaService) - end");
  }

  /**
   * Devuelve el {@link SolicitudProyectoEntidadFinanciadoraAjena} con el id
   * indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * @return {@link SolicitudProyectoEntidadFinanciadoraAjena} correspondiente al
   *         id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  SolicitudProyectoEntidadFinanciadoraAjena findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    SolicitudProyectoEntidadFinanciadoraAjena returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea nuevo {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * 
   * @param solicitudProyectoEntidadFinanciadoraAjena {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   *                                                  que se quiere crear.
   * @return Nuevo {@link SolicitudProyectoEntidadFinanciadoraAjena} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public ResponseEntity<SolicitudProyectoEntidadFinanciadoraAjena> create(
      @Valid @RequestBody SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena) {
    log.debug("create(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena) - start");
    SolicitudProyectoEntidadFinanciadoraAjena returnValue = service.create(solicitudProyectoEntidadFinanciadoraAjena);
    log.debug("create(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link SolicitudProyectoEntidadFinanciadoraAjena} con el id
   * indicado.
   * 
   * @param solicitudProyectoEntidadFinanciadoraAjena {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *                                                  a actualizar.
   * @param id                                        id
   *                                                  {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *                                                  a actualizar.
   * @return {@link SolicitudProyectoEntidadFinanciadoraAjena} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  SolicitudProyectoEntidadFinanciadoraAjena update(@Validated({ Update.class,
      Default.class }) @RequestBody SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena,
      @PathVariable Long id) {
    log.debug(
        "update(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena, Long id) - start");
    solicitudProyectoEntidadFinanciadoraAjena.setId(id);
    SolicitudProyectoEntidadFinanciadoraAjena returnValue = service.update(solicitudProyectoEntidadFinanciadoraAjena);
    log.debug(
        "update(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena, Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link SolicitudProyectoEntidadFinanciadoraAjena} con el id
   * indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoEntidadFinanciadoraAjena}.
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
