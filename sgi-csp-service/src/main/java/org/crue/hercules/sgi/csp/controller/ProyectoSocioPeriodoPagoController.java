package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoPagoService;
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
 * ProyectoSocioPeriodoPagoController
 */
@RestController
@RequestMapping("/proyectosocioperiodopagos")
@Slf4j
public class ProyectoSocioPeriodoPagoController {

  /** ProyectoSocioPeriodoPagoService service */
  private final ProyectoSocioPeriodoPagoService service;

  /**
   * Instancia un nuevo ProyectoSocioPeriodoPagoController.
   * 
   * @param service {@link ProyectoSocioPeriodoPagoService}
   */
  public ProyectoSocioPeriodoPagoController(ProyectoSocioPeriodoPagoService service) {
    this.service = service;
  }

  /**
   * Actualiza el listado de {@link ProyectoSocioPeriodoPago} del
   * {@link ProyectoSocio} con el listado proyectoSocioPeriodoPagos añadiendo,
   * editando o eliminando los elementos segun proceda.
   * 
   * @param proyectoSocioPeriodoPagos lista {@link ProyectoSocioPeriodoPago} a
   *                                  actualizar.
   * @param proyectoSocioId           Identificador {@link ProyectoSocio} a
   *                                  actualizar.
   * @return Lista actualizada de {@link ProyectoSocioPeriodoPago}.
   */
  @PatchMapping("/{proyectoSocioId}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<List<ProyectoSocioPeriodoPago>> update(@PathVariable Long proyectoSocioId,
      @Valid @RequestBody List<ProyectoSocioPeriodoPago> proyectoSocioPeriodoPagos) {
    log.debug("update(Long proyectoSocioId, List<ProyectoSocioPeriodoPago> proyectoSocioPeriodoPagos) - start");
    List<ProyectoSocioPeriodoPago> returnValue = service.update(proyectoSocioId, proyectoSocioPeriodoPagos);
    log.debug("update(Long proyectoSocioId, List<ProyectoSocioPeriodoPago> proyectoSocioPeriodoPagos) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Devuelve el {@link ProyectoSocioPeriodoPago} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoSocioPeriodoPago}.
   * @return {@link ProyectoSocioPeriodoPago} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  ProyectoSocioPeriodoPago findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoSocioPeriodoPago returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
