package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.com.EmailOutput;
import org.crue.hercules.sgi.eti.dto.com.EtiComActaFinalizarActaData;
import org.crue.hercules.sgi.eti.dto.com.EtiComAvisoRetrospectivaData;
import org.crue.hercules.sgi.eti.dto.com.EtiComDictamenEvaluacionRevMinData;
import org.crue.hercules.sgi.eti.dto.com.EtiComEvaluacionModificadaData;
import org.crue.hercules.sgi.eti.dto.com.EtiComInformeSegAnualPendienteData;
import org.crue.hercules.sgi.eti.dto.com.Recipient;
import org.crue.hercules.sgi.eti.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ComunicadosService {
  private final SgiApiComService emailService;
  private final SgiApiSgpService personasService;
  private final EvaluadorService evaluadorService;
  private final SgiConfigProperties sgiConfigProperties;

  public ComunicadosService(SgiApiComService emailService, SgiApiSgpService personasService,
      EvaluadorService evaluadorService, SgiConfigProperties sgiConfigProperties) {
    this.emailService = emailService;
    this.personasService = personasService;
    this.evaluadorService = evaluadorService;
    this.sgiConfigProperties = sgiConfigProperties;
  }

  public void enviarComunicadoConvocatoriaReunionEti(ConvocatoriaReunion convocatoriaReunion)
      throws JsonProcessingException {
    log.debug("enviarComunicadoConvocatoriaReunionEti(ConvocatoriaReunion convocatoriaReunion) - start");
    List<Evaluador> evaluadoresConvocatoriaReunion = evaluadorService
        .findAllByComite(convocatoriaReunion.getComite().getComite());

    List<String> idsPersonaRef = evaluadoresConvocatoriaReunion.stream().map(evaluador -> evaluador.getPersonaRef())
        .collect(Collectors.toList());

    if (idsPersonaRef != null) {
      List<PersonaOutput> personas = personasService.findAllByIdIn(idsPersonaRef);
      List<Recipient> recipients = new ArrayList<Recipient>();
      personas.stream().forEach(persona -> {
        recipients.addAll(persona.getEmails().stream()
            .map(email -> Recipient.builder().name(email.getEmail()).address(email.getEmail()).build())
            .collect(Collectors.toList()));
      });

      EmailOutput emailOutput = emailService
          .createComunicadoConvocatoriaReunionEti(convocatoriaReunion, recipients);

      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoConvocatoriaReunionEti(ConvocatoriaReunion convocatoriaReunion) - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoConvocatoriaReunionEti(ConvocatoriaReunion convocatoriaReunion) - end");
  }

  public void enviarComunicadoActaEvaluacionFinalizada(String nombreInvestigacion, String generoComite,
      String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String solicitanteRef)
      throws JsonProcessingException {
    log.debug(
        "enviarComunicadoActaEvaluacionFinalizada(String nombreInvestigacion, String generoComite, String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String enlaceAplicacion, String solicitanteRef) - start");
    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
    String enlaceAplicacion = sgiConfigProperties.getWebUrl();
    if (recipients != null) {
      EmailOutput emailOutput = emailService.createComunicadoActaFinalizada(
          EtiComActaFinalizarActaData.builder()
              .nombreInvestigacion(nombreInvestigacion)
              .generoComite(generoComite)
              .referenciaMemoria(referenciaMemoria)
              .tipoActividad(tipoActividad)
              .tituloSolicitudEvaluacion(tituloSolicitudEvaluacion)
              .enlaceAplicacion(enlaceAplicacion).build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoActaEvaluacionFinalizada(String nombreInvestigacion, String generoComite, String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String enlaceAplicacion, String solicitanteRef) - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug(
        "enviarComunicadoActaEvaluacionFinalizada(String nombreInvestigacion, String generoComite, String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String enlaceAplicacion, String solicitanteRef) - end");
  }

  public void enviarComunicadoDictamenEvaluacionRevMinima(String nombreInvestigacion, String generoComite,
      String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String solicitanteRef)
      throws JsonProcessingException {
    log.debug(
        "enviarComunicadoDictamenEvaluacionRevMinima(String nombreInvestigacion, String generoComite, String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String enlaceAplicacion, String solicitanteRef) - start");
    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
    String enlaceAplicacion = sgiConfigProperties.getWebUrl();
    if (recipients != null) {
      EmailOutput emailOutput = emailService.createComunicadoEvaluacionMemoriaRevMin(
          EtiComDictamenEvaluacionRevMinData.builder()
              .nombreInvestigacion(nombreInvestigacion)
              .generoComite(generoComite)
              .referenciaMemoria(referenciaMemoria)
              .tipoActividad(tipoActividad)
              .tituloSolicitudEvaluacion(tituloSolicitudEvaluacion)
              .enlaceAplicacion(enlaceAplicacion).build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoDictamenEvaluacionRevMinima(String nombreInvestigacion, String generoComite, String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String enlaceAplicacion, String solicitanteRef) - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug(
        "enviarComunicadoDictamenEvaluacionRevMinima(String nombreInvestigacion, String generoComite, String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String enlaceAplicacion, String solicitanteRef) - end");
  }

  public void enviarComunicadoDictamenEvaluacionSeguimientoRevMinima(String nombreInvestigacion, String generoComite,
      String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String solicitanteRef)
      throws JsonProcessingException {
    log.debug(
        "enviarComunicadoDictamenEvaluacionSeguimientoRevMinima(String nombreInvestigacion, String generoComite, String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String enlaceAplicacion, String solicitanteRef) - start");
    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
    String enlaceAplicacion = sgiConfigProperties.getWebUrl();
    if (recipients != null) {
      EmailOutput emailOutput = emailService.createComunicadoEvaluacionMemoriaRevMin(
          EtiComDictamenEvaluacionRevMinData.builder()
              .nombreInvestigacion(nombreInvestigacion)
              .generoComite(generoComite)
              .referenciaMemoria(referenciaMemoria)
              .tipoActividad(tipoActividad)
              .tituloSolicitudEvaluacion(tituloSolicitudEvaluacion)
              .enlaceAplicacion(enlaceAplicacion).build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoDictamenEvaluacionSeguimientoRevMinima(String nombreInvestigacion, String generoComite, String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String enlaceAplicacion, String solicitanteRef) - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug(
        "enviarComunicadoDictamenEvaluacionSeguimientoRevMinima(String nombreInvestigacion, String generoComite, String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String enlaceAplicacion, String solicitanteRef) - end");
  }

  public void enviarComunicadoInformeRetrospectivaCeeaPendiente(String nombreInvestigacion, String generoComite,
      String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String codigoOrganoCompetente,
      String solicitanteRef)
      throws JsonProcessingException {
    log.debug("enviarComunicadoInformeRetrospectivaCeeaPendiente() - start");

    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
    String enlaceAplicacion = sgiConfigProperties.getWebUrl();
    if (recipients != null) {
      EmailOutput emailOutput = emailService.createComunicadoAvisoRetrospectiva(
          EtiComAvisoRetrospectivaData.builder()
              .nombreInvestigacion(nombreInvestigacion)
              .generoComite(generoComite)
              .referenciaMemoria(referenciaMemoria)
              .tipoActividad(tipoActividad)
              .tituloSolicitudEvaluacion(tituloSolicitudEvaluacion)
              .enlaceAplicacion(enlaceAplicacion)
              .codigoOrganoCompetente(codigoOrganoCompetente).build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoInformeRetrospectivaCeeaPendiente() - end - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoInformeRetrospectivaCeeaPendiente() - end");
  }

  public void enviarComunicadoCambiosEvaluacionEti(String comite, String nombreInvestigacion, String referenciaMemoria,
      String tituloSolicitudEvaluacion) throws JsonProcessingException {
    log.debug("enviarComunicadoCambiosEvaluacionEti(Evaluacion evaluacion) - start");
    List<Evaluador> evaluadoresMemoria = evaluadorService
        .findAllByComite(comite);

    List<String> idsPersonaRef = evaluadoresMemoria.stream().map(evaluador -> evaluador.getPersonaRef())
        .collect(Collectors.toList());

    if (idsPersonaRef != null) {
      List<PersonaOutput> personas = personasService.findAllByIdIn(idsPersonaRef);
      List<Recipient> recipients = new ArrayList<Recipient>();
      personas.stream().forEach(persona -> {
        recipients.addAll(persona.getEmails().stream()
            .map(email -> Recipient.builder().name(email.getEmail()).address(email.getEmail()).build())
            .collect(Collectors.toList()));
      });

      if (recipients != null) {
        EmailOutput emailOutput = emailService.createComunicadoCambiosEvaluacion(
            EtiComEvaluacionModificadaData.builder()
                .nombreInvestigacion(nombreInvestigacion)
                .referenciaMemoria(referenciaMemoria)
                .tituloSolicitudEvaluacion(tituloSolicitudEvaluacion)
                .build(),
            recipients);
        emailService.sendEmail(emailOutput.getId());
      } else {
        log.debug(
            "enviarComunicadoCambiosEvaluacionEti(Evaluacion evaluacion) - No se puede enviar el comunicado, no existe ninguna persona asociada");
      }
    } else {
      log.debug(
          "enviarComunicadoCambiosEvaluacionEti(Evaluacion evaluacion) - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
  }

  public void enviarComunicadoInformeSeguimientoAnual(String nombreInvestigacion, String referenciaMemoria,
      String tipoActividad, String tituloSolicitudEvaluacion, String solicitanteRef)
      throws JsonProcessingException {
    log.debug("enviarComunicadoInformeSeguimientoAnual() - start");

    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
    String enlaceAplicacion = sgiConfigProperties.getWebUrl();
    if (recipients != null) {
      EmailOutput emailOutput = emailService.createComunicadoInformeSeguimientoAnualPendiente(
          EtiComInformeSegAnualPendienteData.builder()
              .nombreInvestigacion(nombreInvestigacion)
              .referenciaMemoria(referenciaMemoria)
              .tipoActividad(tipoActividad)
              .tituloSolicitudEvaluacion(tituloSolicitudEvaluacion)
              .enlaceAplicacion(enlaceAplicacion).build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoInformeSeguimientoAnual() - end - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoInformeSeguimientoAnual() - end");
  }

  /**
   * Obtiene los emails de la personaRef recibida
   * 
   * @param personaRef id del proyecto
   * @return lista @link{Recipient}
   */
  private List<Recipient> getRecipientsFromPersonaRef(String personaRef) {
    List<Recipient> recipients = new ArrayList<>();
    PersonaOutput persona = personasService.findById(personaRef);
    if (persona != null) {
      recipients = persona.getEmails().stream()
          .map(email -> Recipient.builder().name(email.getEmail()).address(email.getEmail()).build())
          .collect(Collectors.toList());
    }
    return recipients;
  }
}
