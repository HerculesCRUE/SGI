import { IConvocatoriaFaseAvisoRequest } from "./convocatoria-fase-aviso-request";

export interface IConvocatoriaFaseRequest {
  convocatoriaId: number;
  tipoFaseId: number;
  fechaInicio: string;
  fechaFin: string;
  observaciones: string;
  aviso1: IConvocatoriaFaseAvisoRequest;
  aviso2: IConvocatoriaFaseAvisoRequest;
}
