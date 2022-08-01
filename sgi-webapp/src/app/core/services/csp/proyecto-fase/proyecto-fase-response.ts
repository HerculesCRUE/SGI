import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { IProyectoFaseAvisoResponse } from './proyecto-fase-aviso-response';

export interface IProyectoFaseResponse {
  id: number;
  proyectoId: number;
  tipoFase: ITipoFase;
  fechaInicio: string;
  fechaFin: string;
  observaciones: string;
  aviso1: IProyectoFaseAvisoResponse;
  aviso2: IProyectoFaseAvisoResponse;
}