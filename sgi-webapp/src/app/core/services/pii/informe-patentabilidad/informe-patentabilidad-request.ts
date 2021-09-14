export interface IInformePatentabilidadRequest {
  invencionId: number;
  fecha: string;
  nombre: string;
  documentoRef: string;
  resultadoInformePatentabilidadId: number;
  entidadCreadoraRef: string;
  contactoEntidadCreadora: string;
  contactoExaminador: string;
  comentarios: string;
}
