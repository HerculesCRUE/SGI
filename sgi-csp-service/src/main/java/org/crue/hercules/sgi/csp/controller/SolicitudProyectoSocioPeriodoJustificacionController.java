package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioPeriodoJustificacionService;
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
 * SolicitudProyectoSocioPeriodoJustificacionController
 */

@RestController
@RequestMapping("/solicitudproyectosocioperiodojustificaciones")
@Slf4j
public class SolicitudProyectoSocioPeriodoJustificacionController {

  /** SolicitudProyectoSocioPeriodoJustificacion service */
  private final SolicitudProyectoSocioPeriodoJustificacionService service;

  public SolicitudProyectoSocioPeriodoJustificacionController(
      SolicitudProyectoSocioPeriodoJustificacionService solicitudProyectoSocioPeriodoJustificacionService) {
    log.debug(
        "SolicitudProyectoSocioPeriodoJustificacionController(SolicitudProyectoSocioPeriodoJustificacionService solicitudProyectoSocioPeriodoJustificacionService) - start");
    this.service = solicitudProyectoSocioPeriodoJustificacionService;
    log.debug(
        "SolicitudProyectoSocioPeriodoJustificacionController(SolicitudProyectoSocioPeriodoJustificacionService solicitudProyectoSocioPeriodoJustificacionService) - end");
  }

  /**
   * Devuelve el {@link SolicitudProyectoSocioPeriodoJustificacion} con el id
   * indicado.
   * 
   * @param id Identificador de
   *           {@link SolicitudProyectoSocioPeriodoJustificacion}.
   * @return {@link SolicitudProyectoSocioPeriodoJustificacion} correspondiente al
   *         id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public SolicitudProyectoSocioPeriodoJustificacion findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    SolicitudProyectoSocioPeriodoJustificacion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link SolicitudProyectoSocioPeriodoJustificacion} de
   * la {@link SolicitudProyectoSocio} con el listado
   * solicitudProyectoSocioPeriodoJustificaciones añadiendo, editando o eliminando
   * los elementos segun proceda.
   * 
   * @param solicitudProyectoSocioId                     Id de la
   *                                                     {@link SolicitudProyectoSocio}.
   * @param solicitudProyectoSocioPeriodoJustificaciones lista con los nuevos
   *                                                     {@link SolicitudProyectoSocioPeriodoJustificacion}
   *                                                     a guardar.
   * @return Lista actualizada con los
   *         {@link SolicitudProyectoSocioPeriodoJustificacion}.
   */
  @PatchMapping("/{solicitudProyectoSocioId}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public ResponseEntity<List<SolicitudProyectoSocioPeriodoJustificacion>> update(
      @PathVariable Long solicitudProyectoSocioId,
      @Valid @RequestBody List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificaciones) {
    log.debug(
        "update(Long solicitudProyectoId, List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificaciones) - start");
    List<SolicitudProyectoSocioPeriodoJustificacion> returnValue = service.update(solicitudProyectoSocioId,
        solicitudProyectoSocioPeriodoJustificaciones);
    log.debug(
        "update(Long solicitudProyectoId, List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificaciones) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
