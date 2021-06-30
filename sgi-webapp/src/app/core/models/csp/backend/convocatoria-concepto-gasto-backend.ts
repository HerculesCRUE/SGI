import { IConceptoGasto } from '../tipos-configuracion';

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
  /** Permitido */
  permitido: boolean;
  /** Mes inicial */
  mesInicial: number;
  /** Mes final */
  mesFinal: number;
  /** Porcentaje coste indirecto */
  porcentajeCosteIndirecto: number;
}
