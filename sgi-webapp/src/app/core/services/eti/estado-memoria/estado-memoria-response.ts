import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { IMemoriaResponse } from '@core/services/eti/memoria/memoria-response';

export interface IEstadoMemoriaResponse {
  id: number;
  memoria: IMemoriaResponse;
  tipoEstadoMemoria: TipoEstadoMemoria;
  fechaEstado: string;
  comentario: I18nFieldValueResponse[];
}
