import { Estado } from '@core/models/pii/reparto';

export interface IRepartoRequest {
  invencionId: number;
  fecha: string;
  importeUniversidad: number;
  importeEquipoInventor: number;
  estado: Estado;
}
