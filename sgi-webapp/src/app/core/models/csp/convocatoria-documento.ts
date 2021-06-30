import { ITipoDocumento, ITipoFase } from './tipos-configuracion';

export interface IConvocatoriaDocumento {
  id: number;
  convocatoriaId: number;
  nombre: string;
  documentoRef: string;
  tipoFase: ITipoFase;
  tipoDocumento: ITipoDocumento;
  publico: boolean;
  observaciones: string;
}
