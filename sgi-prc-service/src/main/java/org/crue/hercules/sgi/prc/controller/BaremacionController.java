package org.crue.hercules.sgi.prc.controller;

import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.service.BaremacionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * BaremacionController
 */
@RestController
@RequestMapping(BaremacionController.MAPPING)
@Slf4j
public class BaremacionController {
  public static final String MAPPING = "/baremacion";

  private final BaremacionService service;

  /**
   * Instancia un nuevo BaremacionController.
   * 
   * @param service {@link BaremacionService}
   */
  public BaremacionController(BaremacionService service) {
    this.service = service;
  }

  /**
   * Lanza el algoritmo de baremación a partir de {@link ConvocatoriaBaremacion}
   * con id indicado.
   *
   * @param convocatoriaBaremacionId id de {@link ConvocatoriaBaremacion}.
   */
  @PostMapping("/{convocatoriaBaremacionId}")
  @PreAuthorize("isAuthenticated()")
  @ResponseStatus(value = HttpStatus.ACCEPTED)
  public void baremacion(@PathVariable Long convocatoriaBaremacionId) {
    log.debug("baremacion(String convocatoriaBaremacionId) -  start");

    service.baremacion(convocatoriaBaremacionId);

    log.debug("baremacion(String convocatoriaBaremacionId) -  end");

  }
}