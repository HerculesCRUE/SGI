package org.crue.hercules.sgi.eti.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.com.EmailOutput;
import org.crue.hercules.sgi.eti.dto.com.EtiComActaFinalizarActaData;
import org.crue.hercules.sgi.eti.dto.com.EtiComAsignacionEvaluacionData;
import org.crue.hercules.sgi.eti.dto.com.EtiComAvisoRetrospectivaData;
import org.crue.hercules.sgi.eti.dto.com.EtiComDictamenEvaluacionRevMinData;
import org.crue.hercules.sgi.eti.dto.com.EtiComDictamenEvaluacionSeguimientoRevMinData;
import org.crue.hercules.sgi.eti.dto.com.EtiComEvaluacionModificadaData;
import org.crue.hercules.sgi.eti.dto.com.EtiComInformeSegAnualPendienteData;
import org.crue.hercules.sgi.eti.dto.com.EtiComInformeSegFinalPendienteData;
import org.crue.hercules.sgi.eti.dto.com.EtiComMemoriaIndicarSubsanacionData;
import org.crue.hercules.sgi.eti.dto.com.EtiComMemoriaRevisionMinArchivadaData;
import org.crue.hercules.sgi.eti.dto.com.EtiComRevisionActaData;
import org.crue.hercules.sgi.eti.dto.com.Recipient;
import org.crue.hercules.sgi.eti.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Asistentes;
import org.crue.hercules.sgi.eti.model.ComiteNombre;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.EstadoMemoriaComentario;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionTitulo;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValue;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

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
        .findAllByComiteId(convocatoriaReunion.getComite().getId());

    List<String> idsPersonaRef = evaluadoresConvocatoriaReunion.stream().map(Evaluador::getPersonaRef)
        .collect(Collectors.toList());

    if (idsPersonaRef != null) {
      List<PersonaOutput> personas = personasService.findAllByIdIn(idsPersonaRef);
      List<Recipient> recipients = new ArrayList<>();
      personas.stream().forEach(persona -> recipients.addAll(persona.getEmails().stream()
          .map(email -> Recipient.builder().name(email.getEmail()).address(email.getEmail()).build())
          .collect(Collectors.toList())));

      EmailOutput emailOutput = emailService
          .createComunicadoConvocatoriaReunionEti(convocatoriaReunion, recipients);

      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoConvocatoriaReunionEti(ConvocatoriaReunion convocatoriaReunion) - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoConvocatoriaReunionEti(ConvocatoriaReunion convocatoriaReunion) - end");
  }

  /**
   * Envia el comunicado {@code ETI_COM_ACTA_SIN_REV_MINIMA} para el {@link Acta}
   * al {@code solicitanteRef}
   * 
   * @param comiteNombre              nombre del comite
   * @param comiteCodigo              codigo del comite
   * @param referenciaMemoria         referencia de la memoria
   * @param tipoActividad             {@link TipoActividad}
   * @param tituloSolicitudEvaluacion titulo de la {@link PeticionEvaluacion}
   * @param solicitanteRef            solicitante de la {@link PeticionEvaluacion}
   * @throws JsonProcessingException
   */
  public void enviarComunicadoActaEvaluacionFinalizada(
      Collection<ComiteNombre> comiteNombre,
      String comiteCodigo,
      String referenciaMemoria,
      List<I18nFieldValue> tipoActividad,
      Collection<PeticionEvaluacionTitulo> tituloSolicitudEvaluacion,
      String solicitanteRef)
      throws JsonProcessingException {
    log.debug(
        "enviarComunicadoActaEvaluacionFinalizada(comiteNombre: {}, comiteCodigo: {}, referenciaMemoria: {}, tipoActividad: {}, tituloSolicitudEvaluacion: {}, solicitanteRef: {}) - start",
        comiteNombre,
        comiteCodigo,
        referenciaMemoria,
        tipoActividad,
        tituloSolicitudEvaluacion,
        solicitanteRef);

    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
    if (CollectionUtils.isEmpty(recipients)) {
      log.debug(
          "enviarComunicadoActaEvaluacionFinalizada(comiteNombre: {}, comiteCodigo: {}, referenciaMemoria: {}, tipoActividad: {}, tituloSolicitudEvaluacion: {}, solicitanteRef: {}) - No se puede enviar el comunicado, no existe ninguna persona asociada",
          comiteNombre,
          comiteCodigo,
          referenciaMemoria,
          tipoActividad,
          tituloSolicitudEvaluacion,
          solicitanteRef);
      return;
    }

    EmailOutput emailOutput = emailService.createComunicadoActaFinalizada(
        EtiComActaFinalizarActaData.builder()
            .nombreInvestigacion(comiteNombre)
            .generoComite(I18nHelper.getFieldValueForCurrentLanguage(comiteNombre)
                .map(ComiteNombre::getGenero).map(Enum::toString).orElse(null))
            .referenciaMemoria(referenciaMemoria)
            .tipoActividad(tipoActividad)
            .tituloSolicitudEvaluacion(tituloSolicitudEvaluacion)
            .comiteCodigo(comiteCodigo)
            .enlaceAplicacion(sgiConfigProperties.getWebUrl())
            .build(),
        recipients);
    emailService.sendEmail(emailOutput.getId());

    log.debug(
        "enviarComunicadoActaEvaluacionFinalizada(comiteNombre: {}, comiteCodigo: {}, referenciaMemoria: {}, tipoActividad: {}, tituloSolicitudEvaluacion: {}, solicitanteRef: {}) - end",
        comiteNombre,
        comiteCodigo,
        referenciaMemoria,
        tipoActividad,
        tituloSolicitudEvaluacion,
        solicitanteRef);
  }

  /**
   * Envia el comunicado {@code ETI_COM_DICT_EVA_REV_MINIMA} para el {@link Acta}
   * al {@code solicitanteRef}
   * 
   * @param comiteNombre              nombre del comite
   * @param comiteCodigo              codigo del comite
   * @param referenciaMemoria         referencia de la memoria
   * @param tipoActividad             {@link TipoActividad}
   * @param tituloSolicitudEvaluacion titulo de la {@link PeticionEvaluacion}
   * @param solicitanteRef            solicitante de la {@link PeticionEvaluacion}
   * @throws JsonProcessingException
   */
  public void enviarComunicadoDictamenEvaluacionRevMinima(
      Collection<ComiteNombre> comiteNombre,
      String comiteCodigo,
      String referenciaMemoria,
      List<I18nFieldValue> tipoActividad,
      Collection<PeticionEvaluacionTitulo> tituloSolicitudEvaluacion,
      String solicitanteRef)
      throws JsonProcessingException {
    log.debug(
        "enviarComunicadoDictamenEvaluacionRevMinima(String nombreInvestigacion, String generoComite, String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String enlaceAplicacion, String solicitanteRef) - start");
    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
    String enlaceAplicacion = sgiConfigProperties.getWebUrl();
    if (recipients != null) {
      EmailOutput emailOutput = emailService.createComunicadoEvaluacionMemoriaRevMin(
          EtiComDictamenEvaluacionRevMinData.builder()
              .nombreInvestigacion(comiteNombre)
              .generoComite(I18nHelper.getFieldValueForCurrentLanguage(comiteNombre)
                  .map(ComiteNombre::getGenero).map(Enum::toString).orElse(null))
              .comiteCodigo(comiteCodigo)
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

  public void enviarComunicadoDictamenEvaluacionSeguimientoRevMinima(Collection<ComiteNombre> comiteNombre,
      String comiteCodigo, String referenciaMemoria, List<I18nFieldValue> tipoActividad,
      Collection<PeticionEvaluacionTitulo> tituloSolicitudEvaluacion, String solicitanteRef)
      throws JsonProcessingException {
    log.debug(
        "enviarComunicadoDictamenEvaluacionSeguimientoRevMinima(String nombreInvestigacion, String generoComite, String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String enlaceAplicacion, String solicitanteRef) - start");
    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
    String enlaceAplicacion = sgiConfigProperties.getWebUrl();
    if (recipients != null) {
      EmailOutput emailOutput = emailService.createComunicadoEvaluacionSeguimientoMemoriaRevMin(
          EtiComDictamenEvaluacionSeguimientoRevMinData.builder()
              .nombreInvestigacion(comiteNombre)
              .comiteCodigo(comiteCodigo)
              .generoComite(I18nHelper.getFieldValueForCurrentLanguage(comiteNombre)
                  .map(ComiteNombre::getGenero).map(Enum::toString).orElse(
                      null))
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

  /**
   * Envia el comunicado {@code ETI_COM_INF_RETRO_PENDIENTE} correspondiente a la
   * memoria {@code referenciaMemoria} al {@code solicitanteRef}
   * 
   * @param comiteNombre              nombre del comite
   * @param comiteCodigo              codigo del comite
   * @param referenciaMemoria         referencia de la memoria
   * @param tipoActividad             {@link TipoActividad}
   * @param tituloSolicitudEvaluacion titulo de la {@link PeticionEvaluacion}
   * @param solicitanteRef            solicitante de la {@link PeticionEvaluacion}
   * @throws JsonProcessingException
   */
  public void enviarComunicadoInformeRetrospectivaCeeaPendiente(
      Collection<ComiteNombre> comiteNombre,
      String comiteCodigo,
      String referenciaMemoria,
      List<I18nFieldValue> tipoActividad,
      Collection<PeticionEvaluacionTitulo> tituloSolicitudEvaluacion,
      String solicitanteRef)
      throws JsonProcessingException {
    log.debug(
        "enviarComunicadoInformeRetrospectivaCeeaPendiente(comiteNombre: {}, comiteCodigo: {}, referenciaMemoria: {}, tipoActividad: {}, tituloSolicitudEvaluacion: {}, solicitanteRef: {}) - start",
        comiteNombre,
        comiteCodigo,
        referenciaMemoria,
        tipoActividad,
        tituloSolicitudEvaluacion,
        solicitanteRef);

    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
    if (CollectionUtils.isEmpty(recipients)) {
      log.debug(
          "enviarComunicadoInformeRetrospectivaCeeaPendiente(comiteNombre: {}, comiteCodigo: {}, referenciaMemoria: {}, tipoActividad: {}, tituloSolicitudEvaluacion: {}, solicitanteRef: {}) - end - No se puede enviar el comunicado, no existe ninguna persona asociada",
          comiteNombre,
          comiteCodigo,
          referenciaMemoria,
          tipoActividad,
          tituloSolicitudEvaluacion,
          solicitanteRef);
      return;
    }

    EmailOutput emailOutput = emailService.createComunicadoAvisoRetrospectiva(
        EtiComAvisoRetrospectivaData.builder()
            .nombreInvestigacion(comiteNombre)
            .comiteCodigo(comiteCodigo)
            .generoComite(I18nHelper.getFieldValueForCurrentLanguage(comiteNombre)
                .map(ComiteNombre::getGenero).map(Enum::toString).orElse(null))
            .referenciaMemoria(referenciaMemoria)
            .tipoActividad(tipoActividad)
            .tituloSolicitudEvaluacion(tituloSolicitudEvaluacion)
            .enlaceAplicacion(sgiConfigProperties.getWebUrl())
            .build(),
        recipients);
    emailService.sendEmail(emailOutput.getId());

    log.debug(
        "enviarComunicadoInformeRetrospectivaCeeaPendiente(comiteNombre: {}, comiteCodigo: {}, referenciaMemoria: {}, tipoActividad: {}, tituloSolicitudEvaluacion: {}, solicitanteRef: {}) - end",
        comiteNombre,
        comiteCodigo,
        referenciaMemoria,
        tipoActividad,
        tituloSolicitudEvaluacion,
        solicitanteRef);
  }

  public void enviarComunicadoCambiosEvaluacionEti(String evaluador1Ref, String evaluador2Ref,
      Collection<ComiteNombre> comiteNombre, String referenciaMemoria,
      Collection<PeticionEvaluacionTitulo> tituloSolicitudEvaluacion) throws JsonProcessingException {
    log.debug("enviarComunicadoCambiosEvaluacionEti(Evaluacion evaluacion) - start");

    List<String> idsPersonaRef = new ArrayList<>();
    idsPersonaRef.add(evaluador1Ref);
    idsPersonaRef.add(evaluador2Ref);

    if (idsPersonaRef == null) {
      log.debug(
          "enviarComunicadoCambiosEvaluacionEti(Evaluacion evaluacion) - No se puede enviar el comunicado, no existe ninguna persona asociada");
      return;
    }
    List<Recipient> recipients = new ArrayList<>();
    personasService.findAllByIdIn(idsPersonaRef).stream()
        .forEach(persona -> recipients.addAll(persona.getEmails().stream()
            .map(email -> Recipient.builder().name(email.getEmail()).address(email.getEmail()).build())
            .collect(Collectors.toList())));

    if (!recipients.isEmpty()) {
      EmailOutput emailOutput = emailService.createComunicadoCambiosEvaluacion(
          EtiComEvaluacionModificadaData.builder()
              .nombreInvestigacion(comiteNombre)
              .referenciaMemoria(referenciaMemoria)
              .tituloSolicitudEvaluacion(tituloSolicitudEvaluacion)
              .build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoCambiosEvaluacionEti(Evaluacion evaluacion) - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
  }

  /**
   * Envia el comunicado {@code ETI_COM_INF_SEG_ANU} correspondiente a la
   * memoria {@code referenciaMemoria} al {@code solicitanteRef}
   * 
   * @param comiteNombre              nombre del comite
   * @param referenciaMemoria         referencia de la memoria
   * @param tipoActividad             {@link TipoActividad}
   * @param tituloSolicitudEvaluacion titulo de la {@link PeticionEvaluacion}
   * @param solicitanteRef            solicitante de la {@link PeticionEvaluacion}
   * @throws JsonProcessingException
   */
  public void enviarComunicadoInformeSeguimientoAnual(
      Collection<ComiteNombre> comiteNombre,
      String referenciaMemoria,
      List<I18nFieldValue> tipoActividad,
      Collection<PeticionEvaluacionTitulo> tituloSolicitudEvaluacion,
      String solicitanteRef)
      throws JsonProcessingException {
    log.debug(
        "enviarComunicadoInformeSeguimientoAnual(comiteNombre: {}, referenciaMemoria: {}, tipoActividad: {}, tituloSolicitudEvaluacion: {}, solicitanteRef: {}) - start",
        comiteNombre,
        referenciaMemoria,
        tipoActividad,
        tituloSolicitudEvaluacion,
        solicitanteRef);

    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
    if (CollectionUtils.isEmpty(recipients)) {
      log.debug(
          "enviarComunicadoInformeSeguimientoAnual(comiteNombre: {}, referenciaMemoria: {}, tipoActividad: {}, tituloSolicitudEvaluacion: {}, solicitanteRef: {}) - end - No se puede enviar el comunicado, no existe ninguna persona asociada",
          comiteNombre,
          referenciaMemoria,
          tipoActividad,
          tituloSolicitudEvaluacion,
          solicitanteRef);
      return;
    }

    EmailOutput emailOutput = emailService.createComunicadoInformeSeguimientoAnualPendiente(
        EtiComInformeSegAnualPendienteData.builder()
            .nombreInvestigacion(comiteNombre)
            .referenciaMemoria(referenciaMemoria)
            .tipoActividad(tipoActividad)
            .tituloSolicitudEvaluacion(tituloSolicitudEvaluacion)
            .enlaceAplicacion(sgiConfigProperties.getWebUrl())
            .build(),
        recipients);
    emailService.sendEmail(emailOutput.getId());

    log.debug(
        "enviarComunicadoInformeSeguimientoAnual(comiteNombre: {}, referenciaMemoria: {}, tipoActividad: {}, tituloSolicitudEvaluacion: {}, solicitanteRef: {}) - end",
        comiteNombre,
        referenciaMemoria,
        tipoActividad,
        tituloSolicitudEvaluacion,
        solicitanteRef);
  }

  public void enviarComunicadoInformeSeguimientoFinal(Collection<ComiteNombre> comiteNombre, String comiteCodigo,
      String referenciaMemoria,
      List<I18nFieldValue> tipoActividad, Collection<PeticionEvaluacionTitulo> tituloSolicitudEvaluacion,
      String solicitanteRef)
      throws JsonProcessingException {
    log.debug("enviarComunicadoInformeSeguimientoFinal() - start");

    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
    String enlaceAplicacion = sgiConfigProperties.getWebUrl();
    if (recipients != null) {
      EmailOutput emailOutput = emailService.createComunicadoInformeSeguimientoFinalPendiente(
          EtiComInformeSegFinalPendienteData.builder()
              .nombreInvestigacion(comiteNombre)
              .comiteCodigo(comiteCodigo)
              .referenciaMemoria(referenciaMemoria)
              .tipoActividad(tipoActividad)
              .tituloSolicitudEvaluacion(tituloSolicitudEvaluacion)
              .enlaceAplicacion(enlaceAplicacion).build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoInformeSeguimientoFinal() - end - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoInformeSeguimientoFinal() - end");
  }

  public void enviarComunicadoMemoriaRevisionMinimaArchivada(Collection<ComiteNombre> comiteNombre,
      String comiteCodigo, String referenciaMemoria, List<I18nFieldValue> tipoActividad,
      Collection<PeticionEvaluacionTitulo> tituloSolicitudEvaluacion, String solicitanteRef)
      throws JsonProcessingException {
    log.debug("enviarComunicadoMemoriaRevisionMinimaArchivada() - start");

    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
    if (recipients != null) {
      EmailOutput emailOutput = emailService.createComunicadoMemoriaRevisionMinArchivada(
          EtiComMemoriaRevisionMinArchivadaData.builder()
              .nombreInvestigacion(comiteNombre)
              .comiteCodigo(comiteCodigo)
              .referenciaMemoria(referenciaMemoria)
              .tipoActividad(tipoActividad)
              .tituloSolicitudEvaluacion(tituloSolicitudEvaluacion)
              .build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoMemoriaRevisionMinimaArchivada() - end - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoMemoriaRevisionMinimaArchivada() - end");
  }

  public void enviarComunicadoMemoriaArchivadaAutomaticamentePorInactividad(Collection<ComiteNombre> comiteNombre,
      String comiteCodigo, String referenciaMemoria, List<I18nFieldValue> tipoActividad,
      Collection<PeticionEvaluacionTitulo> tituloSolicitudEvaluacion, String solicitanteRef)
      throws JsonProcessingException {

    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
    String enlaceAplicacion = sgiConfigProperties.getWebUrl();
    if (recipients != null) {
      EmailOutput emailOutput = emailService.createComunicadoMemoriaArchivadaPorInactividad(
          EtiComInformeSegFinalPendienteData.builder()
              .nombreInvestigacion(comiteNombre)
              .comiteCodigo(comiteCodigo)
              .referenciaMemoria(referenciaMemoria)
              .tipoActividad(tipoActividad)
              .tituloSolicitudEvaluacion(tituloSolicitudEvaluacion)
              .enlaceAplicacion(enlaceAplicacion).build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoMemoriaRevisionMinimaArchivada() - end - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoMemoriaArchivadaAutomaticamentePorInactividad() - end");
  }

  public void enviarComunicadoIndicarSubsanacion(Collection<ComiteNombre> comiteNombre,
      String comiteCodigo,
      Collection<EstadoMemoriaComentario> comentarioEstado,
      String referenciaMemoria, List<I18nFieldValue> tipoActividad,
      Collection<PeticionEvaluacionTitulo> tituloSolicitudEvaluacion,
      String solicitanteRef)
      throws JsonProcessingException {
    log.debug(
        "enviarComunicadoIndicarSubsanacion(String nombreInvestigacion, String comentarioEstado, String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String enlaceAplicacion, String solicitanteRef) - start");
    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
    String enlaceAplicacion = sgiConfigProperties.getWebUrl();
    if (recipients != null) {
      EmailOutput emailOutput = emailService.createComunicadoMemoriaIndicarSubsanacion(
          EtiComMemoriaIndicarSubsanacionData.builder()
              .enlaceAplicacion(enlaceAplicacion)
              .comentarioEstado(comentarioEstado)
              .nombreInvestigacion(comiteNombre)
              .comiteCodigo(comiteCodigo)
              .referenciaMemoria(referenciaMemoria)
              .tipoActividad(tipoActividad)
              .tituloSolicitudEvaluacion(tituloSolicitudEvaluacion)
              .build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoIndicarSubsanacion(String nombreInvestigacion, String comentarioEstado, String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String enlaceAplicacion, String solicitanteRef) - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug(
        "enviarComunicadoIndicarSubsanacion(String nombreInvestigacion, String comentarioEstado, String referenciaMemoria, String tipoActividad, String tituloSolicitudEvaluacion, String enlaceAplicacion, String solicitanteRef) - end");
  }

  public void enviarComunicadoAsignacionEvaluacion(Evaluacion evaluacion, Instant fechaEvaluacionAnterior)
      throws JsonProcessingException {
    log.debug(
        "enviarComunicadoAsignacionEvaluacion(Evaluacion evaluacion, Instant fechaEvaluacionAnterior) - start");
    List<Recipient> recipients = new ArrayList<>();

    PersonaOutput evaluador1 = null;
    PersonaOutput evaluador2 = null;
    try {
      evaluador1 = personasService.findById(evaluacion.getEvaluador1().getPersonaRef());
      recipients.addAll(getRecipientsFromPersona(evaluador1));

      evaluador2 = personasService.findById(evaluacion.getEvaluador2().getPersonaRef());
      recipients.addAll(getRecipientsFromPersona(evaluador2));
    } catch (Exception e) {
      log.error(
          "enviarComunicadoAsignacionEvaluacion(Evaluacion evaluacion, Instant fechaEvaluacionAnterior) - No se puede enviar el comunicado, no se pueden resolver los nombers de los evaluadores");
    }

    if (!CollectionUtils.isEmpty(recipients) && ObjectUtils.isNotEmpty(evaluador1)
        && ObjectUtils.isNotEmpty(evaluador2)) {
      EmailOutput emailOutput = emailService.createComunicadoAsignacionEvaluacion(
          EtiComAsignacionEvaluacionData.builder()
              .fechaConvocatoriaReunion(evaluacion.getConvocatoriaReunion().getFechaEvaluacion())
              .referenciaMemoria(evaluacion.getMemoria().getNumReferencia())
              .tituloSolicitudEvaluacion(evaluacion.getMemoria().getPeticionEvaluacion().getTitulo())
              .nombreApellidosEvaluador1(evaluador1.getNombre() + " " + evaluador1.getApellidos())
              .nombreApellidosEvaluador2(evaluador2.getNombre() + " " + evaluador2.getApellidos())
              .nombreInvestigacion(evaluacion.getConvocatoriaReunion().getComite().getNombre())
              .generoComite(I18nHelper.getFieldValueForCurrentLanguage(evaluacion.getMemoria().getComite().getNombre())
                  .map(ComiteNombre::getGenero).map(Enum::toString).orElse(null))
              .fechaEvaluacionAnterior(fechaEvaluacionAnterior)
              .enlaceAplicacion(sgiConfigProperties.getWebUrl())
              .build(),
          recipients);
      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoAsignacionEvaluacion(Evaluacion evaluacion, Instant fechaEvaluacionAnterior) - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug(
        "enviarComunicadoAsignacionEvaluacion(Evaluacion evaluacion, Instant fechaEvaluacionAnterior) - end");
  }

  /**
   * Envia el comunicado {@code ETI_COM_REVISION_ACTA} para el {@link Acta} a la
   * lista de {@code asistentes}
   * 
   * @param acta       {@link Acta} para la que se envia el comunicado
   * @param asistentes La lista de {@link Asistentes} a los que se les envia el
   *                   comunicado
   * @throws JsonProcessingException
   */
  public void enviarComunicadoRevisionActa(Acta acta, List<Asistentes> asistentes)
      throws JsonProcessingException {

    log.debug("enviarComunicadoRevisionActa(acta: {}, asistentes: {}) - start", acta, asistentes);
    List<Recipient> recipients = new ArrayList<>();

    asistentes
        .forEach(asistente -> recipients.addAll(getRecipientsFromPersonaRef(asistente.getEvaluador().getPersonaRef())));

    if (CollectionUtils.isEmpty(recipients)) {
      log.debug(
          "enviarComunicadoRevisionActa(acta: {}, asistentes: {}) - No se puede enviar el comunicado, no existe ninguna persona asociada",
          acta, asistentes);
      return;
    }

    EmailOutput emailOutput = emailService.createComunicadoRevisionActa(
        EtiComRevisionActaData.builder()
            .enlaceAplicacion(sgiConfigProperties.getWebUrl())
            .nombreInvestigacion(acta.getConvocatoriaReunion().getComite().getNombre())
            .nombreComite(acta.getConvocatoriaReunion().getComite().getCodigo())
            .fechaEvaluacion(acta.getConvocatoriaReunion().getFechaEvaluacion())
            .generoComite(
                I18nHelper.getFieldValueForCurrentLanguage(acta.getConvocatoriaReunion().getComite().getNombre())
                    .map(ComiteNombre::getGenero).map(Enum::name).orElse(null))
            .build(),
        recipients);
    emailService.sendEmail(emailOutput.getId());

    log.debug(
        "enviarComunicadoRevisionActa(acta: {}, asistentes: {}) - end", acta, asistentes);
  }

  /**
   * Obtiene los emails de la personaRef recibida
   * 
   * @param personaRef id de la persona
   * @return lista @link{Recipient}
   */
  private List<Recipient> getRecipientsFromPersonaRef(String personaRef) {
    List<Recipient> recipients = new ArrayList<>();
    PersonaOutput persona = personasService.findById(personaRef);
    if (persona != null) {
      recipients = getRecipientsFromPersona(persona);
    }
    return recipients;
  }

  /**
   * Obtiene los emails de la persona recibida
   * 
   * @param persona la persona
   * @return lista @link{Recipient}
   */
  private List<Recipient> getRecipientsFromPersona(PersonaOutput persona) {
    List<Recipient> recipients = new ArrayList<>();
    if (persona != null) {
      recipients = persona.getEmails().stream()
          .map(email -> Recipient.builder().name(email.getEmail()).address(email.getEmail()).build())
          .collect(Collectors.toList());
    }
    return recipients;
  }

}
