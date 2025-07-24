import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { ITipoFaseResponse } from '../tipo-fase/tipo-fase-response';
import { IProyectoFaseAvisoResponse } from './proyecto-fase-aviso-response';

export interface IProyectoFaseResponse {
  id: number;
  proyectoId: number;
  tipoFase: ITipoFaseResponse;
  fechaInicio: string;
  fechaFin: string;
  observaciones: I18nFieldValueResponse[];
  aviso1: IProyectoFaseAvisoResponse;
  aviso2: IProyectoFaseAvisoResponse;
}