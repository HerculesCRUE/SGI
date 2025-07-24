
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum TIPO_INVESTIGACION_TUTELADA {
  TESIS_DOCTORAL = 1,
  TRABAJO_FIN_MASTER = 2,
  TRABAJO_FIN_GRADO = 3
}

export const TIPO_INVESTIGACION_TUTELADA_MAP: Map<TIPO_INVESTIGACION_TUTELADA, string> = new Map([
  [TIPO_INVESTIGACION_TUTELADA.TESIS_DOCTORAL, marker(`eti.tipo-investigacion-tutelada.TESIS_DOCTORAL`)],
  [TIPO_INVESTIGACION_TUTELADA.TRABAJO_FIN_MASTER, marker(`eti.tipo-investigacion-tutelada.TRABAJO_FIN_MASTER`)],
  [TIPO_INVESTIGACION_TUTELADA.TRABAJO_FIN_GRADO, marker(`eti.tipo-investigacion-tutelada.TRABAJO_FIN_GRADO`)]
]);

export interface ITipoInvestigacionTutelada {
  /** ID */
  id: number;
  /** Nombre */
  nombre: string;
  /** Activo */
  activo: boolean;
}
