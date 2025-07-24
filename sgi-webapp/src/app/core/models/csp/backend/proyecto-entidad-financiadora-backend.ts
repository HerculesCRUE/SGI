import { IFuenteFinanciacionResponse } from '@core/services/csp/fuente-financiacion/fuente-financiacion-response';
import { ITipoFinanciacionResponse } from '@core/services/csp/tipo-financiacion/tipo-financiacion-response';

export interface IProyectoEntidadFinanciadoraBackend {
  id: number;
  entidadRef: string;
  proyectoId: number;
  fuenteFinanciacion: IFuenteFinanciacionResponse;
  tipoFinanciacion: ITipoFinanciacionResponse;
  porcentajeFinanciacion: number;
  importeFinanciacion: number;
  ajena: boolean;
}
