import { IResumenPuntuacionGrupo } from './resumen-puntuacion-grupo';

export interface IResumenPuntuacionGrupoAnio {
  anio: number;
  puntuacionesGrupos: IResumenPuntuacionGrupo[];
}
