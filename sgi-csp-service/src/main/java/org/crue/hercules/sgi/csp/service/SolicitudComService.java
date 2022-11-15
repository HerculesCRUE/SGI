package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoAlegacionesData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoDefinitivoData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoProvisionalData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoSolicitadaData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudPeticionEvaluacionData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudUsuarioExternoData;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Enlace;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolicitudComService {
  private static final String CONFIG_CSP_COM_SOL_CAMB_EST_SOLICITADA = "csp-com-sol-camb-est-solicitada-destinatarios-";
  private static final String CONFIG_CSP_COM_SOL_CAMB_EST_ALEGACIONES = "csp-com-sol-camb-est-alegaciones-destinatarios-";

  private final SgiApiComService emailService;
  private final SgiApiCnfService configService;
  private final SolicitanteDataService solicitanteDataService;
  private final SgiConfigProperties sgiConfigProperties;

  public void enviarComunicadoSolicitudCambioEstadoSolicitada(Long solicitudId, String solicitanteRef,
      String unidadGestionRef, String tituloConvocatoria, Instant fechaPublicacionConvocatoria,
      Instant fechaCambioEstadoSolicitud)
      throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoSolicitada() - start");
    List<Recipient> recipients = getRecipients(unidadGestionRef, CONFIG_CSP_COM_SOL_CAMB_EST_SOLICITADA);
    if (!recipients.isEmpty()) {
      EmailOutput emailOutput = emailService.createComunicadoSolicitudCambioEstadoSolicitada(
          CspComSolicitudCambioEstadoSolicitadaData.builder()
              .tituloConvocatoria(tituloConvocatoria)
              .nombreApellidosSolicitante(
                  this.solicitanteDataService.getSolicitanteNombreApellidos(solicitudId, solicitanteRef))
              .fechaCambioEstadoSolicitud(fechaCambioEstadoSolicitud)
              .fechaPublicacionConvocatoria(fechaPublicacionConvocatoria).build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoSolicitudCambioEstadoSolicitada() - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoSolicitudCambioEstadoSolicitada() - end");
  }

  public void enviarComunicadoSolicitudCambioEstadoAlegaciones(Long solicitudId, String solicitanteRef,
      String unidadGestionRef, String tituloConvocatoria, String codigoInterno, Instant fechaCambioEstadoSolicitud,
      Instant fechaProvisionalConvocatoria) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoAlegaciones() - start");
    List<Recipient> recipients = getRecipients(unidadGestionRef, CONFIG_CSP_COM_SOL_CAMB_EST_ALEGACIONES);
    if (!recipients.isEmpty()) {
      EmailOutput emailOutput = emailService.createComunicadoSolicitudCambioEstadoAlegaciones(
          CspComSolicitudCambioEstadoAlegacionesData.builder()
              .tituloConvocatoria(tituloConvocatoria)
              .nombreApellidosSolicitante(
                  this.solicitanteDataService.getSolicitanteNombreApellidos(solicitudId, solicitanteRef))
              .codigoInternoSolicitud(codigoInterno)
              .fechaCambioEstadoSolicitud(fechaCambioEstadoSolicitud)
              .fechaProvisionalConvocatoria(fechaProvisionalConvocatoria).build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoSolicitudCambioEstadoAlegaciones() - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoSolicitudCambioEstadoAlegaciones() - end");
  }

  public void enviarComunicadoSolicitudCambioEstadoExclProv(Long solicitudId, String solicitanteRef,
      String tituloConvocatoria,
      Instant fechaProvisionalConvocatoria,
      List<ConvocatoriaEnlace> convocatoriaEnlaces) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoExclProv() - start");
    List<Enlace> enlaces = getListadoEnlacesComunicados(convocatoriaEnlaces);
    List<Recipient> recipients = this.solicitanteDataService.getSolicitanteRecipients(solicitudId, solicitanteRef);
    if (!recipients.isEmpty()) {
      EmailOutput emailOutput = emailService.createComunicadoSolicitudCambioEstadoExclProv(
          CspComSolicitudCambioEstadoProvisionalData.builder()
              .tituloConvocatoria(tituloConvocatoria)
              .fechaProvisionalConvocatoria(
                  fechaProvisionalConvocatoria)
              .enlaces(enlaces).build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoSolicitudCambioEstadoExclProv() - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoSolicitudCambioEstadoExclProv() - end");
  }

  public void enviarComunicadoSolicitudCambioEstadoExclDef(Long solicitudId, String solicitanteRef,
      String tituloConvocatoria,
      Instant fechaConcesionConvocatoria,
      List<ConvocatoriaEnlace> convocatoriaEnlaces) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoExclDef() - start");
    List<Enlace> enlaces = getListadoEnlacesComunicados(convocatoriaEnlaces);
    List<Recipient> recipients = this.solicitanteDataService.getSolicitanteRecipients(solicitudId, solicitanteRef);
    if (!recipients.isEmpty()) {
      EmailOutput emailOutput = emailService.createComunicadoSolicitudCambioEstadoExclDef(
          CspComSolicitudCambioEstadoDefinitivoData.builder()
              .tituloConvocatoria(tituloConvocatoria)
              .fechaConcesionConvocatoria(
                  fechaConcesionConvocatoria)
              .enlaces(enlaces).build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoSolicitudCambioEstadoExclDef() - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoSolicitudCambioEstadoExclDef() - end");
  }

  public void enviarComunicadoSolicitudCambioEstadoConcProv(Long solicitudId, String solicitanteRef,
      String tituloConvocatoria,
      Instant fechaProvisionalConvocatoria,
      List<ConvocatoriaEnlace> convocatoriaEnlaces) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoConcProv() - start");
    List<Enlace> enlaces = getListadoEnlacesComunicados(convocatoriaEnlaces);
    List<Recipient> recipients = this.solicitanteDataService.getSolicitanteRecipients(solicitudId, solicitanteRef);
    if (!recipients.isEmpty()) {
      EmailOutput emailOutput = emailService.createComunicadoSolicitudCambioEstadoConcProv(
          CspComSolicitudCambioEstadoProvisionalData.builder()
              .tituloConvocatoria(tituloConvocatoria)
              .fechaProvisionalConvocatoria(
                  fechaProvisionalConvocatoria)
              .enlaces(enlaces).build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoSolicitudCambioEstadoConcProv() - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoSolicitudCambioEstadoConcProv() - end");
  }

  public void enviarComunicadoSolicitudCambioEstadoDenProv(Long solicitudId, String solicitanteRef,
      String tituloConvocatoria,
      Instant fechaProvisionalConvocatoria,
      List<ConvocatoriaEnlace> convocatoriaEnlaces) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoDenProv() - start");
    List<Enlace> enlaces = getListadoEnlacesComunicados(convocatoriaEnlaces);
    List<Recipient> recipients = this.solicitanteDataService.getSolicitanteRecipients(solicitudId, solicitanteRef);
    if (!recipients.isEmpty()) {
      EmailOutput emailOutput = emailService.createComunicadoSolicitudCambioEstadoDenProv(
          CspComSolicitudCambioEstadoProvisionalData.builder()
              .tituloConvocatoria(tituloConvocatoria)
              .fechaProvisionalConvocatoria(
                  fechaProvisionalConvocatoria)
              .enlaces(enlaces).build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoSolicitudCambioEstadoDenProv() - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoSolicitudCambioEstadoDenProv() - end");
  }

  public void enviarComunicadoSolicitudCambioEstadoConc(Long solicitudId, String solicitanteRef,
      String tituloConvocatoria,
      Instant fechaConcesionConvocatoria,
      List<ConvocatoriaEnlace> convocatoriaEnlaces) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoConc() - start");
    List<Enlace> enlaces = getListadoEnlacesComunicados(convocatoriaEnlaces);
    List<Recipient> recipients = this.solicitanteDataService.getSolicitanteRecipients(solicitudId, solicitanteRef);
    if (!recipients.isEmpty()) {
      EmailOutput emailOutput = emailService.createComunicadoSolicitudCambioEstadoConc(
          CspComSolicitudCambioEstadoDefinitivoData.builder()
              .tituloConvocatoria(tituloConvocatoria)
              .fechaConcesionConvocatoria(
                  fechaConcesionConvocatoria)
              .enlaces(enlaces).build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoSolicitudCambioEstadoConc() - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoSolicitudCambioEstadoConc() - end");
  }

  public void enviarComunicadoSolicitudCambioEstadoDen(Long solicitudId, String solicitanteRef,
      String tituloConvocatoria,
      Instant fechaConcesionConvocatoria,
      List<ConvocatoriaEnlace> convocatoriaEnlaces) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoDen() - start");
    List<Enlace> enlaces = getListadoEnlacesComunicados(convocatoriaEnlaces);
    List<Recipient> recipients = this.solicitanteDataService.getSolicitanteRecipients(solicitudId,
        solicitanteRef);
    if (!recipients.isEmpty()) {
      EmailOutput emailOutput = emailService.createComunicadoSolicitudCambioEstadoDen(
          CspComSolicitudCambioEstadoDefinitivoData.builder()
              .tituloConvocatoria(tituloConvocatoria)
              .fechaConcesionConvocatoria(
                  fechaConcesionConvocatoria)
              .enlaces(enlaces).build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoSolicitudCambioEstadoDen() - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoSolicitudCambioEstadoDen() - end");
  }

  public void enviarComunicadoSolicitudAltaPeticionEvaluacionEti(Long solicitudId, String codigoPeticionEvaluacion,
      String codigoSolicitud, String solicitanteRef) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudAltaPeticionEvaluacionEti() - start");
    List<Recipient> recipients = this.solicitanteDataService.getSolicitanteRecipients(solicitudId, solicitanteRef);
    if (!CollectionUtils.isEmpty(recipients)) {
      EmailOutput emailOutput = emailService
          .createComunicadoSolicitudPeticionEvaluacionEti(
              CspComSolicitudPeticionEvaluacionData.builder().codigoPeticionEvaluacion(codigoPeticionEvaluacion)
                  .codigoSolicitud(codigoSolicitud).build(),
              recipients);

      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoSolicitudAltaPeticionEvaluacionEti() - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoSolicitudAltaPeticionEvaluacionEti() - end");
  }

  public void enviarComunicadoSolicitudUsuarioExterno(Long solicitudId, String tituloConvocatoria, String uuid)
      throws JsonProcessingException {
    log.debug(
        "enviarComunicadoSolicitudUsuarioExterno(Long solicitudId, String tituloConvocatoria, String uuid) - start");
    List<Recipient> recipients = this.solicitanteDataService.getSolicitanteRecipients(solicitudId, null);
    if (!recipients.isEmpty()) {
      EmailOutput emailOutput = emailService.createComunicadoSolicitudUsuarioExterno(
          CspComSolicitudUsuarioExternoData.builder()
              .tituloConvocatoria(tituloConvocatoria)
              .enlaceAplicacion(sgiConfigProperties.getWebUrl())
              .uuid(uuid)
              .build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoSolicitudUsuarioExterno(Long solicitudId, String tituloConvocatoria, String uuid) - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug(
        "enviarComunicadoSolicitudUsuarioExterno(Long solicitudId, String tituloConvocatoria, String uuid) - end");
  }

  private List<Enlace> getListadoEnlacesComunicados(List<ConvocatoriaEnlace> convocatoriaEnlaces) {
    List<Enlace> enlaces = new LinkedList<>();
    for (ConvocatoriaEnlace enlace : convocatoriaEnlaces) {
      Enlace nuevoEnlace = Enlace
          .builder()
          .descripcion(enlace.getDescripcion())
          .url(enlace.getUrl())
          .build();
      if (enlace.getTipoEnlace() != null) {
        nuevoEnlace.setTipoEnlace(enlace.getTipoEnlace().getNombre());
      }
      enlaces.add(nuevoEnlace);
    }
    return enlaces;
  }

  private List<Recipient> getRecipients(String unidadGestionRef, String template) throws JsonProcessingException {
    List<String> destinatarios = configService
        .findStringListByName(
            template + unidadGestionRef);

    return destinatarios.stream()
        .map(destinatario -> Recipient.builder().name(destinatario).address(destinatario).build())
        .collect(Collectors.toList());
  }
}
