package org.crue.hercules.sgi.pii.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComunicadosService {
  public static final String CONFIG_PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DESTINATARIOS = "pii-com-fecha-fin-pri-sol-prot-destinatarios";
  public static final String CONFIG_PII_COM_AVISO_FIN_PLAZO_FASE_NAC_REG_SOLICITUD_PROTECCION_DESTINATARIOS = "fin-plaz-fases-nac-reg-sol-prot-destinatarios";

  private final SolicitudProteccionComService solicitudProteccionComService;

  public void enviarComunicadoMesesHastaFinPrioridadSolicitudProteccion() {
    this.solicitudProteccionComService.enviarComunicadoMesesHastaFinPrioridadSolicitudProteccion();
  }

  public void enviarComunicadoAvisoFinPlazoPresentacionFasesNacionalesRegionalesSolicitudProteccion() {
    this.solicitudProteccionComService
        .enviarComunicadoAvisoFinPlazoPresentacionFasesNacionalesRegionalesSolicitudProteccion();
  }

}
