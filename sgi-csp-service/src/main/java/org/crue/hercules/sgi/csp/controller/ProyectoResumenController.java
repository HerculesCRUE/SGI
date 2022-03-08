package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.dto.ProyectoResumenOutput;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.service.ProyectoService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoResumenController
 */
@RestController
@RequestMapping(ProyectoResumenController.REQUEST_MAPPING)
@Slf4j
public class ProyectoResumenController {
  /** El path que gestiona este controlador */
  public static final String REQUEST_MAPPING = "/proyectos-resumen";

  private ModelMapper modelMapper;

  /** Proyecto service */
  private final ProyectoService service;

  /**
   * Instancia un nuevo ProyectoResumenController.
   * 
   * @param modelMapper     {@link ModelMapper}.
   * @param proyectoService {@link ProyectoService}.
   */

  public ProyectoResumenController(ModelMapper modelMapper, ProyectoService proyectoService) {
    this.modelMapper = modelMapper;
    this.service = proyectoService;
  }

  /**
   * Devuelve el {@link Proyecto} con el id indicado.
   * 
   * @param id Identificador de {@link Proyecto}.
   * @return Proyecto {@link Proyecto} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('CSP-PRO-PRC-V')")
  public ProyectoResumenOutput findById(@PathVariable Long id) {
    log.debug("Proyecto findById(Long id) - start");
    Proyecto returnValue = service.findProyectoResumenById(id);
    log.debug("Proyecto findById(Long id) - end");
    return convert(returnValue);
  }

  private ProyectoResumenOutput convert(Proyecto proyecto) {
    return modelMapper.map(proyecto, ProyectoResumenOutput.class);
  }
}
