import { TipoJustificacion } from '@core/enums/tipo-justificacion';
import { I18nFieldValueRequest } from '@core/i18n/i18n-field-request';
import { Estado } from '@core/models/csp/estado-proyecto-periodo-justificacion';

export interface IProyectoPeriodoJustificacionRequest {
  id: number;
  proyectoId: number;
  numPeriodo: number;
  fechaInicio: string;
  fechaFin: string;
  fechaInicioPresentacion: string;
  fechaFinPresentacion: string;
  tipoJustificacion: TipoJustificacion;
  observaciones: I18nFieldValueRequest[];
  convocatoriaPeriodoJustificacionId: number;
  estado: {
    id: number;
    estado: Estado;
    comentario: string;
  };
}
