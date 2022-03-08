import { IDocumento } from '../documento';

export interface IDatoEconomicoDetalleBackend {
  id: string;
  proyectoId: string;
  partidaPresupuestaria: string;
  codigoEconomico: any;
  anualidad: string;
  documentos: IDocumento[];
  campos: {
    nombre: string;
    valor: string;
  }[];
  clasificacionSGE: any;
  fechaDevengo: string;
}
