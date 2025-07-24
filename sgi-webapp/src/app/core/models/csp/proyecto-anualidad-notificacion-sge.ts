import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';

export interface IProyectoAnualidadNotificacionSge {
  id: number;
  anio: number;
  proyectoFechaInicio: DateTime;
  proyectoFechaFin: DateTime;
  totalGastos: number;
  totalIngresos: number;
  proyectoId: number;
  proyectoTitulo: I18nFieldValue[];
  proyectoAcronimo: string;
  proyectoEstado: string;
  proyectoSgeRef: string;
  enviadoSge: boolean;
}
