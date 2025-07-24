
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum TIPO_ACTIVIDAD {
  PROYECTO_INVESTIGACION = 1,
  PRACTICA_DOCENTE = 2,
  INVESTIGACION_TUTELADA = 3
}

export const TIPO_ACTIVIDAD_MAP: Map<TIPO_ACTIVIDAD, string> = new Map([
  [TIPO_ACTIVIDAD.PROYECTO_INVESTIGACION, marker(`eti.tipo-actividad.PROYECTO_INVESTIGACION`)],
  [TIPO_ACTIVIDAD.PRACTICA_DOCENTE, marker(`eti.tipo-actividad.PRACTICA_DOCENTE`)],
  [TIPO_ACTIVIDAD.INVESTIGACION_TUTELADA, marker(`eti.tipo-actividad.INVESTIGACION_TUTELADA`)]
]);

export interface ITipoActividad {

  /** ID */
  id: number;
  /** Nombre */
  nombre: string;
  /** Activo */
  activo: boolean;

}