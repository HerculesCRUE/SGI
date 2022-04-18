package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.commons.collections4.CollectionUtils;
import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.com.CspComInicioPresentacionGastoData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoAlegacionesData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoDefinitivoData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoProvisionalData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoSolicitadaData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudPeticionEvaluacionData;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Enlace;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ComunicadosService {
  private static final String CONFIG_CSP_COM_INICIO_PRESENTACION_JUSTIFICACION_GASTO_DESTINATARIOS = "csp-com-inicio-presentacion-gasto-destinatarios-";
  private static final String CONFIG_CSP_COM_SOL_CAMB_EST_SOLICITADA = "csp-com-sol-camb-est-solicitada-destinatarios-";
  private static final String CONFIG_CSP_COM_SOL_CAMB_EST_ALEGACIONES = "csp-com-sol-camb-est-alegaciones-destinatarios-";

  private final SgiConfigProperties sgiConfigProperties;
  private final ProyectoRepository proyectoRepository;
  private final ProyectoPeriodoJustificacionRepository proyectoPeriodoJustificacionRepository;
  private final ProyectoSeguimientoCientificoComService proyectoSeguimientoCientificoComService;
  private final ProyectoSocioPeriodoPagoComService proyectoSocioPeriodoPagoComService;
  private final SgiApiCnfService configService;
  private final SgiApiComService emailService;
  private final SgiApiSgpService personasService;
  private final ProyectoSocioPeriodoJustificacionComService proyectoSocioPeriodoJustificacionComService;

  public ComunicadosService(
      SgiConfigProperties sgiConfigProperties,
      ProyectoRepository proyectoRepository,
      ProyectoPeriodoJustificacionRepository proyectoPeriodoJustificacionRepository,
      SgiApiCnfService configService, SgiApiComService emailService, SgiApiSgpService personasService,
      ProyectoSeguimientoCientificoComService proyectoSeguimientoCientificoComService,
      ProyectoSocioPeriodoPagoComService proyectoSocioPeriodoPagoComService,
      ProyectoSocioPeriodoJustificacionComService proyectoSocioPeriodoJustificacionComService) {

    this.proyectoSocioPeriodoPagoComService = proyectoSocioPeriodoPagoComService;
    this.sgiConfigProperties = sgiConfigProperties;
    this.proyectoRepository = proyectoRepository;
    this.proyectoPeriodoJustificacionRepository = proyectoPeriodoJustificacionRepository;
    this.proyectoSeguimientoCientificoComService = proyectoSeguimientoCientificoComService;
    this.configService = configService;
    this.emailService = emailService;
    this.personasService = personasService;
    this.proyectoSocioPeriodoJustificacionComService = proyectoSocioPeriodoJustificacionComService;
  }

  public void enviarComunicadoInicioPresentacionJustificacionGastos() throws JsonProcessingException {
    log.debug("enviarComunicadoInicioPresentacionJustificacionGastos() - start");

    ZoneId zoneId = sgiConfigProperties.getTimeZone().toZoneId();
    YearMonth yearMonth = YearMonth.now(zoneId);
    List<ProyectoPeriodoJustificacion> periodos = proyectoPeriodoJustificacionRepository
        .findByFechaInicioPresentacionBetweenAndProyectoActivoTrue(
            yearMonth.atDay(1).atStartOfDay(zoneId)
                .toInstant(),
            yearMonth.atEndOfMonth().atStartOfDay(zoneId).withHour(23).withMinute(59).withSecond(59).toInstant());

    List<Long> proyectoIds = periodos.stream().map(ProyectoPeriodoJustificacion::getProyectoId)
        .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(proyectoIds)) {
      log.info(
          "No existen proyectos que requieran generar aviso de inicio del período de presentación de justificación de gastos");
      return;
    }
    Map<String, List<Proyecto>> proyectosByUnidadGestionRef = getProyectosByUnidadGestion(proyectoIds);

    Map<Long, List<ProyectoPeriodoJustificacion>> periodosByProyecto = this
        .getPeriodosJustificacionGastoByProyecto(periodos);

    for (Map.Entry<String, List<Proyecto>> entry : proyectosByUnidadGestionRef.entrySet()) {

      List<Recipient> recipients = this.getRecipients(entry.getKey(),
          CONFIG_CSP_COM_INICIO_PRESENTACION_JUSTIFICACION_GASTO_DESTINATARIOS);
      List<CspComInicioPresentacionGastoData.Proyecto> proyectosEmail = new ArrayList<>();
      List<Proyecto> proyectosUnidad = proyectosByUnidadGestionRef.get(entry.getKey());
      for (Proyecto proyecto : proyectosUnidad) {
        List<ProyectoPeriodoJustificacion> periodosProyecto = periodosByProyecto.get(proyecto.getId());
        for (ProyectoPeriodoJustificacion periodo : periodosProyecto) {
          CspComInicioPresentacionGastoData.Proyecto proyectoEmail = CspComInicioPresentacionGastoData.Proyecto
              .builder()
              .titulo(proyecto.getTitulo())
              .fechaInicio(periodo.getFechaInicioPresentacion())
              .fechaFin(periodo.getFechaFinPresentacion())
              .build();
          proyectosEmail.add(proyectoEmail);
        }
      }
      buildAndSendEmailForPeriodosJustificacionGasto(recipients, proyectosEmail);
    }
    log.debug("enviarComunicadoInicioPresentacionJustificacionGastos() - end");
  }

  public void enviarComunicadoInicioJustificacionSeguimientoCientificoCommunication() throws JsonProcessingException {
    this.proyectoSeguimientoCientificoComService.sendStartOfJustificacionSeguimientoCientificoCommunication();
  }

  public void enviarComunicadoSolicitudAltaPeticionEvaluacionEti(String codigoPeticionEvaluacion,
      String codigoSolicitud, String solicitanteRef) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudAltaPeticionEvaluacionEti() - start");
    PersonaOutput persona = personasService.findById(solicitanteRef);
    if (persona != null) {
      List<Recipient> recipients = persona.getEmails().stream()
          .map(email -> Recipient.builder().name(email.getEmail()).address(email.getEmail()).build())
          .collect(Collectors.toList());
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

  public void enviarComunicadoJustificacionSeguimientoCientificoIps() {
    this.proyectoSeguimientoCientificoComService.sendCommunicationIPs();
  }

  public void enviarComunicadoVencimientoPeriodoPagoSocio() {
    this.proyectoSocioPeriodoPagoComService.enviarComunicadoVencimientoPeriodoPagoSocio();
  }

  private void buildAndSendEmailForPeriodosJustificacionGasto(List<Recipient> recipients,
      List<CspComInicioPresentacionGastoData.Proyecto> proyectosEmail) throws JsonProcessingException {

    ZoneId zoneId = sgiConfigProperties.getTimeZone().toZoneId();
    YearMonth yearMonth = YearMonth.now(zoneId);

    EmailOutput emailOutput = emailService
        .createComunicadoInicioPresentacionJustificacionGastosEmail(
            CspComInicioPresentacionGastoData.builder().fecha(yearMonth.atDay(1)).proyectos(
                proyectosEmail).build(),
            recipients);
    emailService.sendEmail(emailOutput.getId());
  }

  private List<Recipient> getRecipients(String unidadGestionRef, String template) throws JsonProcessingException {
    List<String> destinatarios = configService
        .findStringListByName(
            template + unidadGestionRef);

    return destinatarios.stream()
        .map(destinatario -> Recipient.builder().name(destinatario).address(destinatario).build())
        .collect(Collectors.toList());
  }

  private Map<Long, List<ProyectoPeriodoJustificacion>> getPeriodosJustificacionGastoByProyecto(
      List<ProyectoPeriodoJustificacion> periodos) {
    Map<Long, List<ProyectoPeriodoJustificacion>> periodosByProyecto = new HashMap<>();
    for (ProyectoPeriodoJustificacion periodo : periodos) {
      Long proyectoId = periodo.getProyectoId();

      List<ProyectoPeriodoJustificacion> periodosProyecto = periodosByProyecto.get(proyectoId);
      if (periodosProyecto == null) {
        periodosProyecto = new LinkedList<>();
      }
      periodosProyecto.add(periodo);
      periodosByProyecto.put(proyectoId, periodosProyecto);
    }
    return periodosByProyecto;
  }

  private Map<String, List<Proyecto>> getProyectosByUnidadGestion(List<Long> proyectoIds) {
    List<Proyecto> proyectos = proyectoRepository.findByIdInAndActivoTrue(proyectoIds);
    Map<String, List<Proyecto>> proyectosByUnidadGestionRef = new HashMap<>();
    for (Proyecto proyecto : proyectos) {
      List<Proyecto> proyectosUnidad = proyectosByUnidadGestionRef.get(proyecto.getUnidadGestionRef());
      if (proyectosUnidad == null) {
        proyectosUnidad = new ArrayList<>();
      }
      proyectosUnidad.add(proyecto);
      proyectosByUnidadGestionRef.put(proyecto.getUnidadGestionRef(), proyectosUnidad);
    }
    return proyectosByUnidadGestionRef;
  }

  public void enviarComunicadoSolicitudCambioEstadoSolicitada(String nombreApellidosSolicitante,
      String unidadGestionRef, String tituloConvocatoria, Instant fechaPublicacionConvocatoria,
      Instant fechaCambioEstadoSolicitud)
      throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoSolicitada() - start");
    List<Recipient> recipients = getRecipients(unidadGestionRef, CONFIG_CSP_COM_SOL_CAMB_EST_SOLICITADA);
    if (!recipients.isEmpty()) {
      EmailOutput emailOutput = emailService.createComunicadoSolicitudCambioEstadoSolicitada(
          CspComSolicitudCambioEstadoSolicitadaData.builder()
              .tituloConvocatoria(tituloConvocatoria)
              .nombreApellidosSolicitante(nombreApellidosSolicitante)
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

  public void enviarComunicadoSolicitudCambioEstadoAlegaciones(String nombreApellidosSolicitante,
      String unidadGestionRef, String tituloConvocatoria, String codigoInterno, Instant fechaCambioEstadoSolicitud,
      Instant fechaProvisionalConvocatoria) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoAlegaciones() - start");
    List<Recipient> recipients = getRecipients(unidadGestionRef, CONFIG_CSP_COM_SOL_CAMB_EST_ALEGACIONES);
    if (!recipients.isEmpty()) {
      EmailOutput emailOutput = emailService.createComunicadoSolicitudCambioEstadoAlegaciones(
          CspComSolicitudCambioEstadoAlegacionesData.builder()
              .tituloConvocatoria(tituloConvocatoria)
              .nombreApellidosSolicitante(nombreApellidosSolicitante)
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

  public void enviarComunicadoSolicitudCambioEstadoExclProv(String solicitanteRef, String tituloConvocatoria,
      Instant fechaProvisionalConvocatoria,
      List<ConvocatoriaEnlace> convocatoriaEnlaces) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoExclProv() - start");
    List<Enlace> enlaces = getListadoEnlacesComunicados(convocatoriaEnlaces);
    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
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

  public void enviarComunicadoSolicitudCambioEstadoExclDef(String solicitanteRef, String tituloConvocatoria,
      Instant fechaConcesionConvocatoria,
      List<ConvocatoriaEnlace> convocatoriaEnlaces) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoExclDef() - start");
    List<Enlace> enlaces = getListadoEnlacesComunicados(convocatoriaEnlaces);
    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
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

  public void enviarComunicadoSolicitudCambioEstadoConcProv(String solicitanteRef, String tituloConvocatoria,
      Instant fechaProvisionalConvocatoria,
      List<ConvocatoriaEnlace> convocatoriaEnlaces) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoConcProv() - start");
    List<Enlace> enlaces = getListadoEnlacesComunicados(convocatoriaEnlaces);
    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
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

  public void enviarComunicadoSolicitudCambioEstadoDenProv(String solicitanteRef, String tituloConvocatoria,
      Instant fechaProvisionalConvocatoria,
      List<ConvocatoriaEnlace> convocatoriaEnlaces) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoDenProv() - start");
    List<Enlace> enlaces = getListadoEnlacesComunicados(convocatoriaEnlaces);
    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
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

  public void enviarComunicadoSolicitudCambioEstadoConc(String solicitanteRef, String tituloConvocatoria,
      Instant fechaConcesionConvocatoria,
      List<ConvocatoriaEnlace> convocatoriaEnlaces) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoConc() - start");
    List<Enlace> enlaces = getListadoEnlacesComunicados(convocatoriaEnlaces);
    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
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

  public void enviarComunicadoSolicitudCambioEstadoDen(String solicitanteRef, String tituloConvocatoria,
      Instant fechaConcesionConvocatoria,
      List<ConvocatoriaEnlace> convocatoriaEnlaces) throws JsonProcessingException {
    log.debug("enviarComunicadoSolicitudCambioEstadoDen() - start");
    List<Enlace> enlaces = getListadoEnlacesComunicados(convocatoriaEnlaces);
    List<Recipient> recipients = getRecipientsFromPersonaRef(solicitanteRef);
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

  public void enviarComunicadosPeriodoJustificacionSocio() {
    this.proyectoSocioPeriodoJustificacionComService.enviarComunicadoInicioFinPeriodoJustificacionSocio();
  }

  private List<Enlace> getListadoEnlacesComunicados(List<ConvocatoriaEnlace> convocatoriaEnlaces) {
    List<Enlace> enlaces = new ArrayList<>();
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

}
