package org.crue.hercules.sgi.pii.util;

import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.TimeZone;

import org.springframework.data.util.Pair;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PeriodDateUtil {

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
