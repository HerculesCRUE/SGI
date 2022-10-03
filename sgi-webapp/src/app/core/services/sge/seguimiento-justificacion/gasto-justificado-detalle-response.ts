export interface IGastoJustificadoDetalleResponse {
  id: string;
  proyectoId: string;
  justificacionId: string;
  campos: {
    nombre: string;
    valor: string;
  }[];
  documentos: {
    id: string;
    nombre: string;
    nombreFichero: string;
  }[];
}
