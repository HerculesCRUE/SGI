import { Estado } from '@core/models/pii/reparto';

export interface IRepartoResponse {
  id: number;
  invencionId: number;
  fecha: string;
  importeUniversidad: number;
  importeEquipoInventor: number;
  estado: Estado;
}
