import { ITipoHito } from '@core/models/csp/tipos-configuracion';

export interface IProyectoHitoResponse {
  /** Id */
  id: number;
  /** Fecha inicio  */
  fecha: string;
  /** Tipo de hito */
  tipoHito: ITipoHito;
  /** Comentario */
  comentario: string;
  /** Id de Proyecto */
  proyectoId: number;
  proyectoHitoAviso: {
    comunicadoRef: string;
    tareaProgramadaRef: string;
    incluirIpsProyecto: boolean;
  };
}
