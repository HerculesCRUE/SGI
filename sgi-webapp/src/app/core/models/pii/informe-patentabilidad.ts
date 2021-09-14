import { DateTime } from "luxon";
import { IDocumento } from "../sgdoc/documento";
import { IEmpresa } from "../sgemp/empresa";
import { IInvencion } from "./invencion";
import { IResultadoInformePatentibilidad } from "./resultado-informe-patentabilidad";

export interface IInformePatentabilidad {
  id: number;
  invencion: IInvencion;
  fecha: DateTime;
  nombre: string;
  documento: IDocumento;
  resultadoInformePatentabilidad: IResultadoInformePatentibilidad;
  entidadCreadora: IEmpresa;
  contactoEntidadCreadora: string;
  contactoExaminador: string;
  comentarios: string;
}