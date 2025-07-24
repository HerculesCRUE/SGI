import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IInformePatentabilidadRequest {
  invencionId: number;
  fecha: string;
  nombre: I18nFieldValueRequest[];
  documentoRef: string;
  resultadoInformePatentabilidadId: number;
  entidadCreadoraRef: string;
  contactoEntidadCreadora: string;
  contactoExaminador: string;
  comentarios: I18nFieldValueRequest[];
}
