package org.crue.hercules.sgi.csp.service;

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
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ComunicadosService {
  private static final String CONFIG_CSP_COM_INICIO_PRESENTACION_JUSTIFICACION_GASTO_DESTINATARIOS = "csp-com-inicio-presentacion-gasto-destinatarios-";

  private final SgiConfigProperties sgiConfigProperties;
  private final ProyectoRepository proyectoRepository;
  private final ProyectoPeriodoJustificacionRepository proyectoPeriodoJustificacionRepository;
  private final ProyectoSeguimientoCientificoComService proyectoSeguimientoCientificoComService;
  private final ProyectoSocioPeriodoPagoComService proyectoSocioPeriodoPagoComService;
  private final SgiApiCnfService configService;
  private final SgiApiComService emailService;
  private final ProyectoSocioPeriodoJustificacionComService proyectoSocioPeriodoJustificacionComService;

  public ComunicadosService(
      SgiConfigProperties sgiConfigProperties,
      ProyectoRepository proyectoRepository,
      ProyectoPeriodoJustificacionRepository proyectoPeriodoJustificacionRepository,
      SgiApiCnfService configService,
      SgiApiComService emailService,
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

  public void enviarComunicadosPeriodoJustificacionSocio() {
    this.proyectoSocioPeriodoJustificacionComService.enviarComunicadoInicioFinPeriodoJustificacionSocio();
  }

}
