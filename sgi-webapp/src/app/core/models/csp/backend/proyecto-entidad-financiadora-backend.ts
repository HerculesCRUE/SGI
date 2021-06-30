import { IFuenteFinanciacion } from '../fuente-financiacion';
import { ITipoFinanciacion } from '../tipos-configuracion';

export interface IProyectoEntidadFinanciadoraBackend {
  id: number;
  entidadRef: string;
  proyectoId: number;
  fuenteFinanciacion: IFuenteFinanciacion;
  tipoFinanciacion: ITipoFinanciacion;
  porcentajeFinanciacion: number;
  importeFinanciacion: number;
  ajena: boolean;
}
