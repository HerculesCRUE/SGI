import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IProcedimientoRequest {

  fecha: string;
  tipoProcedimientoId: number;
  solicitudProteccionId: number;
  accionATomar: I18nFieldValueRequest[];
  fechaLimiteAccion: string;
  generarAviso: boolean;
  comentarios: I18nFieldValueRequest[];

}
