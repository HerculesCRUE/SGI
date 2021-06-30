import { ITipoFase } from '../tipos-configuracion';

export interface IProyectoPlazoBackend {
  /** Id */
  id: number;
  /** Id de Proyecto */
  proyectoId: number;
  /** Tipo de hito */
  tipoFase: ITipoFase;
  /** Fecha inicio */
  fechaInicio: string;
  /** Fecha fin  */
  fechaFin: string;
  /** Observaciones */
  observaciones: string;
  /** Aviso */
  generaAviso: boolean;
}
