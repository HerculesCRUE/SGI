import { Language } from '@core/i18n/language';

export interface IInformeDocumento {
  /** Referencia documento */
  documentoRef: string;
  /** id informe */
  informeId: number;
  /** Language */
  lang: Language;
}
