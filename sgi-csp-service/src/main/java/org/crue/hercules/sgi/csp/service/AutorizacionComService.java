package org.crue.hercules.sgi.csp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.com.CspComCambioEstadoParticipacionAutorizacionProyectoExternoData;
import org.crue.hercules.sgi.csp.dto.com.CspComModificacionEstadoParticipacionProyectoExternoData;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutorizacionComService {

  private static final String CONFIG_CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DESTINATARIOS = "csp-pro-ex-mod-aut-participacion-destinatarios";

  private final SgiApiSgpService sgiApiSgpService;
  private final SgiApiCnfService configService;
  private final SgiApiComService emailService;

  public void enviarComunicadoModificadaAutorizacionParticipacionProyectoExterno(Autorizacion autorizacion) {

    PersonaOutput datosSolicitante = this.sgiApiSgpService.findById(autorizacion.getSolicitanteRef());

    CspComModificacionEstadoParticipacionProyectoExternoData data = CspComModificacionEstadoParticipacionProyectoExternoData
        .builder()
        .fecha(autorizacion.getEstado().getFecha())
        .tituloProyecto(autorizacion.getTituloProyecto())
        .nombreSolicitante(
            String.format("%s %s", datosSolicitante.getNombre(),
                datosSolicitante.getApellidos()))
        .build();
    try {
      EmailOutput comunicado = this.emailService
          .createComunicadoModificacionAutorizacionParticipacionProyectoExterno(data,
              this.getRecipientsPreconfigurados());
      this.emailService.sendEmail(comunicado.getId());

    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
  }

  public void enviarComunicadoCambioEstadoParticipacionAutorizacionProyectoExterno(Autorizacion autorizacion) {
    List<Recipient> recipients = getSolicitanteRecipients(autorizacion.getSolicitanteRef());

    CspComCambioEstadoParticipacionAutorizacionProyectoExternoData data = CspComCambioEstadoParticipacionAutorizacionProyectoExternoData
        .builder()
        .estadoSolicitudPext(autorizacion.getEstado().getEstado().name())
        .fechaEstadoSolicitudPext(autorizacion.getEstado().getFecha())
        .tituloPext(autorizacion.getTituloProyecto())
        .build();

    try {
      EmailOutput comunicado = this.emailService
          .createComunicadoCambioEstadoAutorizacionParticipacionProyectoExterno(data,
          recipients);
      this.emailService.sendEmail(comunicado.getId());

    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
  }

  private List<Recipient> getSolicitanteRecipients(String solicitanteRef) {
    PersonaOutput datosSolicitante = this.sgiApiSgpService.findById(solicitanteRef);

    return datosSolicitante.getEmails().stream().filter(email -> email.getPrincipal())
        .map(email -> Recipient
            .builder().name(email.getEmail()).address(email.getEmail())
            .build())
        .collect(Collectors.toList());
  }

  private List<Recipient> getRecipientsPreconfigurados() throws JsonProcessingException {
    List<String> destinatarios = configService
        .findStringListByName(
            CONFIG_CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DESTINATARIOS);

    return destinatarios.stream()
        .map(destinatario -> Recipient.builder().name(destinatario).address(destinatario)
            .build())
        .collect(Collectors.toList());
  }

}
