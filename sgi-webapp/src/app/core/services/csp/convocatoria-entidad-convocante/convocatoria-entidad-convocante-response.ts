import { IProgramaResponse } from '../programa/programa-response';

export interface IConvocatoriaEntidadConvocanteResponse {
  id: number;
  convocatoriaId: number;
  entidadRef: string;
  programa: IProgramaResponse;
}
