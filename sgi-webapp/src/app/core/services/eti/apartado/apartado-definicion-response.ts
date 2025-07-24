import { SgiFormlyFieldConfig } from "@formly-forms/formly-field-config";

export interface IApartadoDefinicionResponse {
  /** Language */
  lang: string;
  /** Nombre */
  nombre: string;
  /** Esquema */
  esquema: SgiFormlyFieldConfig[];
}
