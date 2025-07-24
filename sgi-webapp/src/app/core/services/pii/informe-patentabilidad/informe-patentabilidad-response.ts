import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";
import { IResultadoInformePatentibilidadResponse } from "../resultado-informe-patentabilidad/resultado-informe-patentabilidad-response";

export interface IInformePatentabilidadResponse {
  id: number;
  invencionId: number;
  fecha: string;
  nombre: I18nFieldValueResponse[];
  documentoRef: string;
  resultadoInformePatentabilidad: IResultadoInformePatentibilidadResponse;
  entidadCreadoraRef: string;
  contactoEntidadCreadora: string;
  contactoExaminador: string;
  comentarios: I18nFieldValueResponse[];
}