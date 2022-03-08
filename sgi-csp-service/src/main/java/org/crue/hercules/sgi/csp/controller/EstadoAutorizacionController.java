package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.dto.EstadoAutorizacionOutput;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.csp.service.EstadoAutorizacionService;
import org.modelmapper.ModelMapper;
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
  private ModelMapper modelMapper;

  public EstadoAutorizacionController(EstadoAutorizacionService service,
      ModelMapper modelMapper) {
    this.service = service;
    this.modelMapper = modelMapper;
  }

  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  EstadoAutorizacionOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    EstadoAutorizacionOutput returnValue = convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  private EstadoAutorizacionOutput convert(EstadoAutorizacion estado) {
    return modelMapper.map(estado, EstadoAutorizacionOutput.class);
  }
}
