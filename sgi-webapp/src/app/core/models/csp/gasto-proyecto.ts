import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { IConceptoGasto } from './concepto-gasto';
import { IEstadoGastoProyecto } from './estado-gasto-proyecto';

export interface IGastoProyecto {
  id: number;
  proyectoId: number;
  gastoRef: string;
  conceptoGasto: IConceptoGasto;
  estado: IEstadoGastoProyecto;
  fechaCongreso: DateTime;
  importeInscripcion: number;
  observaciones: I18nFieldValue[];
}
