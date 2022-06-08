package org.crue.hercules.sgi.eti.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.eti.service.EvaluacionService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(ComunicadosController.REQUEST_MAPPING)
@Slf4j
public class ComunicadosController {
  /** El path que gestiona este controlador */
  public static final String REQUEST_MAPPING = "/comunicados";

  private MemoriaService memoriaService;
  private EvaluacionService evaluacionService;

  public ComunicadosController(MemoriaService memoriaService, EvaluacionService evaluacionService) {
    this.memoriaService = memoriaService;
    this.evaluacionService = evaluacionService;
  }

  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-eti'))")
  @GetMapping("/informes-retrospectiva-ceea-pendientes")
  public void enviarComunicadoInformeRetrospectivaCeeaPendiente() throws JsonProcessingException {
    log.debug("enviarComunicadoInformeRetrospectivaCeeaPendiente() - start");
    this.memoriaService.sendComunicadoInformeRetrospectivaCeeaPendiente();
    log.debug("enviarComunicadoInformeRetrospectivaCeeaPendiente() - end");
  }

  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-eti'))")
  @GetMapping("/informes-seguimiento-anual-pendientes")
  public void enviarComunicadoInformeSeguimientoAnualPendiente() throws JsonProcessingException {
    log.debug("enviarComunicadoInformeSeguimientoAnualPendiente() - start");
    this.evaluacionService.sendComunicadoInformeSeguimientoAnualPendiente();
    log.debug("enviarComunicadoInformeSeguimientoAnualPendiente() - end");
  }

}
