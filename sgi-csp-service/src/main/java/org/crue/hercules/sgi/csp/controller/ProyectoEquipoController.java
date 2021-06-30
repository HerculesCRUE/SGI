package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.service.ProyectoEquipoService;
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
 * ProyectoEquipoController
 */

@RestController
@RequestMapping("/proyectoequipos")
@Slf4j
public class ProyectoEquipoController {

  /** ProyectoEquipo service */
  private final ProyectoEquipoService service;

  public ProyectoEquipoController(ProyectoEquipoService proyectoEquipoService) {
    log.debug("ProyectoEquipoController(ProyectoEquipoService proyectoEquipoService) - start");
    this.service = proyectoEquipoService;
    log.debug("ProyectoEquipoController(ProyectoEquipoService proyectoEquipoService) - end");
  }

  /**
   * Actualiza el listado de {@link ProyectoEquipo} de la {@link Proyecto} con el
   * listado proyectoEquipos añadiendo, editando o eliminando los elementos segun
   * proceda.
   * 
   * @param proyectoId      Id de la {@link Proyecto}.
   * @param proyectoEquipos lista con los nuevos {@link ProyectoEquipo} a guardar.
   * @return Lista actualizada con los {@link ProyectoEquipo}.
   */
  @PatchMapping("/{proyectoId}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<List<ProyectoEquipo>> update(@PathVariable Long proyectoId,
      @Valid @RequestBody List<ProyectoEquipo> proyectoEquipos) {
    log.debug("update(List<ProyectoEquipo> proyectoEquipos, proyectoId) - start");
    List<ProyectoEquipo> returnValue = service.update(proyectoId, proyectoEquipos);
    log.debug("update(List<ProyectoEquipo> proyectoEquipos, proyectoId) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Devuelve el {@link ProyectoEquipo} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoEquipo}.
   * @return ProyectoEquipo {@link ProyectoEquipo} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  ProyectoEquipo findById(@PathVariable Long id) {
    log.debug("ProyectoEquipo findById(Long id) - start");
    ProyectoEquipo returnValue = service.findById(id);
    log.debug("ProyectoEquipo findById(Long id) - end");
    return returnValue;
  }

}
