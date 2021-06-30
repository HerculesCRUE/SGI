import { IFuenteFinanciacion } from '../fuente-financiacion';
import { ITipoFinanciacion } from '../tipos-configuracion';

export interface ISolicitudProyectoEntidadFinanciadoraAjenaBackend {
  id: number;
  entidadRef: string;
  solicitudProyectoId: number;
  fuenteFinanciacion: IFuenteFinanciacion;
  tipoFinanciacion: ITipoFinanciacion;
  porcentajeFinanciacion: number;
  importeFinanciacion: number;
}
