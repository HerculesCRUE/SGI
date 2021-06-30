import { IEntidad } from './entidad';
import { IFuenteFinanciacion } from './fuente-financiacion';
import { ITipoFinanciacion } from './tipos-configuracion';

export interface IEntidadFinanciadora extends IEntidad {
  /**
   * Fuente de financiación
   */
  fuenteFinanciacion: IFuenteFinanciacion;
  /**
   * Tipo de financiación
   */
  tipoFinanciacion: ITipoFinanciacion;
  /**
   * Porcentaje de financiación
   */
  porcentajeFinanciacion: number;
  /**
   * Importe de financiación
   */
  importeFinanciacion: number;
}
