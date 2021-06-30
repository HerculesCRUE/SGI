package org.crue.hercules.sgi.csp.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PeriodDateUtil {

  private static final int MONTHS_TO_SUBTRACT = 1;

  public static Instant calculateFechaInicioPeriodo(Instant fechaInicio, int mesInicial,
      Instant fechaCalculoPeriodoRef) {

    OffsetDateTime fechaBase = fechaCalculoPeriodoRef.atOffset(ZoneOffset.UTC);

    // Sumamos el desplazamiento UTC máximo antes de hacer los cálculos
    // para que el cálculo de los días del mes de referencia sea correcto
    fechaBase = fechaBase.plusSeconds(ZoneOffset.MAX.getTotalSeconds());

    // El número de meses a sumar siempre es el mes en el que empezar menos uno (si
    // empezamos en el primer mes, no hay que sumar nada)
    OffsetDateTime fechaInicioPeriodo = fechaBase.plusMonths(mesInicial - MONTHS_TO_SUBTRACT);

    // Restamos el desplazamiento UTC máximo tras hacer los cálculos
    // para recuperar la hora establecida inicialmente
    fechaInicioPeriodo = fechaInicioPeriodo.minusSeconds(ZoneOffset.MAX.getTotalSeconds());

    Instant instantInicioPeridio = fechaInicioPeriodo.toInstant();

    // La fecha de inicio del periodo nunca puede ser anterior a la fecha de inicio
    // del proyecto
    return fechaInicio.isAfter(instantInicioPeridio) ? fechaInicio : instantInicioPeridio;

  }

  public static Instant calculateFechaFinPeriodo(Instant fechaInicio, Instant fechaFin, int mesFinal,
      Instant fechaCalculoPeriodoRef) {

    OffsetDateTime fechaBase = fechaCalculoPeriodoRef.atOffset(ZoneOffset.UTC);

    // Sumamos el desplazamiento UTC máximo antes de hacer los cálculos
    // para que el cálculo de los días del mes de referencia sea correcto
    fechaBase = fechaBase.plusSeconds(ZoneOffset.MAX.getTotalSeconds());

    // Se calcula el primer día del mes siguiente y se resta un segundo para tener
    // el último mes del día del mes en el que finalizar a las 23:59.59
    OffsetDateTime fechaFinPeriodo = fechaBase.plusMonths(mesFinal).minusSeconds(1);

    // Restamos el desplazamiento UTC máximo tras hacer los cálculos
    // para recuperar la hora establecida inicialmente
    fechaFinPeriodo = fechaFinPeriodo.minusSeconds(ZoneOffset.MAX.getTotalSeconds());

    Instant instantFinPeridio = fechaFinPeriodo.toInstant();

    // La fecha de finalización del periodo nunca puede ser posterior a la fecha de
    // fin del proyecto
    return fechaFin.isBefore(instantFinPeridio) ? fechaFin : instantFinPeridio;

  }

}
