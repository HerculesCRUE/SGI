import { TipoJustificacion } from '@core/enums/tipo-justificacion';


export interface IConvocatoriaPeriodoJustificacionBackend {
  /** Id */
  id: number;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Num Periodo */
  numPeriodo: number;
  /** Mes inicial */
  mesInicial: number;
  /** Mes final */
  mesFinal: number;
  /** Fecha inicio */
  fechaInicioPresentacion: string;
  /** Fecha fin */
  fechaFinPresentacion: string;
  /** Observaciones */
  observaciones: string;
  /** Tipo */
  tipo: TipoJustificacion;
}
