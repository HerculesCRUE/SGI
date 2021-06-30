import { IPrograma } from '../programa';

export interface IProyectoEntidadConvocanteBackend {
  id: number;
  entidadRef: string;
  programaConvocatoria: IPrograma;
  programa: IPrograma;
}
