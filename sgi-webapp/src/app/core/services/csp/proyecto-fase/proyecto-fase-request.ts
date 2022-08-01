import { IProyectoFaseAvisoRequest } from "./proyecto-fase-aviso-request";

export interface IProyectoFaseRequest {
  proyectoId: number;
  tipoFaseId: number;
  fechaInicio: string;
  fechaFin: string;
  observaciones: string;
  aviso1: IProyectoFaseAvisoRequest;
  aviso2: IProyectoFaseAvisoRequest;
}
