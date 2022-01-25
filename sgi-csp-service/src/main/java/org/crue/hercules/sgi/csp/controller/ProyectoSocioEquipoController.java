package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.service.ProyectoSocioEquipoService;
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
 * ProyectoSocioEquipoController
 */

@RestController
@RequestMapping("/proyectosocioequipos")
@Slf4j
public class ProyectoSocioEquipoController {

  /** ProyectoSocioEquipo service */
  private final ProyectoSocioEquipoService service;

  public ProyectoSocioEquipoController(ProyectoSocioEquipoService proyectoSocioEquipoService) {
    log.debug("ProyectoSocioEquipoController(ProyectoSocioEquipoService proyectoSocioEquipoService) - start");
    this.service = proyectoSocioEquipoService;
    log.debug("ProyectoSocioEquipoController(ProyectoSocioEquipoService proyectoSocioEquipoService) - end");
  }

  /**
   * Devuelve el {@link ProyectoSocioEquipo} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoSocioEquipo}.
   * @return {@link ProyectoSocioEquipo} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ProyectoSocioEquipo findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoSocioEquipo returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link ProyectoSocioEquipo} de la
   * {@link ProyectoSocio} con el listado proyectoSocioEquipoes añadiendo,
   * editando o eliminando los elementos segun proceda.
   * 
   * @param proyectoSocioId     Id del {@link ProyectoSocio}.
   * @param proyectoSocioEquipo lista con los nuevos {@link ProyectoSocioEquipo} a
   *                            guardar.
   * @return Lista actualizada con los {@link ProyectoSocioEquipo}.
   */
  @PatchMapping("/{proyectoSocioId}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<List<ProyectoSocioEquipo>> update(@PathVariable Long proyectoSocioId,
      @Valid @RequestBody List<ProyectoSocioEquipo> proyectoSocioEquipo) {
    log.debug("update(Long convocatoriaId, List<ProyectoSocioEquipo> proyectoSocioEquipoes) - start");
    List<ProyectoSocioEquipo> returnValue = service.update(proyectoSocioId, proyectoSocioEquipo);
    log.debug("update(Long convocatoriaId, List<ProyectoSocioEquipo> proyectoSocioEquipoes) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
