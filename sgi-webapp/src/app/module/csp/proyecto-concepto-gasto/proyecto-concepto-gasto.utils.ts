import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { IProyectoConceptoGastoCodigoEc } from '@core/models/csp/proyecto-concepto-gasto-codigo-ec';
import { DateTime } from 'luxon';

export function compareConceptoGastoCodigoEc(
  convocatoriaCodigoEconomico: IConvocatoriaConceptoGastoCodigoEc,
  proyectoCodigoEconomico: IProyectoConceptoGastoCodigoEc): boolean {

  return proyectoCodigoEconomico?.codigoEconomico?.id !== convocatoriaCodigoEconomico?.codigoEconomico.id
    || proyectoCodigoEconomico?.fechaInicio?.toMillis() !== convocatoriaCodigoEconomico?.fechaInicio?.toMillis()
    || proyectoCodigoEconomico?.fechaFin?.toMillis() !== convocatoriaCodigoEconomico?.fechaFin?.toMillis()
    || (proyectoCodigoEconomico.observaciones ?? '') !== (convocatoriaCodigoEconomico?.observaciones ?? '');
}

export function compareConceptoGasto(
  convocatoriaConceptoGasto: IConvocatoriaConceptoGasto,
  proyectoConceptoGasto: IProyectoConceptoGasto,
  fechaInicioProyecto: DateTime,
  fechaFinProyecto: DateTime): boolean {

  let fechaInicioConvocatoriaConceptoGasto: DateTime;
  if (convocatoriaConceptoGasto?.mesInicial) {
    fechaInicioConvocatoriaConceptoGasto =
      getFechaInicioConceptoGasto(fechaInicioProyecto, convocatoriaConceptoGasto.mesInicial);
  }

  let fechaFinConvocatoriaConceptoGasto: DateTime;
  if (convocatoriaConceptoGasto?.mesFinal) {
    fechaFinConvocatoriaConceptoGasto = getFechaFinConceptoGasto(fechaInicioProyecto, fechaFinProyecto,
      convocatoriaConceptoGasto.mesFinal, fechaInicioConvocatoriaConceptoGasto);
  }

  return proyectoConceptoGasto.conceptoGasto?.id !== convocatoriaConceptoGasto?.conceptoGasto.id
    || proyectoConceptoGasto.importeMaximo !== convocatoriaConceptoGasto?.importeMaximo
    || proyectoConceptoGasto.fechaInicio?.toMillis() !== fechaInicioConvocatoriaConceptoGasto?.toMillis()
    || proyectoConceptoGasto.fechaFin?.toMillis() !== fechaFinConvocatoriaConceptoGasto?.toMillis()
    || (proyectoConceptoGasto.observaciones ?? '') !== (convocatoriaConceptoGasto?.observaciones ?? '');
}

/**
 * Obtiene la fecha resultante de sumarle a la fecha de inicio del proyecto el numero de meses de mesInicialConceptoGasto,
 * si la fecha de inicio del proyecto es mayor que la resultante para el concepto devuelve la del proyecto.
 *
 * @param fechaInicioProyecto fecha de inicio del proyecto
 * @param mesInicialConceptoGasto mes inicial del concepto de gasto en la convocatoria
 * @returns la fecha de inicio
 */
export function getFechaInicioConceptoGasto(fechaInicioProyecto: DateTime, mesInicialConceptoGasto: number): DateTime {
  if (!fechaInicioProyecto) {
    return;
  }

  return fechaInicioProyecto.plus({ months: mesInicialConceptoGasto - 1 });
}

/**
 * Obtiene la fecha de fin del concepto gasto.
 *  - Si la fecha resultante de sumar el numero de meses a la fecha de inicio del proyecto
 *    esta dentro del rango del proyecto o la fecha inicio del concepto gasto es posterior a la fecha de fin del proyecto,
 *   el ultimo dia del mes resultante de la suma a las 23:59:59
 *  - Si la fecha de fin es posterior a la fecha de fin del proyecto y la fecha de inicio del concepto gasto esta dentro del proyecto,
 *    la fecha de fin del proyecto
 *
 * @param fechaInicioProyecto fecha de inicio del proyecto
 * @param fechaFinProyecto  fecha de fin del proyecto
 * @param mesFinalConceptoGasto mes final del concepto de gasto en la convocatoria
 * @param fechaInicioConceptoGasto fecha de inicio del concepto de gasto
 * @returns la fecha de fin
 */
export function getFechaFinConceptoGasto(
  fechaInicioProyecto: DateTime, fechaFinProyecto: DateTime,
  mesFinalConceptoGasto: number, fechaInicioConceptoGasto: DateTime
): DateTime {

  if (!fechaInicioProyecto) {
    return;
  }

  let fechaFinConvocatoriaConceptoGasto = fechaInicioProyecto
    .plus({ months: mesFinalConceptoGasto })
    .minus({ seconds: 1 });

  if (fechaFinProyecto != null && (!fechaInicioConceptoGasto || fechaInicioConceptoGasto < fechaFinProyecto) && fechaFinProyecto < fechaFinConvocatoriaConceptoGasto) {
    fechaFinConvocatoriaConceptoGasto = fechaFinProyecto;
  }

  return fechaFinConvocatoriaConceptoGasto;
}
