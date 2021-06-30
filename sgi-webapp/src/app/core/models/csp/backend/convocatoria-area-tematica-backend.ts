import { IAreaTematica } from '../area-tematica';

export interface IConvocatoriaAreaTematicaBackend {
  id: number;
  areaTematica: IAreaTematica;
  convocatoriaId: number;
  observaciones: string;
}
