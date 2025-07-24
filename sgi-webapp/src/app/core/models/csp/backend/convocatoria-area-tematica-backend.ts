import { IAreaTematicaResponse } from '@core/services/csp/area-tematica/area-tematica-response';

export interface IConvocatoriaAreaTematicaBackend {
  id: number;
  areaTematica: IAreaTematicaResponse;
  convocatoriaId: number;
  observaciones: string;
}
