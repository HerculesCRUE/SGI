import { TipoPartida } from '@core/enums/tipo-partida';

export interface IConvocatoriaPartidaPresupuestariaBackend {
  /** Id */
  id: number;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Código  */
  codigo: string;
  /** Descripción */
  descripcion: string;
  /** Tipo de partida */
  tipoPartida: TipoPartida;
}
