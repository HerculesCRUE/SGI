package org.crue.hercules.sgi.csp.service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.com.CspComPeriodoJustificacionSocioData;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.sgemp.EmpresaOutput;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgempService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProyectoSocioPeriodoJustificacionComService {
  private static final String CSP_INICIO_FIN_PERIODO_JUST_SOCIO_DESTINATARIO = "csp-inicio-fin-periodo-just-socio-destinatarios-";

  private final ProyectoSocioPeriodoJustificacionRepository proyectoSocioPeriodoJustificacionRepository;
  private final ProyectoRepository proyectoRepository;
  private final ProyectoSocioRepository proyectoSocioRepository;
  private final SgiApiCnfService configService;
  private final SgiApiComService emailService;
  private final SgiConfigProperties sgiConfigProperties;
  private final SgiApiSgempService sgiApiSgempService;

  public void enviarComunicadoInicioFinPeriodoJustificacionSocio() {
    Instant dateFrom = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MIN).toInstant();
    Instant dateTo = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MAX).toInstant();

    List<ProyectoSocioPeriodoJustificacion> periodosFechaInicioPresentacion = this.proyectoSocioPeriodoJustificacionRepository
        .findByFechaInicioPresentacionBetweenAndProyectoSocioProyectoActivoTrue(dateFrom, dateTo);

    this.buildAndSendComunicadosForPeriodosJustificacionSocio(
        periodosFechaInicioPresentacion,
        TipoComunicado.INICIO);

    this.getDatesThreeWorkableDaysAfter().forEach(date -> {
      Instant fechaFinPresentacionFrom = date.atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
          .with(LocalTime.MIN).toInstant();
      Instant fechaFinPresentacionTo = date;

      List<ProyectoSocioPeriodoJustificacion> periodosFechaFinPresentacionAfterThreeWorkableDays = this.proyectoSocioPeriodoJustificacionRepository
          .findByFechaFinPresentacionBetweenAndProyectoSocioProyectoActivoTrueAndDocumentacionRecibidaFalse(
              fechaFinPresentacionFrom, fechaFinPresentacionTo);

      this.buildAndSendComunicadosForPeriodosJustificacionSocio(periodosFechaFinPresentacionAfterThreeWorkableDays,
          TipoComunicado.FIN);
    });
  }

  private void buildAndSendComunicadosForPeriodosJustificacionSocio(List<ProyectoSocioPeriodoJustificacion> periodos,
      TipoComunicado tipo) {
    periodos.forEach(periodo -> {
      EmailOutput comunicado = this.buildComunicadoInicioPeriodoJustificacionSocio(periodo, tipo);
      this.emailService.sendEmail(comunicado.getId());
    });
  }

  private EmailOutput buildComunicadoInicioPeriodoJustificacionSocio(ProyectoSocioPeriodoJustificacion periodo,
      TipoComunicado tipo) {

    ProyectoSocio proyectoSocio = this.proyectoSocioRepository.findById(periodo.getProyectoSocioId())
        .orElseThrow(() -> new ProyectoSocioNotFoundException(periodo.getProyectoSocioId()));
    Proyecto proyecto = this.proyectoRepository.findById(proyectoSocio.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoSocio.getProyectoId()));

    CspComPeriodoJustificacionSocioData data = buildPeriodoJustificacionSocioData(proyectoSocio, proyecto,
        periodo);

    List<Recipient> recipients = getRecipientsUG(proyecto.getUnidadGestionRef());

    EmailOutput comunicado = null;

    try {
      if (TipoComunicado.INICIO.equals(tipo)) {
        comunicado = this.emailService.createComunicadoInicioPresentacionPeriodoJustificacionSocioEmail(data,
            recipients);
      } else {
        comunicado = this.emailService.createComunicadoFinPresentacionPeriodoJustificacionSocioEmail(data,
            recipients);
      }
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
    return comunicado;
  }

  private CspComPeriodoJustificacionSocioData buildPeriodoJustificacionSocioData(
      ProyectoSocio proyectoSocio,
      Proyecto proyecto,
      ProyectoSocioPeriodoJustificacion periodo) {

    EmpresaOutput empresa = proyectoSocio == null ? null : sgiApiSgempService.findById(proyectoSocio.getEmpresaRef());

    return CspComPeriodoJustificacionSocioData.builder()
        .fechaInicio(periodo.getFechaInicioPresentacion())
        .fechaFin(periodo.getFechaFinPresentacion())
        .nombreEntidad(empresa == null ? "" : empresa.getNombre())
        .numPeriodo(periodo.getNumPeriodo())
        .titulo(proyecto.getTitulo())
        .build();
  }

  private List<Recipient> getRecipientsUG(String unidadGestionRef) {
    List<String> destinatarios = null;
    try {
      destinatarios = configService
          .findStringListByName(
              CSP_INICIO_FIN_PERIODO_JUST_SOCIO_DESTINATARIO + unidadGestionRef);
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

  private List<Instant> getDatesThreeWorkableDaysAfter() {
    ZonedDateTime now = getLastInstantOfDay();
    DayOfWeek day = now.getDayOfWeek();
    List<Instant> dates = new LinkedList<>();
    final int daysAfter = 3;

    switch (day) {
      case TUESDAY:
        // next Friday
        ZonedDateTime nextFriday = now.plusDays(daysAfter);
        // next Saturday
        ZonedDateTime saturday = getLastInstantOfDay().plusDays(daysAfter + 1);
        // next Sunday
        ZonedDateTime sunday = getLastInstantOfDay().plusDays(daysAfter + 2);

        dates.add(nextFriday.toInstant());
        dates.add(saturday.toInstant());
        dates.add(sunday.toInstant());
        break;
      case MONDAY:
      case WEDNESDAY:
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

  private enum TipoComunicado {
    INICIO, FIN;
  }
}