import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";
import { IEstadoGastoProyectoRequest } from "../estado-gasto-proyecto/estado-gasto-proyecto-request";

export interface IGastoProyectoRequest {
  proyectoId: number;
  gastoRef: string;
  conceptoGastoId: number;
  estado: IEstadoGastoProyectoRequest;
  fechaCongreso: string;
  importeInscripcion: number;
  observaciones: I18nFieldValueRequest[];
}
