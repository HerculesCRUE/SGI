package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.csp.service.EstadoAutorizacionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * EstadoAutorizacionController
 */
@RestController
@RequestMapping(EstadoAutorizacionController.REQUEST_MAPPING)
@Slf4j
public class EstadoAutorizacionController {
  public static final String REQUEST_MAPPING = "/estadosautorizaciones";

  private final EstadoAutorizacionService service;

  public EstadoAutorizacionController(EstadoAutorizacionService service) {
    this.service = service;
  }

  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  EstadoAutorizacion findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    EstadoAutorizacion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
