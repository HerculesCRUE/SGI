import { IConceptoGasto } from './concepto-gasto';

export interface IPartidaGasto {
  id: number;
  conceptoGasto: IConceptoGasto;
  anualidad: number;
  importeSolicitado: number;
  observaciones: string;
}
