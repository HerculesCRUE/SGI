import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IConceptoGasto } from './concepto-gasto';

export interface IConvocatoriaConceptoGasto {
  /** id */
  id: number;
  /** ConceptoGasto */
  conceptoGasto: IConceptoGasto;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Observaciones */
  observaciones: I18nFieldValue[];
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
