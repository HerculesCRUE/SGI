import { IConceptoGasto } from '../concepto-gasto';

export interface IConvocatoriaConceptoGastoBackend {
  /** id */
  id: number;
  /** ConceptoGasto */
  conceptoGasto: IConceptoGasto;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Observaciones */
  observaciones: string;
  /** Importe máximo */
  importeMaximo: number;
  /** Porcentaje máximo */
  porcentajeMaximo: number;
  /** Permitido */
  permitido: boolean;
  /** Mes inicial */
  mesInicial: number;
  /** Mes final */
  mesFinal: number;
}
