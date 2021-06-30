import { ITipoHito } from '../tipos-configuracion';

export interface IConvocatoriaHitoBackend {
  /** Id */
  id: number;
  /** Fecha inicio  */
  fecha: string;
  /** Tipo de hito */
  tipoHito: ITipoHito;
  /** Comentario */
  comentario: string;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Aviso */
  generaAviso: boolean;
}
