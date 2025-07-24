import { Language } from "@core/i18n/language";

export interface IActaDocumento {
  /** Referencia documento */
  documentoRef: string;
  /** id acta */
  actaId: number;
  /** Referencia a la transacción blockchain */
  transaccionRef: string;
  /** Language */
  lang: Language;
}
