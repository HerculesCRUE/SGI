package org.crue.hercules.sgi.pii.service;

import java.time.Instant;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.pii.config.SgiConfigProperties;
import org.crue.hercules.sgi.pii.dto.com.EmailOutput;
import org.crue.hercules.sgi.pii.dto.com.PiiComMesesHastaFinPrioridadSolicitudProteccionData;
import org.crue.hercules.sgi.pii.dto.com.Recipient;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.repository.SolicitudProteccionRepository;
import org.crue.hercules.sgi.pii.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.pii.service.sgi.SgiApiComService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class SolicitudProteccionComService {

  private final SolicitudProteccionRepository solicitudProteccionRepository;
  private final SgiApiCnfService configService;
  private final SgiApiComService emailService;
  private final SgiConfigProperties sgiConfigProperties;

  public void enviarComunicadoMesesHastaFinPrioridadSolicitudProteccion() {

    IntStream.of(6, 3, 1).forEach(meses -> {
      Instant fechaFinPrioridadFrom = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
          .with(LocalTime.MIN).plusMonths(meses).toInstant();

      Instant fechaFinPrioridadTo = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
          .with(LocalTime.MAX).withNano(0).plusMonths(meses).toInstant();

      List<SolicitudProteccion> solicitudes = this.solicitudProteccionRepository
          .findByfechaFinPriorPresFasNacRecBetweenAndViaProteccionExtensionInternacionalFalse(fechaFinPrioridadFrom,
              fechaFinPrioridadTo);

      solicitudes.forEach(solicitud -> {
        this.emailService
            .sendEmail(this.buildComunicadoMesesHastaFinPrioridadSolicitudProteccion(solicitud, meses).getId());
      });
    });

  }

  public void enviarComunicadoAvisoFinPlazoPresentacionFasesNacionalesRegionalesSolicitudProteccion() {

    IntStream.of(12, 6, 2).forEach(meses -> {
      Instant fechaFinPresentacionFrom = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
          .with(LocalTime.MIN).plusMonths(meses).toInstant();

      Instant fechaFinPresentaciónTo = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
          .with(LocalTime.MAX).withNano(0).plusMonths(meses).toInstant();

      List<SolicitudProteccion> solicitudes = this.solicitudProteccionRepository
          .findByfechaFinPriorPresFasNacRecBetweenAndViaProteccionExtensionInternacionalTrue(fechaFinPresentacionFrom,
              fechaFinPresentaciónTo);

      solicitudes.forEach(solicitud -> {
        this.emailService
            .sendEmail(this
                .buildComunicadoAvisoFinPlazoPresentacionFasesNacionalesRegionalesSolicitudProteccion(solicitud, meses)
                .getId());
      });
    });

  }

  private EmailOutput buildComunicadoMesesHastaFinPrioridadSolicitudProteccion(SolicitudProteccion solicitud,
      Integer monthsBeforeFechaFinPrioridad) {
    PiiComMesesHastaFinPrioridadSolicitudProteccionData data = PiiComMesesHastaFinPrioridadSolicitudProteccionData
        .builder()
        .solicitudTitle(solicitud.getTitulo())
        .fechaFinPrioridad(solicitud.getFechaFinPriorPresFasNacRec())
        .monthsBeforeFechaFinPrioridad(monthsBeforeFechaFinPrioridad)
        .build();

    List<Recipient> recipients = getRecipientsComunicado(
        ComunicadosService.CONFIG_PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DESTINATARIOS);
    EmailOutput comunicado = null;
    try {
      comunicado = this.emailService.createComunicadoMesesHastaFinPrioridadSolicitudProteccion(data, recipients);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
    return comunicado;
  }

  private EmailOutput buildComunicadoAvisoFinPlazoPresentacionFasesNacionalesRegionalesSolicitudProteccion(
      SolicitudProteccion solicitud,
      Integer monthsBeforeFechaFinPresentacion) {
    PiiComMesesHastaFinPrioridadSolicitudProteccionData data = PiiComMesesHastaFinPrioridadSolicitudProteccionData
        .builder()
        .solicitudTitle(solicitud.getTitulo())
        .fechaFinPrioridad(solicitud.getFechaFinPriorPresFasNacRec())
        .monthsBeforeFechaFinPrioridad(monthsBeforeFechaFinPresentacion)
        .build();

    List<Recipient> recipients = getRecipientsComunicado(
        ComunicadosService.CONFIG_PII_COM_AVISO_FIN_PLAZO_FASE_NAC_REG_SOLICITUD_PROTECCION_DESTINATARIOS);
    EmailOutput comunicado = null;
    try {
      comunicado = this.emailService
          .createComunicadoAvisoFinPlazoPresentacionFasesNacionalesRegionalesSolicitudProteccion(data, recipients);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
    return comunicado;
  }

  private List<Recipient> getRecipientsComunicado(
      String configDestinatariosComunicado) {
    List<String> destinatarios = null;
    try {
      destinatarios = configService
          .findStringListByName(configDestinatariosComunicado);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
    if (destinatarios == null) {
      return new LinkedList<>();
    }

    return destinatarios.stream()
        .map(destinatario -> Recipient.builder().name(destinatario).address(destinatario).build())
        .collect(Collectors.toList());
  }

}
