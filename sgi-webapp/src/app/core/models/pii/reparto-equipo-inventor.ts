import { IInvencionInventor } from './invencion-inventor';
import { IProyecto } from '../csp/proyecto';
import { IReparto } from './reparto';

export interface IRepartoEquipoInventor {
  id: number;
  reparto: IReparto;
  invencionInventor: IInvencionInventor;
  proyecto: IProyecto;
  importeNomina: number;
  importeProyecto: number;
  importeOtros: number;
}
