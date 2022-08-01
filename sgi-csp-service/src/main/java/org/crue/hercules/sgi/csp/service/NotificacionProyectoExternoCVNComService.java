package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.com.CspComRecepcionNotificacionesCVNProyectoExtData;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificacionProyectoExternoCVNComService {
  private static final String CONFIG_CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO_DESTINATARIOS = "csp-pro-recep-not-cvn-pext-destinatarios";

  private final SgiApiSgpService sgiApiSgpService;
  private final SgiApiCnfService configService;
  private final SgiApiComService emailService;

  public void enviarComunicadoRecepcionNotificacionCVNProyectoExterno(NotificacionProyectoExternoCVN notificacion) {

    CspComRecepcionNotificacionesCVNProyectoExtData data = CspComRecepcionNotificacionesCVNProyectoExtData.builder()
        .nombreApellidosCreador(getNombreApellidosSolicitante(notificacion.getSolicitanteRef()))
        .fechaCreacion(notificacion.getCreationDate() == null ? Instant.now() : notificacion.getCreationDate())
        .tituloProyecto(notificacion.getTitulo())
        .build();

    try {
      EmailOutput comunicado = this.emailService
          .createComunicadoRecepcionNotificacionCVNProyectoExterno(data,
              this.getRecipientsPreconfigurados());
      this.emailService.sendEmail(comunicado.getId());

    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
  }

  private String getNombreApellidosSolicitante(String solicitanteRef) {
    PersonaOutput datos = this.sgiApiSgpService.findById(solicitanteRef);
    return String.format("%s %s", datos.getNombre(), datos.getApellidos());
  }

  private List<Recipient> getRecipientsPreconfigurados() throws JsonProcessingException {
    List<String> destinatarios = configService
        .findStringListByName(
            CONFIG_CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO_DESTINATARIOS);

    return destinatarios.stream()
        .map(destinatario -> Recipient.builder().name(destinatario).address(destinatario)
            .build())
        .collect(Collectors.toList());
  }

}
