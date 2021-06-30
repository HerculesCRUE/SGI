import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import { DateTime } from 'luxon';


export function comparePeriodoSeguimiento(
  convocatoriaPeriodoSeguimiento: IConvocatoriaPeriodoSeguimientoCientifico,
  proyectoPeriodoSeguimiento: IProyectoPeriodoSeguimiento,
  fechaInicioProyecto: DateTime,
  fechaFinProyecto: DateTime): boolean {

  let fechaInicioConvocatoriaPeriodoSeguimiento: DateTime;
  if (convocatoriaPeriodoSeguimiento.mesInicial) {
    fechaInicioConvocatoriaPeriodoSeguimiento =
      getFechaInicioPeriodoSeguimiento(fechaInicioProyecto, convocatoriaPeriodoSeguimiento.mesInicial);
  }

  let fechaFinConvocatoriaConceptoGasto: DateTime;
  if (convocatoriaPeriodoSeguimiento.mesFinal) {
    fechaFinConvocatoriaConceptoGasto = getFechaFinPeriodoSeguimiento(fechaInicioProyecto, fechaFinProyecto,
      convocatoriaPeriodoSeguimiento.mesFinal, fechaInicioConvocatoriaPeriodoSeguimiento);
  }

  return convocatoriaPeriodoSeguimiento.numPeriodo !== convocatoriaPeriodoSeguimiento.numPeriodo
    || proyectoPeriodoSeguimiento.tipoSeguimiento !== convocatoriaPeriodoSeguimiento.tipoSeguimiento
    || proyectoPeriodoSeguimiento.fechaInicio?.toMillis() !== fechaInicioConvocatoriaPeriodoSeguimiento?.toMillis()
    || proyectoPeriodoSeguimiento.fechaFin?.toMillis() !== fechaFinConvocatoriaConceptoGasto?.toMillis()
    || proyectoPeriodoSeguimiento.fechaInicioPresentacion?.toMillis() !== convocatoriaPeriodoSeguimiento.fechaInicioPresentacion?.toMillis()
    || proyectoPeriodoSeguimiento.fechaFinPresentacion?.toMillis() !== convocatoriaPeriodoSeguimiento.fechaFinPresentacion?.toMillis()
    || (proyectoPeriodoSeguimiento.observaciones !== convocatoriaPeriodoSeguimiento.observaciones);
}



/**
 * Obtiene la fecha resultante de sumarle a la fecha de inicio del proyecto el numero de meses de mesInicialPeriodoSeguimiento,
 * si la fecha de inicio del proyecto es mayor que la resultante para el concepto devuelve la del proyecto.
 *
 * @param fechaInicioProyecto fecha de inicio del proyecto
 * @param mesInicialPeriodoSeguimiento mes inicial del periodo seguimiento en la convocatoria
 * @returns la fecha de inicio
 */
export function getFechaInicioPeriodoSeguimiento(fechaInicioProyecto: DateTime, mesInicialPeriodoSeguimiento: number): DateTime {
  let fechaInicioPeriodoSeguimiento = fechaInicioProyecto.plus({ months: mesInicialPeriodoSeguimiento - 1 });

  if (mesInicialPeriodoSeguimiento > 1) {
    fechaInicioPeriodoSeguimiento = fechaInicioPeriodoSeguimiento.set({
      day: 1, hour: 0, minute: 0, second: 0
    });
  }

  return fechaInicioPeriodoSeguimiento;
}

/**
 * Obtiene la fecha de fin del periodo seguimiento.
 *  - Si la fecha resultante de sumar el numero de meses a la fecha de inicio del proyecto
 *    esta dentro del rango del proyecto o la fecha inicio del periodo de seguimiento es posterior a la fecha de fin del proyecto,
 *   el ultimo dia del mes resultante de la suma a las 23:59:59
 *  - Si la fecha de fin es posterior a la fecha de fin del proyecto y la fecha de inicio del periodo de seguimiento
 *    esta dentro del proyecto, la fecha de fin del proyecto
 *
 * @param fechaInicioProyecto fecha de inicio del proyecto
 * @param fechaFinProyecto  fecha de fin del proyecto
 * @param mesFinalPeriodoSeguimiento mes final del concepto de gasto en la convocatoria
 * @param fechaInicioPeriodoSeguimiento fecha de inicio del concepto de gasto
 * @returns la fecha de fin
 */
export function getFechaFinPeriodoSeguimiento(
  fechaInicioProyecto: DateTime, fechaFinProyecto: DateTime,
  mesFinalPeriodoSeguimiento: number, fechaInicioPeriodoSeguimiento: DateTime): DateTime {

  let fechaFinConvocatoriaPeriodoSeguimiento = fechaInicioProyecto
    .plus({ months: mesFinalPeriodoSeguimiento - 1 })
    .set({ hour: 23, minute: 59, second: 59 });

  fechaFinConvocatoriaPeriodoSeguimiento = fechaFinConvocatoriaPeriodoSeguimiento
    .set({ day: fechaFinConvocatoriaPeriodoSeguimiento.daysInMonth });

  if ((!fechaInicioPeriodoSeguimiento || fechaInicioPeriodoSeguimiento < fechaFinProyecto)
    && fechaFinProyecto < fechaFinConvocatoriaPeriodoSeguimiento) {
    fechaFinConvocatoriaPeriodoSeguimiento = fechaFinProyecto;
  }

  return fechaFinConvocatoriaPeriodoSeguimiento;
}
