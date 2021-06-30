import { IFuenteFinanciacion } from '../fuente-financiacion';
import { ITipoFinanciacion } from '../tipos-configuracion';

export interface IConvocatoriaEntidadFinanciadoraBackend {
  id: number;
  entidadRef: string;
  convocatoriaId: number;
  fuenteFinanciacion: IFuenteFinanciacion;
  tipoFinanciacion: ITipoFinanciacion;
  porcentajeFinanciacion: number;
  importeFinanciacion: number;
}
