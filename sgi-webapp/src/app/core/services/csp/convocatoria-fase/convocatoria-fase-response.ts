import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { ITipoFaseResponse } from '../tipo-fase/tipo-fase-response';
import { IConvocatoriaFaseAvisoResponse } from './convocatoria-fase-aviso-response';

export interface IConvocatoriaFaseResponse {
  id: number;
  convocatoriaId: number;
  tipoFase: ITipoFaseResponse;
  fechaInicio: string;
  fechaFin: string;
  observaciones: I18nFieldValueResponse[];
  aviso1: IConvocatoriaFaseAvisoResponse;
  aviso2: IConvocatoriaFaseAvisoResponse;
}