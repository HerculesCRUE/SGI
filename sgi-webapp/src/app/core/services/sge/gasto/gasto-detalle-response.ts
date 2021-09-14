import { ICodigoEconomicoGasto } from '@core/models/sge/codigo-economico-gasto';
import { IDocumento } from '@core/models/sge/documento';

export interface IGastoDetalleResponse {
  id: string;
  proyectoId: string;
  partidaPresupuestaria: string;
  codigoEconomico: ICodigoEconomicoGasto;
  anualidad: string;
  documentos: IDocumento[];
  campos: {
    nombre: string;
    valor: string;
  }[];
}
