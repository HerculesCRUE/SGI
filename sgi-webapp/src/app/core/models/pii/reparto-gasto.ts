import { IInvencionGasto } from './invencion-gasto';
import { IReparto } from './reparto';

export interface IRepartoGasto {
  id: number;
  reparto: IReparto;
  invencionGasto: IInvencionGasto;
  importeADeducir: number;
}
