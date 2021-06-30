import { IPrograma } from '../programa';

export interface IConvocatoriaEntidadConvocanteBackend {
  id: number;
  convocatoriaId: number;
  entidadRef: string;
  programa: IPrograma;
}
