import { IInvencionIngreso } from './invencion-ingreso';
import { IReparto } from './reparto';

export interface IRepartoIngreso {
  id: number;
  reparto: IReparto;
  invencionIngreso: IInvencionIngreso;
  importeARepartir: number;
}
