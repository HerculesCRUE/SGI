import { ITipoHito } from '../tipos-configuracion';

export interface IProyectoHitoBackend {
  /** Id */
  id: number;
  /** Id de Proyecto */
  proyectoId: number;
  /** Tipo de hito */
  tipoHito: ITipoHito;
  /** Fecha  */
  fecha: string;
  /** Comentario */
  comentario: string;
  /** Aviso */
  generaAviso: boolean;
}
