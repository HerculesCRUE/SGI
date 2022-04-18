package org.crue.hercules.sgi.csp.util;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.TimeZone;

import org.springframework.data.util.Pair;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PeriodDateUtil {

  private static final Long MONTHS_TO_SUBTRACT = 1L;

  public static Instant calculateFechaInicioPeriodo(Instant fechaInicio, Integer mesInicial, TimeZone timeZone) {
    if (mesInicial == null) {
      return null;
    }

    ZonedDateTime fechaBase = fechaInicio.atZone(timeZone.toZoneId());

    // El número de meses a sumar siempre es el mes en el que empezar menos uno (si
    // empezamos en el primer mes, no hay que sumar nada)
    ZonedDateTime fechaInicioPeriodo = fechaBase.plusMonths(mesInicial - MONTHS_TO_SUBTRACT);

    return fechaInicioPeriodo.toInstant();
  }

  public static Instant calculateFechaFinPeriodo(Instant fechaInicio, Integer mesFinal, Instant fechaFin,
      TimeZone timeZone) {
    if (mesFinal == null) {
      return null;
    }

    ZonedDateTime fechaBase = fechaInicio.atZone(timeZone.toZoneId());

    // Se calcula el primer día del mes siguiente y se resta un segundo para tener
    // el último mes del día del mes en el que finalizar a las 23:59.59
    ZonedDateTime fechaFinPeriodo = fechaBase.plusMonths(mesFinal).minusSeconds(1);

    Instant instantFinPeriodo = fechaFinPeriodo.toInstant();

    // La fecha de finalización del periodo nunca puede ser posterior a la fecha de
    // fin del proyecto
    if (fechaFin == null) {
      return instantFinPeriodo;
    }
    return fechaFin.isBefore(instantFinPeriodo) ? fechaFin : instantFinPeriodo;
  }

  public static Instant calculateFechaInicioBaremacionByAnio(Integer anio, TimeZone timeZone) {
    return Instant.now().atZone(timeZone.toZoneId())
        .withYear(anio)
        .with(TemporalAdjusters.firstDayOfYear())
        .with(LocalTime.MIN).toInstant();
  }

  public static Instant calculateFechaFinBaremacionByAnio(Integer anio, TimeZone timeZone) {
    return Instant.now().atZone(timeZone.toZoneId())
        .withYear(anio)
        .with(TemporalAdjusters.lastDayOfYear())
        .with(LocalTime.MAX).toInstant();
  }

  public static Pair<Instant, Instant> calculateFechasInicioFinBaremacionByAnio(Integer anio, TimeZone timeZone) {
    Instant fechaInicioBaremacion = calculateFechaInicioBaremacionByAnio(anio, timeZone);
    Instant fechaFinBaremacion = calculateFechaFinBaremacionByAnio(anio, timeZone);
    return Pair.of(fechaInicioBaremacion, fechaFinBaremacion);
  }
}
