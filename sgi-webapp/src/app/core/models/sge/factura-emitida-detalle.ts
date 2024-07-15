import { IDocumento } from './documento';

export interface IFacturaEmitidaDetalle {
  id: string;
  proyectoId: string;
  anualidad: string;
  numeroFactura: string;
  campos: {
    nombre: string;
    valor: string;
  }[];
  documentos: IDocumento[];
}
