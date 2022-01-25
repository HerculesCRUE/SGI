package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioEquipoService;
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
 * SolicitudProyectoSocioEquipoController
 */
@RestController
@RequestMapping("/solicitudproyectosocioequipo")
@Slf4j
public class SolicitudProyectoSocioEquipoController {

  /** SolicitudProyectoSocioEquipoService service */
  private final SolicitudProyectoSocioEquipoService service;

  /**
   * Instancia un nuevo SolicitudProyectoSocioEquipoController.
   * 
   * @param solicitudProyectoEquipoSocioService {@link SolicitudProyectoSocioEquipoService}.
   */
  public SolicitudProyectoSocioEquipoController(
      SolicitudProyectoSocioEquipoService solicitudProyectoEquipoSocioService) {
    this.service = solicitudProyectoEquipoSocioService;
  }

  /**
   * Actualiza el listado de {@link SolicitudProyectoSocioEquipo} de la
   * {@link SolicitudProyectoSocio} con el listado solicitud proyecto equipo socio
   * añadiendo, editando o eliminando los elementos segun proceda.
   * 
   * @param proyectoSolictudSocioId       Id de la {@link SolicitudProyectoSocio}.
   * @param solicitudProyectoEquipoSocios lista con los nuevos
   *                                      {@link SolicitudProyectoSocioEquipo} a
   *                                      guardar.
   * @return Lista actualizada con los {@link SolicitudProyectoSocioEquipo}.
   */
  @PatchMapping("/{proyectoSolictudSocioId}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public ResponseEntity<List<SolicitudProyectoSocioEquipo>> updateConvocatoriaPeriodoJustificacionesConvocatoria(
      @PathVariable Long proyectoSolictudSocioId,
      @Valid @RequestBody List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocios) {
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long proyectoSolictudSocioId, List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocios) - start");
    List<SolicitudProyectoSocioEquipo> returnValue = service.update(proyectoSolictudSocioId,
        solicitudProyectoEquipoSocios);
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long proyectoSolictudSocioId, List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocios) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Devuelve el {@link SolicitudProyectoSocioEquipo} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoSocioEquipo}.
   * @return SolicitudProyectoSocioEquipo {@link SolicitudProyectoSocioEquipo}
   *         correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public SolicitudProyectoSocioEquipo findById(@PathVariable Long id) {
    log.debug("SolicitudProyectoSocioEquipo findById(Long id) - start");
    SolicitudProyectoSocioEquipo returnValue = service.findById(id);
    log.debug("SolicitudProyectoSocioEquipo findById(Long id) - end");
    return returnValue;
  }

}