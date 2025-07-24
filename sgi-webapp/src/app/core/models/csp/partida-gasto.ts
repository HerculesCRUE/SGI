import { IConceptoGasto } from './concepto-gasto';
import { I18nFieldValue } from '@core/i18n/i18n-field';

export interface IPartidaGasto {
  id: number;
  conceptoGasto: IConceptoGasto;
  anualidad: number;
  importeSolicitado: number;
  observaciones: I18nFieldValue[];
}
