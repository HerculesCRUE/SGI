package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiCnfService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ComunicadosService {
  private static final String CONFIG_CSP_COM_INICIO_PRESENTACION_JUSTIFICACION_GASTO_DESTINATARIOS = "csp-com-inicio-presentacion-gasto-destinatarios-";
  private final SgiConfigProperties sgiConfigProperties;
  private final ProyectoRepository proyectoRepository;
  private final ProyectoPeriodoJustificacionRepository proyectoPeriodoJustificacionRepository;
  private final SgiApiCnfService configService;
  private final SgiApiComService emailService;

  public ComunicadosService(
      SgiConfigProperties sgiConfigProperties,
      ProyectoRepository proyectoRepository,
      ProyectoPeriodoJustificacionRepository proyectoPeriodoJustificacionRepository,
      SgiApiCnfService configService, SgiApiComService emailService) {
    log.debug(
        "ComunicadosService(SgiConfigProperties sgiConfigProperties, ProyectoRepository proyectoRepository, ProyectoPeriodoJustificacionRepository proyectoPeriodoJustificacionRepository, ConfigService configService, EmailService emailService) - start");
    this.sgiConfigProperties = sgiConfigProperties;
    this.proyectoRepository = proyectoRepository;
    this.proyectoPeriodoJustificacionRepository = proyectoPeriodoJustificacionRepository;
    this.configService = configService;
    this.emailService = emailService;
    log.debug(
        "ComunicadosService(SgiConfigProperties sgiConfigProperties, ProyectoRepository proyectoRepository, ProyectoPeriodoJustificacionRepository proyectoPeriodoJustificacionRepository, ConfigService configService, EmailService emailService) - end");
  }

  public void enviarComunicadoInicioPresentacionJustificacionGastos() throws JsonProcessingException {
    log.debug("enviarComunicadoInicioPresentacionJustificacionGastos() - start");
    ZoneId zoneId = sgiConfigProperties.getTimeZone().toZoneId();
    YearMonth yearMonth = YearMonth.now(zoneId);

    LocalDate firtDayOfCurrentMonth = yearMonth.atDay(1);
    LocalDate lastDayOfCurrentMonth = yearMonth.atEndOfMonth();

    List<ProyectoPeriodoJustificacion> periodos = proyectoPeriodoJustificacionRepository
        .findByFechaInicioPresentacionBetween(firtDayOfCurrentMonth.atStartOfDay(zoneId)
            .toInstant(),
            lastDayOfCurrentMonth.atStartOfDay(zoneId).withHour(23).withMinute(59).withSecond(59).toInstant());
    List<Long> proyectoIds = periodos.stream().map(ProyectoPeriodoJustificacion::getProyectoId)
        .collect(Collectors.toList());
    if (CollectionUtils.isNotEmpty(proyectoIds)) {
      List<Proyecto> proyectos = proyectoRepository.findByIdIn(proyectoIds);
      Map<String, List<Proyecto>> proyectosByUnidadGestionRef = new HashMap<>();
      for (Proyecto proyecto : proyectos) {
        List<Proyecto> proyectosUnidad = proyectosByUnidadGestionRef.get(proyecto.getUnidadGestionRef());
        if (proyectosUnidad == null) {
          proyectosUnidad = new ArrayList<>();
        }
        proyectosUnidad.add(proyecto);
        proyectosByUnidadGestionRef.put(proyecto.getUnidadGestionRef(), proyectosUnidad);
      }

      Map<Long, List<ProyectoPeriodoJustificacion>> periodosByProyecto = new HashMap<>();
      for (ProyectoPeriodoJustificacion periodo : periodos) {
        List<ProyectoPeriodoJustificacion> periodosProyecto = periodosByProyecto.get(periodo.getProyectoId());
        if (periodosProyecto == null) {
          periodosProyecto = new ArrayList<>();
        }
        periodosProyecto.add(periodo);
        periodosByProyecto.put(periodo.getProyectoId(), periodosProyecto);
      }

      for (String unidadGestionRef : proyectosByUnidadGestionRef.keySet()) {
        List<String> destinatarios = configService
            .findStringListByName(
                CONFIG_CSP_COM_INICIO_PRESENTACION_JUSTIFICACION_GASTO_DESTINATARIOS + unidadGestionRef);
        List<Recipient> recipients = destinatarios.stream()
            .map(destinatario -> Recipient.builder().name(destinatario).address(destinatario).build())
            .collect(Collectors.toList());
        List<CspComInicioPresentacionGastoData.Proyecto> proyectosEmail = new ArrayList<>();
        List<Proyecto> proyectosUnidad = proyectosByUnidadGestionRef.get(unidadGestionRef);
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
        EmailOutput emailOutput = emailService
            .createComunicadoInicioPresentacionJustificacionGastosEmail(
                CspComInicioPresentacionGastoData.builder().fecha(firtDayOfCurrentMonth).proyectos(
                    proyectosEmail).build(),
                recipients);
        emailService.sendEmail(emailOutput.getId());
      }
    } else {
      log.info(
          "No existen proyectos que requieran generar aviso de inicio del período de presentación de justificación de gastos");
    }
    log.debug("enviarComunicadoInicioPresentacionJustificacionGastos() - end");
  }
}
