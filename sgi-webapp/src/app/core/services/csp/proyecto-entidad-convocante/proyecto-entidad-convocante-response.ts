import { IProgramaResponse } from '../programa/programa-response';

export interface IProyectoEntidadConvocanteResponse {
  id: number;
  entidadRef: string;
  programaConvocatoria: IProgramaResponse;
  programa: IProgramaResponse;
}
