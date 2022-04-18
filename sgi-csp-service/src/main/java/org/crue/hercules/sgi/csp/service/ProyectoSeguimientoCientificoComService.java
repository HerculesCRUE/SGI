package org.crue.hercules.sgi.csp.service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.com.CspComInicioPresentacionSeguimientoCientificoData;
import org.crue.hercules.sgi.csp.dto.com.CspComPresentacionSeguimientoCientificoIpData;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoSeguimientoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProyectoSeguimientoCientificoComService {
  private static final String CONFIG_CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_DESTINATARIOS = "csp-com-inicio-pres-seg-cientifico-destinatarios-";

  private final ProyectoRepository proyectoRepository;
  private final ProyectoPeriodoSeguimientoRepository proyectoPeriodoSeguimientoRepository;
  private final ProyectoEquipoRepository proyectoEquipoRepository;
  private final SgiApiCnfService configService;
  private final SgiApiComService emailService;
  private final SgiApiSgpService sgiApiSgpService;
  private final SgiConfigProperties sgiConfigProperties;

  /**
   * Agrupa los periodos de seguimiento científico por proyecto con la
   * fechaInicioPresentación dentro del rango
   * del mes actual, construye el comunicado para enviárselos a las destinatarios
   * asignados a las unidades de gestión
   * que pertenezca el proyecto
   * 
   * @throws JsonProcessingException posible excepción al parsear los datos
   */
  public void sendStartOfJustificacionSeguimientoCientificoCommunication() throws JsonProcessingException {
    ZoneId zoneId = sgiConfigProperties.getTimeZone().toZoneId();
    YearMonth yearMonth = YearMonth.now(zoneId);

    List<ProyectoPeriodoSeguimiento> periodos = this.proyectoPeriodoSeguimientoRepository
        .findByFechaInicioPresentacionBetweenAndProyectoActivoTrue(yearMonth.atDay(1).atStartOfDay(zoneId)
            .toInstant(),
            yearMonth.atEndOfMonth().atStartOfDay(zoneId).withHour(23).withMinute(59).withSecond(59).toInstant());

    List<Long> proyectoIds = getProyectosIdsByPeriodos(periodos);

    if (CollectionUtils.isEmpty(proyectoIds)) {

      log.info(
          "No existen proyectos que requieran generar aviso de inicio del período de presentación de justificación de gastos");
      return;
    }

    Map<String, List<Proyecto>> proyectosByUnidadGestionRef = getProyectosByUnidadGestion(proyectoIds);
    Map<Long, List<ProyectoPeriodoSeguimiento>> periodosByProyecto = getPeriodosSeguimientoCientificoByProyecto(
        periodos);

    for (Map.Entry<String, List<Proyecto>> entry : proyectosByUnidadGestionRef.entrySet()) {

      List<Recipient> recipients = getRecipientsUG(entry.getKey());

      List<CspComInicioPresentacionSeguimientoCientificoData.Proyecto> proyectosEmail = getProyectosEmailSeguimientoCientifico(
          periodosByProyecto, proyectosByUnidadGestionRef.get(entry.getKey()));

      sendEmailForPeriodosSeguimientoUG(recipients, proyectosEmail);
    }
    log.debug("enviarComunicadoInicioPresentacionJustificacionGastos() - end");
  }

  /**
   * Envía comunicados de aviso de Inicio/Finalización de periodo de presentación
   * de documentos por cada inicio de
   * periodo de seguimiento científico que empiece / termine dentro de tres dias
   * hábiles o naturales respectivamente.
   */
  public void sendCommunicationIPs() {

    List<Instant> periodsStartDates = this.getDateThreeWorkableDaysAfter();

    periodsStartDates.forEach(periodsStartDate -> {
      List<EmailOutput> comunicadosInicioPeriodoSeguimientoIps = getComunicadoIPStartPeriods(periodsStartDate);

      if (!CollectionUtils.isEmpty(comunicadosInicioPeriodoSeguimientoIps)) {
        buildAndSendComunicadoInicioPeriodoJustificacionSeguimientoEmailToIps(comunicadosInicioPeriodoSeguimientoIps);
      }
    });
    List<EmailOutput> comunicadosFinPeriodoSeguimientoIps = getComunicadoIPEndPeriods();
    if (!CollectionUtils.isEmpty(comunicadosFinPeriodoSeguimientoIps)) {
      buildAndSendComunicadoFinPeriodoJustificacionSeguimientoEmailToIps(comunicadosFinPeriodoSeguimientoIps);
    }

  }

  /**
   * Obtine una lista de objetos de tipo @link{EmailOutput} obtenida a partir de
   * una lista de @link{ProyectoPeriodoSeguimiento}
   * cuya fechaInicioPresentacion destá dentro del rango de la fecha pasada por
   * parámetro
   * 
   * @param periodsStartDate fecha en la que se buscan los periodos de seguimiento
   * @return List @link{EmailOutput}
   */
  private List<EmailOutput> getComunicadoIPStartPeriods(Instant periodsStartDate) {
    if (periodsStartDate == null) {
      return new LinkedList<>();
    }

    Instant dateFrom = periodsStartDate.atZone(this.sgiConfigProperties.getTimeZone().toZoneId()).with(LocalTime.MIN)
        .toInstant();
    Instant dateTo = periodsStartDate;

    List<ProyectoPeriodoSeguimiento> periodos = this.proyectoPeriodoSeguimientoRepository
        .findByFechaInicioPresentacionBetweenAndProyectoActivoTrue(dateFrom, dateTo);
    return getComunicadosIpsByPeriodosSeguimiento(periodos, TipoComunicado.INICIO);
  }

  /**
   * Obtine una lista de objetos de tipo @link{EmailOutput} obtenida a partir de
   * una lista de @link{ProyectoPeriodoSeguimiento}
   * * cuya fechaFinPresentacion destá dentro del rango de la fecha pasada por
   * parámetro
   * 
   * @return List @link{EmailOutput}
   */
  private List<EmailOutput> getComunicadoIPEndPeriods() {
    Instant fechaFinFrom = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MIN).plusDays(3).toInstant();
    Instant fechaFinTo = this.getLastInstantOfDay().plusDays(3)
        .toInstant();
    List<ProyectoPeriodoSeguimiento> periodos = this.proyectoPeriodoSeguimientoRepository
        .findByFechaFinPresentacionBetweenAndProyectoActivoTrue(fechaFinFrom, fechaFinTo);

    return getComunicadosIpsByPeriodosSeguimiento(periodos, TipoComunicado.FIN);
  }

  /**
   * Construye una lista de objetos de tipo @link{EmailOutput} dada
   * una lista de objetos de tipo @link{ProyectoPeriodoSeguimiento}
   * Se obtienen los ids de los proyectos asociados a los periodos, por cada
   * id @link{Proyecto} obtiene los datos para enviar el comunicado al/los IP/s
   * asociado/s a cada proyecto
   * 
   * @param periodos lista @link{ProyectoPeriodoSeguimiento}
   * @return lista @link{EmailOutput}
   */
  private List<EmailOutput> getComunicadosIpsByPeriodosSeguimiento(List<ProyectoPeriodoSeguimiento> periodos,
      TipoComunicado tipo) {
    List<Long> proyectoIds = getProyectosIdsByPeriodos(periodos);

    return proyectoIds
        .stream().map(idProyecto -> {
          Optional<Proyecto> proyecto = this.proyectoRepository.findById(idProyecto);
          ProyectoPeriodoSeguimiento periodo = periodos.stream()
              .filter(currentPeriodo -> currentPeriodo.getProyectoId().compareTo(idProyecto) == 0).findFirst()
              .orElse(null);
          if (periodo == null) {
            return null;
          }

          CspComPresentacionSeguimientoCientificoIpData data = CspComPresentacionSeguimientoCientificoIpData.builder()
              .fechaInicio(periodo.getFechaInicioPresentacion())
              .fechaFin(periodo.getFechaFinPresentacion())
              .numPeriodo(periodo.getNumPeriodo())
              .titulo(proyecto.map(Proyecto::getTitulo).orElse(""))
              .build();
          EmailOutput comunicado = null;
          try {
            if (TipoComunicado.FIN.equals(tipo)) {
              comunicado = this.emailService.createComunicadoFinPresentacionSeguimientoCientificoIPEmail(
                  data, getRecipientsFromEquipoProyecto(idProyecto, periodo));
            } else {
              comunicado = this.emailService.createComunicadoInicioPresentacionSeguimientoCientificoIPEmail(
                  data, getRecipientsFromEquipoProyecto(idProyecto, periodo));
            }

          } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
          }
          return comunicado;
        }).collect(Collectors.toList());
  }

  /**
   * Por cada objeto de tipo @link{ComunicadoIPs} obtiene un
   * objeto @link{EmailOutput} y manda el comunicado de Inicio de
   * presentación a los IPs configuardos en el objeto @link{ComunicadoIPs}
   * 
   * @param comunicadosIps lista de @link{ComunicadoIPs}
   */
  private void buildAndSendComunicadoInicioPeriodoJustificacionSeguimientoEmailToIps(
      List<EmailOutput> comunicadosIps) {

    comunicadosIps.stream().filter(Objects::nonNull)
        .forEach(comunicado -> this.emailService.sendEmail(comunicado.getId()));
  }

  /**
   * Por cada objeto de tipo @link{ComunicadoIPs} obtiene un
   * objeto @link{EmailOutput} y manda el comunicado de vencimiento de
   * presentación a los IPs configuardos en el objeto @link{ComunicadoIPs}
   * 
   * @param comunicadosFinPeriodoSeguimientoIps lista de @link{ComunicadoIPs}
   */
  private void buildAndSendComunicadoFinPeriodoJustificacionSeguimientoEmailToIps(
      List<EmailOutput> comunicadosFinPeriodoSeguimientoIps) {
    comunicadosFinPeriodoSeguimientoIps.stream()
        .filter(Objects::nonNull)
        .forEach(comunicado -> this.emailService.sendEmail(comunicado.getId()));
  }

  /**
   * Obtiene los emails de los Investigadores principales vigentes de un periodo
   * 
   * @param proyectoId id del proyecto
   * @param periodo    periodo seguiemiento @link{ProyectoPeriodoSeguimiento}
   * @return lista @link{Recipient}
   */
  private List<Recipient> getRecipientsFromEquipoProyecto(Long proyectoId, ProyectoPeriodoSeguimiento periodo) {
    return this.proyectoEquipoRepository
        .findByProyectoIdAndRolProyectoRolPrincipalTrue(proyectoId)
        .stream()
        .filter(member -> (member.getFechaInicio() == null || member.getFechaInicio().isAfter(periodo.getFechaInicio()))
            && (member.getFechaFin() == null || member.getFechaFin().isAfter(periodo.getFechaFin())))
        .map(member -> {
          List<String> emails = this.sgiApiSgpService.findById(member.getPersonaRef()).getEmails().stream()
              .map(PersonaOutput.Email::getEmail).collect(Collectors.toList());
          return emails.stream().map(email -> Recipient.builder()
              .address(email)
              .name(email)
              .build()).collect(Collectors.toList());
        }).flatMap(List::stream).collect(Collectors.toList());
  }

  /**
   * Obtiene fecha tres dias después de la fecha actual, siendo hoy un día hábil
   * 
   * @return returns List of @link{Instant}
   */
  private List<Instant> getDateThreeWorkableDaysAfter() {
    ZonedDateTime now = getLastInstantOfDay();
    DayOfWeek day = now.getDayOfWeek();
    List<Instant> dates = new LinkedList<>();

    switch (day) {
      case MONDAY:
        dates.add(now.plusDays(3).toInstant());
        break;
      case TUESDAY:
        ZonedDateTime friday = now.plusDays(3);
        ZonedDateTime saturday = getLastInstantOfDay().plusDays(4);
        ZonedDateTime sunday = getLastInstantOfDay().plusDays(5);

        dates.add(friday.toInstant());
        dates.add(saturday.toInstant());
        dates.add(sunday.toInstant());
        break;
      case WEDNESDAY:
      case THURSDAY:
      case FRIDAY:
        dates.add(now.plusDays(5).toInstant());
        break;
      default:
        break;
    }

    return dates;
  }

  private ZonedDateTime getLastInstantOfDay() {
    return Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MAX);
  }

  private List<CspComInicioPresentacionSeguimientoCientificoData.Proyecto> getProyectosEmailSeguimientoCientifico(
      Map<Long, List<ProyectoPeriodoSeguimiento>> periodosByProyecto,
      List<Proyecto> proyectosUnidad) {

    return proyectosUnidad.stream().flatMap(proyecto -> periodosByProyecto.get(proyecto.getId()).stream()
        .map(periodo -> CspComInicioPresentacionSeguimientoCientificoData.Proyecto
            .builder()
            .titulo(proyecto.getTitulo())
            .fechaInicio(periodo.getFechaInicioPresentacion())
            .fechaFin(periodo.getFechaFinPresentacion())
            .build()))
        .collect(Collectors.toList());
  }

  private void sendEmailForPeriodosSeguimientoUG(List<Recipient> recipients,
      List<CspComInicioPresentacionSeguimientoCientificoData.Proyecto> proyectosEmail) throws JsonProcessingException {

    ZoneId zoneId = sgiConfigProperties.getTimeZone().toZoneId();
    YearMonth yearMonth = YearMonth.now(zoneId);

    EmailOutput emailOutput = emailService
        .createComunicadoInicioPresentacionSeguimientoCientificoEmail(
            CspComInicioPresentacionSeguimientoCientificoData.builder().fecha(yearMonth.atDay(1)).proyectos(
                proyectosEmail).build(),
            recipients);
    emailService.sendEmail(emailOutput.getId());
  }

  private List<Recipient> getRecipientsUG(String unidadGestionRef) throws JsonProcessingException {
    List<String> destinatarios = configService
        .findStringListByName(
            CONFIG_CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_DESTINATARIOS + unidadGestionRef);

    return destinatarios.stream()
        .map(destinatario -> Recipient.builder().name(destinatario).address(destinatario).build())
        .collect(Collectors.toList());
  }

  private Map<Long, List<ProyectoPeriodoSeguimiento>> getPeriodosSeguimientoCientificoByProyecto(
      List<ProyectoPeriodoSeguimiento> periodos) {
    Map<Long, List<ProyectoPeriodoSeguimiento>> periodosByProyecto = new HashMap<>();
    for (ProyectoPeriodoSeguimiento periodo : periodos) {
      Long proyectoId = periodo.getProyectoId();

      List<ProyectoPeriodoSeguimiento> periodosProyecto = periodosByProyecto.get(proyectoId);
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
        proyectosUnidad = new LinkedList<>();
      }
      proyectosUnidad.add(proyecto);
      proyectosByUnidadGestionRef.put(proyecto.getUnidadGestionRef(), proyectosUnidad);
    }
    return proyectosByUnidadGestionRef;
  }

  private List<Long> getProyectosIdsByPeriodos(List<ProyectoPeriodoSeguimiento> periodos) {
    return periodos.stream().map(ProyectoPeriodoSeguimiento::getProyectoId)
        .collect(Collectors.toList());
  }

  private enum TipoComunicado {
    INICIO, FIN;
  }

}
