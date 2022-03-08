package org.crue.hercules.sgi.csp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.csp.service.ComunicadosService;
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

  private ComunicadosService service;

  public ComunicadosController(ComunicadosService service) {
    this.service = service;
  }

  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-csp'))")
  @GetMapping("/inicio-presentacion-justificacion-gastos")
  public void enviarComunicadoInicioPresentacionJustificacionGastos() throws JsonProcessingException {
    log.debug("enviarComunicadoInicioPresentacionJustificacionGastos() - start");
    service.enviarComunicadoInicioPresentacionJustificacionGastos();
    log.debug("enviarComunicadoInicioPresentacionJustificacionGastos() - end");
  }
}
