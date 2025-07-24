import { I18nFieldValue } from "@core/i18n/i18n-field";
import { DateTime } from "luxon";
import { IDocumento } from "../sgdoc/documento";
import { IEmpresa } from "../sgemp/empresa";
import { IInvencion } from "./invencion";
import { IResultadoInformePatentibilidad } from "./resultado-informe-patentabilidad";

export interface IInformePatentabilidad {
  id: number;
  invencion: IInvencion;
  fecha: DateTime;
  nombre: I18nFieldValue[];
  documento: IDocumento;
  resultadoInformePatentabilidad: IResultadoInformePatentibilidad;
  entidadCreadora: IEmpresa;
  contactoEntidadCreadora: string;
  contactoExaminador: string;
  comentarios: I18nFieldValue[];
}