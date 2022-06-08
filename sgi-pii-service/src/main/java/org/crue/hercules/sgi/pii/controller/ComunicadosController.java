package org.crue.hercules.sgi.pii.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.pii.service.ComunicadosService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(ComunicadosController.REQUEST_MAPPING)
public class ComunicadosController {
  /** El path que gestiona este controlador */
  public static final String REQUEST_MAPPING = "/comunicados";

  private final ComunicadosService comunicadoService;

  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-pii'))")
  @GetMapping("/meses-hasta-fecha-fin-prioridad-solicitud-proteccion")
  public void enviarComunicadoMesesHastaFinPrioridadSolicitudProteccion() throws JsonProcessingException {
    comunicadoService.enviarComunicadoMesesHastaFinPrioridadSolicitudProteccion();
  }

  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-pii'))")
  @GetMapping("/aviso-fin-plazo-presentacion-fases-nacionales-regionales-solicitud-proteccion")
  public void enviarComunicadoMesesHastaFinPlazoPresentacionFasesNacionalesRegionalesSolicitudProteccion()
      throws JsonProcessingException {
    comunicadoService.enviarComunicadoAvisoFinPlazoPresentacionFasesNacionalesRegionalesSolicitudProteccion();
  }
}
