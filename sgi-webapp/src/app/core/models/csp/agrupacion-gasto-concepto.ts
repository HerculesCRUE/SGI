import { IConceptoGasto } from './concepto-gasto';

export interface IAgrupacionGastoConcepto {
  /** Id */
  id: number;
  /** Proyecto */
  agrupacionId: number;
  /** Nombre */
  conceptoGasto: IConceptoGasto;
}
