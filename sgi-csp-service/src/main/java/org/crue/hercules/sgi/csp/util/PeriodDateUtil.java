package org.crue.hercules.sgi.csp.util;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PeriodDateUtil {

  private static final int MONTHS_TO_SUBTRACT = 1;

  public static Instant calculateFechaInicioPeriodo(Instant fechaInicio, Integer mesInicial, TimeZone timeZone) {
    if (mesInicial == null) {
      return null;
    }

    ZonedDateTime fechaBase = fechaInicio.atZone(timeZone.toZoneId());

    // El número de meses a sumar siempre es el mes en el que empezar menos uno (si
    // empezamos en el primer mes, no hay que sumar nada)
    ZonedDateTime fechaInicioPeriodo = fechaBase.plusMonths(mesInicial - MONTHS_TO_SUBTRACT);

    Instant instantInicioPeriodo = fechaInicioPeriodo.toInstant();

    return instantInicioPeriodo;
  }

  public static Instant calculateFechaFinPeriodo(Instant fechaInicio, Integer mesFinal, Instant fechaFin, TimeZone timeZone) {
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
}
