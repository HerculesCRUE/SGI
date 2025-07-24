import { TipoJustificacion } from '@core/enums/tipo-justificacion';
import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';


export interface IConvocatoriaPeriodoJustificacionResponse {
  /** Id */
  id: number;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Num Periodo */
  numPeriodo: number;
  /** Mes inicial */
  mesInicial: number;
  /** Mes final */
  mesFinal: number;
  /** Fecha inicio */
  fechaInicioPresentacion: string;
  /** Fecha fin */
  fechaFinPresentacion: string;
  /** Observaciones */
  observaciones: I18nFieldValueResponse[];
  /** Tipo */
  tipo: TipoJustificacion;
}
