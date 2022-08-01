package org.crue.hercules.sgi.csp.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.time.Instant;
import java.util.Calendar;
import java.util.TimeZone;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PeriodDateUtilTest {

  private static final int JULY = 7;
  private static final String UTC = "UTC";

  @ParameterizedTest
  @CsvSource(value = {// @formatter:off
      "2021-01-08T00:00:00Z, 2021-12-31T23:59:59Z, 1, 1, 2021-01-08T00:00:00Z, 2021-02-07T23:59:59Z",
      "2021-01-08T00:00:00Z, 2021-12-31T23:59:59Z, 1, 2, 2021-01-08T00:00:00Z, 2021-03-07T23:59:59Z",
      "2021-01-08T00:00:00Z, 2021-12-31T23:59:59Z, 1, 3, 2021-01-08T00:00:00Z, 2021-04-07T23:59:59Z",
      "2021-01-08T00:00:00Z, 2021-12-31T23:59:59Z, 4, 5, 2021-04-08T00:00:00Z, 2021-06-07T23:59:59Z",
      "2021-01-31T00:00:00Z, 2021-12-31T23:59:59Z, 2, 2, 2021-02-28T00:00:00Z, 2021-03-30T23:59:59Z",
      "2021-01-31T00:00:00Z, 2021-12-31T23:59:59Z, 3, 3, 2021-03-31T00:00:00Z, 2021-04-29T23:59:59Z",
      "2021-02-28T00:00:00Z, 2021-12-31T23:59:59Z, 1, 3, 2021-02-28T00:00:00Z, 2021-05-27T23:59:59Z",
      "2020-10-01T00:00:00Z, 2024-12-31T23:59:59Z, 1, 18, 2020-10-01T00:00:00Z, 2022-03-31T23:59:59Z",
      "2020-10-01T00:00:00Z, 2024-12-31T23:59:59Z, 19, 36, 2022-04-01T00:00:00Z, 2023-09-30T23:59:59Z"
      // @formatter:on
  })
  void test(Instant fechaInicioProyecto, Instant fechaFinProyecto, Integer mesInicioRango, Integer mesFinRango,
      Instant fechaInicioRangoEsperada, Instant fechaFinRangoEsperada) {

    Instant fechaRangoInicio = PeriodDateUtil.calculateFechaInicioPeriodo(fechaInicioProyecto, mesInicioRango,
        TimeZone.getDefault());
    Instant fechaRangoFin = PeriodDateUtil.calculateFechaFinPeriodo(fechaInicioProyecto, mesFinRango, fechaFinProyecto,
        TimeZone.getDefault());

    Assertions.assertThat(fechaRangoInicio).isEqualTo(fechaInicioRangoEsperada);
    Assertions.assertThat(fechaRangoFin).isEqualTo(fechaFinRangoEsperada);
  }

  @Test
  void calculateFechaInicioSeguimiento_should_return_a_valid_start_periodo_instant_when_is_called() {

    Instant result = PeriodDateUtil.calculateFechaInicioPeriodo(DEFAULT_FECHA_INICIO(), 3, TimeZone.getTimeZone(UTC));

    assertThat(result).isNotNull();
    Calendar cal = Calendar.getInstance();
    cal.setTime(Date.from(result));
    assertThat(cal.get(Calendar.MONTH) + 1).isEqualTo(JULY);

  }

  @Test
  void calculateFechaFinSeguimiento_should_return_a_valid_end_periodo_instant_when_is_called() {

    Instant result = PeriodDateUtil.calculateFechaFinPeriodo(DEFAULT_FECHA_INICIO(), 3, DEFAULT_FECHA_FIN(),
        TimeZone.getTimeZone(UTC));

    assertThat(result).isNotNull();
    Calendar cal = Calendar.getInstance();
    cal.setTime(Date.from(result));
    assertThat(cal.get(Calendar.MONTH)).isEqualTo(JULY);

  }

  @Test
  void calculateFechaFinSeguimiento_should_return_project_end_date_when_calculated_date_is_greater_than_fecha_fin() {

    Instant result = PeriodDateUtil.calculateFechaFinPeriodo(DEFAULT_FECHA_INICIO(), 6, DEFAULT_FECHA_FIN(),
        TimeZone.getTimeZone(UTC));
    Calendar calResult = Calendar.getInstance();
    calResult.setTime(Date.from(result));

    Calendar calFechaFin = Calendar.getInstance();
    calFechaFin.setTime(Date.from(DEFAULT_FECHA_FIN()));

    assertThat(result).isNotNull();
    assertThat(calResult.get(Calendar.MONTH)).isEqualTo(calFechaFin.get(Calendar.MONTH));
    assertThat(calResult.get(Calendar.DAY_OF_MONTH)).isEqualTo(calFechaFin.get(Calendar.DAY_OF_MONTH));

  }

  private Instant DEFAULT_FECHA_FIN() {
    Calendar calendarFechaFin = Calendar.getInstance();
    calendarFechaFin.setTimeZone(TimeZone.getTimeZone(UTC));
    calendarFechaFin.set(Calendar.DAY_OF_MONTH, 25);
    calendarFechaFin.set(Calendar.YEAR, 2021);
    calendarFechaFin.set(Calendar.MONTH, 7);

    return calendarFechaFin.getTime().toInstant();
  }

  private Instant DEFAULT_FECHA_INICIO() {
    Calendar calendarFechaInicio = Calendar.getInstance();
    calendarFechaInicio.setTimeZone(TimeZone.getTimeZone(UTC));
    calendarFechaInicio.set(Calendar.DAY_OF_MONTH, 25);
    calendarFechaInicio.set(Calendar.YEAR, 2021);
    calendarFechaInicio.set(Calendar.MONTH, 4);
    return calendarFechaInicio.getTime().toInstant();
  }
}
