import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { IEstadoProyecto } from '@core/models/csp/estado-proyecto';

export interface IProyectoAnualidadNotificacionSgeResponse {
  id: number;
  anio: number;
  proyectoFechaInicio: string;
  proyectoFechaFin: string;
  totalGastos: number;
  totalIngresos: number;
  proyectoId: number;
  proyectoTitulo: I18nFieldValueResponse[];
  proyectoAcronimo: string;
  proyectoEstado: IEstadoProyecto;
  proyectoSgeRef: string;
  enviadoSge: boolean;
}
