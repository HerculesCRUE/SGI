import { IAreaTematica } from './area-tematica';

export interface IConvocatoriaAreaTematica {
  id: number;
  areaTematica: IAreaTematica;
  convocatoriaId: number;
  observaciones: string;
}
