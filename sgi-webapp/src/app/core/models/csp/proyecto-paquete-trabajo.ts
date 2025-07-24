import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';

export interface IProyectoPaqueteTrabajo {
  /** Id */
  id: number;
  /** Id de Proyecto */
  proyectoId: number;
  /** nombre */
  nombre: string;
  /** fechaInicio */
  fechaInicio: DateTime;
  /** fechaFin */
  fechaFin: DateTime;
  /** personaMes */
  personaMes: number;
  /** descripcion */
  descripcion: I18nFieldValue[];
}
