import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IInvencionRequest {
  titulo: I18nFieldValueRequest[];
  fechaComunicacion: string;
  descripcion: I18nFieldValueRequest[];
  comentarios: I18nFieldValueRequest[];
  proyectoRef: string;
  tipoProteccionId: number;
}