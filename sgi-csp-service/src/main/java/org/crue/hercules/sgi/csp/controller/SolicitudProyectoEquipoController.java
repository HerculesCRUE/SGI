package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEquipoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * SolicitudProyectoEquipoController
 */
@RestController
@Validated
@RequestMapping("/solicitudproyectoequipo")
@Slf4j
public class SolicitudProyectoEquipoController {

  /** SolicitudProyectoEquipoService service */
  private final SolicitudProyectoEquipoService service;

  /**
   * Instancia un nuevo SolicitudProyectoEquipoController.
   * 
   * @param solicitudProyectoEquipoService {@link SolicitudProyectoEquipoService}.
   */
  public SolicitudProyectoEquipoController(SolicitudProyectoEquipoService solicitudProyectoEquipoService) {
    this.service = solicitudProyectoEquipoService;
  }

  /**
   * Devuelve el {@link SolicitudProyectoEquipo} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoEquipo}.
   * @return SolicitudProyectoEquipo {@link SolicitudProyectoEquipo}
   *         correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  SolicitudProyectoEquipo findById(@PathVariable Long id) {
    log.debug("SolicitudProyectoEquipo findById(Long id) - start");
    SolicitudProyectoEquipo returnValue = service.findById(id);
    log.debug("SolicitudProyectoEquipo findById(Long id) - end");
    return returnValue;
  }

  @PatchMapping("/{solicitudProyectoId}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public ResponseEntity<List<SolicitudProyectoEquipo>> updateMiembrosEquipo(@PathVariable Long solicitudProyectoId,
      @Valid @RequestBody List<SolicitudProyectoEquipo> solicitudProyectoEquipos) {
    log.debug("updateMiembrosEquipo(Long solicitudId, List<SolicitudProyectoEquipo> solicitudProyectoEquipos) - start");

    solicitudProyectoEquipos.stream().forEach(solicitudProyectoEquipo -> {
      if (solicitudProyectoEquipo.getSolicitudProyectoId() != solicitudProyectoId) {
        throw new NoRelatedEntitiesException(SolicitudProyectoEquipo.class, Solicitud.class);
      }
    });

    List<SolicitudProyectoEquipo> returnValue = service.updateSolicitudProyectoEquipo(solicitudProyectoId,
        solicitudProyectoEquipos);
    log.debug("updateMiembrosEquipo(Long solicitudId, List<SolicitudProyectoEquipo> solicitudProyectoEquipos) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}