import { IDocumento } from './documento';

export interface IGastoJustificadoDetalle {
  id: string;
  proyectoId: string;
  justificacionId: string;
  campos: {
    nombre: string;
    valor: string;
  }[];
  documentos: IDocumento[];
}
