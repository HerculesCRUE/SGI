import { IResultadoInformePatentibilidad } from "@core/models/pii/resultado-informe-patentabilidad";

export interface IInformePatentabilidadResponse {
  id: number;
  invencionId: number;
  fecha: string;
  nombre: string;
  documentoRef: string;
  resultadoInformePatentabilidad: IResultadoInformePatentibilidad;
  entidadCreadoraRef: string;
  contactoEntidadCreadora: string;
  contactoExaminador: string;
  comentarios: string;
}