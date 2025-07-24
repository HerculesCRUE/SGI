import { I18nFieldValueRequest } from '@core/i18n/i18n-field-request';
import { MemoriaTipo } from '@core/models/eti/memoria';

export interface IMemoriaRequest {
  /** Peticion Evaluacion */
  peticionEvaluacionId: number;
  /** Comité */
  comiteId: number;
  /** Título */
  titulo: I18nFieldValueRequest[];
  /** Referencia persona responsable */
  responsableRef: string;
  /** Tipo Memoria */
  tipo: MemoriaTipo;
}
