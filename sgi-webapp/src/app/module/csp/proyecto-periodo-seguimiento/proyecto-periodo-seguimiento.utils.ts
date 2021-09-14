import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
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
      convocatoriaPeriodoSeguimiento.mesFinal);
  }

  return convocatoriaPeriodoSeguimiento.numPeriodo !== convocatoriaPeriodoSeguimiento.numPeriodo
    || proyectoPeriodoSeguimiento.tipoSeguimiento !== convocatoriaPeriodoSeguimiento.tipoSeguimiento
    || proyectoPeriodoSeguimiento.fechaInicio?.toMillis() !== fechaInicioConvocatoriaPeriodoSeguimiento?.toMillis()
    || proyectoPeriodoSeguimiento.fechaFin?.toMillis() !== fechaFinConvocatoriaConceptoGasto?.toMillis()
    || proyectoPeriodoSeguimiento.fechaInicioPresentacion?.toMillis() !== convocatoriaPeriodoSeguimiento.fechaInicioPresentacion?.toMillis()
    || proyectoPeriodoSeguimiento.fechaFinPresentacion?.toMillis() !== convocatoriaPeriodoSeguimiento.fechaFinPresentacion?.toMillis()
    || (proyectoPeriodoSeguimiento.observaciones !== convocatoriaPeriodoSeguimiento.observaciones);
}

export function comparePeriodoJustificacion(
  convocactoriaPeriodoJustificacion: IConvocatoriaPeriodoJustificacion,
  proyectoPeriodojustificacion: IProyectoPeriodoJustificacion,
  fechaInicioProyecto: DateTime,
  fechaFinProyecto: DateTime): boolean {

  let fechaInicioConvocatoriaPeriodoSeguimiento: DateTime;
  if (convocactoriaPeriodoJustificacion.mesInicial) {
    fechaInicioConvocatoriaPeriodoSeguimiento =
      getFechaInicioPeriodoSeguimiento(fechaInicioProyecto, convocactoriaPeriodoJustificacion.mesInicial);
  }

  let fechaFinConvocatoriaConceptoGasto: DateTime;
  if (convocactoriaPeriodoJustificacion.mesFinal) {
    fechaFinConvocatoriaConceptoGasto = getFechaFinPeriodoSeguimiento(fechaInicioProyecto, fechaFinProyecto,
      convocactoriaPeriodoJustificacion.mesFinal);
  }

  return convocactoriaPeriodoJustificacion.numPeriodo !== convocactoriaPeriodoJustificacion.numPeriodo
    || proyectoPeriodojustificacion.tipoJustificacion !== convocactoriaPeriodoJustificacion.tipo
    || proyectoPeriodojustificacion.fechaInicio?.toMillis() !== fechaInicioConvocatoriaPeriodoSeguimiento?.toMillis()
    || proyectoPeriodojustificacion.fechaFin?.toMillis() !== fechaFinConvocatoriaConceptoGasto?.toMillis()
    || proyectoPeriodojustificacion.fechaInicioPresentacion?.toMillis() !==
    convocactoriaPeriodoJustificacion.fechaInicioPresentacion?.toMillis()
    || proyectoPeriodojustificacion.fechaFinPresentacion?.toMillis() !== convocactoriaPeriodoJustificacion.fechaFinPresentacion?.toMillis()
    || (proyectoPeriodojustificacion.observaciones !== convocactoriaPeriodoJustificacion.observaciones);
}



/**
 * Obtiene la fecha de inicio del periodo seguimiento.
 *   - A la fecha de inicio del proyecto se suma el numero de meses correspondiente (menos uno)
 *
 * @param fechaInicioProyecto fecha de inicio del proyecto
 * @param mesInicialPeriodoSeguimiento mes inicial del periodo seguimiento en la convocatoria
 * @returns la fecha de inicio
 */
export function getFechaInicioPeriodoSeguimiento(fechaInicioProyecto: DateTime, mesInicialPeriodoSeguimiento: number): DateTime {
  return fechaInicioProyecto.plus({ months: mesInicialPeriodoSeguimiento - 1 });
}

/**
 * Obtiene la fecha de fin del periodo seguimiento.
 *  - A la fecha de inicio del proyecto se suma el numero de meses correspondiente y se resta un segundo (para que sea el día anterior)
 *  - Si la fecha de fin es posterior a la fecha de fin del proyecto se usa la fecha de fin del proyecto
 *
 * @param fechaInicioProyecto fecha de inicio del proyecto
 * @param fechaFinProyecto  fecha de fin del proyecto
 * @param mesFinalPeriodoSeguimiento mes final del concepto de gasto en la convocatoria
 * @returns la fecha de fin
 */
export function getFechaFinPeriodoSeguimiento(
  fechaInicioProyecto: DateTime, fechaFinProyecto: DateTime,
  mesFinalPeriodoSeguimiento: number): DateTime {

  let fechaFinConvocatoriaPeriodoSeguimiento = fechaInicioProyecto
    .plus({ months: mesFinalPeriodoSeguimiento })
    .plus({ second: -1 });

  if (fechaFinProyecto != null && fechaFinConvocatoriaPeriodoSeguimiento > fechaFinProyecto) {
    fechaFinConvocatoriaPeriodoSeguimiento = fechaFinProyecto;
  }

  return fechaFinConvocatoriaPeriodoSeguimiento;
}
