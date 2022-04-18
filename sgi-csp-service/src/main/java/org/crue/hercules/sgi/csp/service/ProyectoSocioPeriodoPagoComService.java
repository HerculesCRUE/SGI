package org.crue.hercules.sgi.csp.service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.com.CspComVencimientoPeriodoPagoSocioData;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.sgemp.EmpresaOutput;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoPagoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgempService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProyectoSocioPeriodoPagoComService {
  private static final String CONFIG_CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DESTINATARIOS = "csp-vencim-periodo-pago-socio-destinatarios-";

  private final ProyectoSocioPeriodoPagoRepository proyectoSocioPeriodoPagoRepository;
  private final ProyectoSocioRepository proyectoSocioRepository;
  private final ProyectoRepository proyectoRepository;
  private final SgiApiCnfService configService;
  private final SgiApiComService emailService;
  private final SgiConfigProperties sgiConfigProperties;
  private final SgiApiSgempService sgiApiSgempService;

  public void enviarComunicadoVencimientoPeriodoPagoSocio() {
    List<Instant> dates = this.getDateSevenWorkableDaysAfterAndWeekendOnMonday();
    ZoneId zoneId = sgiConfigProperties.getTimeZone().toZoneId();

    dates.forEach(periodsFechaPrevistaPago -> {
      Instant dateFrom = periodsFechaPrevistaPago.atZone(zoneId).with(LocalTime.MIN).toInstant();
      Instant dateTo = periodsFechaPrevistaPago;

      List<ProyectoSocioPeriodoPago> periodos = this.proyectoSocioPeriodoPagoRepository
          .findByFechaPrevistaPagoBetweenAndFechaPagoIsNullAndProyectoSocioProyectoActivoTrue(dateFrom, dateTo);

      this.buildAndSendEmailForPeriodosJustificacionGasto(periodos);

    });
  }

  private void buildAndSendEmailForPeriodosJustificacionGasto(List<ProyectoSocioPeriodoPago> periodos) {
    periodos.forEach(periodo -> {
      EmailOutput comunicado = buildComunicadoVencimientoPago(periodo);
      this.emailService.sendEmail(comunicado.getId());
    });
  }

  private EmailOutput buildComunicadoVencimientoPago(ProyectoSocioPeriodoPago periodo) {
    ProyectoSocio proyectoSocio = this.proyectoSocioRepository.findById(periodo.getProyectoSocioId())
        .orElseThrow(() -> new ProyectoSocioNotFoundException(periodo.getProyectoSocioId()));
    EmpresaOutput empresa = sgiApiSgempService.findById(proyectoSocio.getEmpresaRef());
    Proyecto proyecto = this.proyectoRepository.findById(proyectoSocio.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoSocio.getProyectoId()));

    CspComVencimientoPeriodoPagoSocioData data = CspComVencimientoPeriodoPagoSocioData.builder()
        .fechaPrevistaPago(periodo.getFechaPrevistaPago())
        .nombreEntidadColaboradora(empresa.getNombre())
        .titulo(proyecto.getTitulo())
        .build();
    List<Recipient> recipients = getRecipientsUG(proyecto.getUnidadGestionRef());
    EmailOutput comunicado = null;
    try {
      comunicado = this.emailService.createComunicadoVencimientoPeriodoPagoSocioEmail(data, recipients);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
    return comunicado;
  }

  /**
   * Obtiene fecha siete dias después de la fecha actual, siendo hoy un día
   * hábil, excepto los lunes
   * teniendo que devolver también sábado y domingo
   * 
   * @return returns List of @link{Instant}
   */
  private List<Instant> getDateSevenWorkableDaysAfterAndWeekendOnMonday() {
    ZonedDateTime now = getLastInstantOfDay();
    DayOfWeek day = now.getDayOfWeek();
    List<Instant> dates = new LinkedList<>();
    final int daysAfter = 9;

    switch (day) {
      /*
       * Lunes +9 -> Miércoles
       * Martes +9 -> Jueves
       * Miércoles +9 +10 +11-> Viernes, Sábado y Domingo
       * Jueves +9 -> Lunes
       * Viernes +9 -> Martes
       */
      case WEDNESDAY:
        // next Monday
        ZonedDateTime nextFriday = now.plusDays(daysAfter);
        // next Saturday
        ZonedDateTime saturday = getLastInstantOfDay().plusDays(daysAfter + 1l);
        // next Sunday
        ZonedDateTime sunday = getLastInstantOfDay().plusDays(daysAfter + 2l);

        dates.add(nextFriday.toInstant());
        dates.add(saturday.toInstant());
        dates.add(sunday.toInstant());
        break;
      case MONDAY:
      case TUESDAY:
      case THURSDAY:
      case FRIDAY:
        dates.add(now.plusDays(daysAfter).toInstant());
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

  private List<Recipient> getRecipientsUG(String unidadGestionRef) {
    List<String> destinatarios = null;
    try {
      destinatarios = configService
          .findStringListByName(
              CONFIG_CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DESTINATARIOS + unidadGestionRef);
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
