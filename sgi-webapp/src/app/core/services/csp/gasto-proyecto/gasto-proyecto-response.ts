import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { Estado } from '@core/models/csp/estado-gasto-proyecto';
import { IConceptoGastoResponse } from '../concepto-gasto/concepto-gasto-response';

export interface IGastoProyectoResponse {
  id: number;
  proyectoId: number;
  gastoRef: string;
  conceptoGasto: IConceptoGastoResponse;
  estado: {
    id: number;
    estado: Estado;
    fechaEstado: string;
    comentario: I18nFieldValueResponse[];
  };
  fechaCongreso: string;
  importeInscripcion: number;
  observaciones: I18nFieldValueResponse[];
}
