import { ITipoDocumento, ITipoFase } from '../tipos-configuracion';

export interface IConvocatoriaDocumentoBackend {
  id: number;
  convocatoriaId: number;
  nombre: string;
  documentoRef: string;
  tipoFase: ITipoFase;
  tipoDocumento: ITipoDocumento;
  publico: boolean;
  observaciones: string;
}
