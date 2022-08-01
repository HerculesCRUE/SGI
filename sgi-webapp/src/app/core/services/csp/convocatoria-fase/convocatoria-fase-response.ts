import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { IConvocatoriaFaseAvisoResponse } from './convocatoria-fase-aviso-response';

export interface IConvocatoriaFaseResponse {
  id: number;
  convocatoriaId: number;
  tipoFase: ITipoFase;
  fechaInicio: string;
  fechaFin: string;
  observaciones: string;
  aviso1: IConvocatoriaFaseAvisoResponse;
  aviso2: IConvocatoriaFaseAvisoResponse;
}