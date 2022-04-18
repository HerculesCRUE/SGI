import { DateTime } from 'luxon';
import { CVN_TIPO_PROYECTO } from 'src/app/module/prc/shared/cvn/literales-cvn';
import { IProduccionCientifica } from './produccion-cientifica';

export interface IDireccionTesis extends IProduccionCientifica {
  fechaDefensa: DateTime;
  tituloTrabajo: string;
}

export enum TipoProyecto {
  OTROS = '010',
  PROYECTO_FINAL_DE_CARRERA = '055',
  TESINA = '066',
  TESIS_DOCTORAL = '067',
  TRABAJO_CONDUCENTE_A_OBTENCION_DE_DEA = '071',
}

export const TIPO_PROYECTO_MAP: Map<TipoProyecto, string> = new Map([
  [TipoProyecto.OTROS, CVN_TIPO_PROYECTO.OTROS],
  [TipoProyecto.PROYECTO_FINAL_DE_CARRERA, CVN_TIPO_PROYECTO.PROYECTO_FINAL_DE_CARRERA],
  [TipoProyecto.TESINA, CVN_TIPO_PROYECTO.TESINA],
  [TipoProyecto.TESIS_DOCTORAL, CVN_TIPO_PROYECTO.TESIS_DOCTORAL],
  [TipoProyecto.TRABAJO_CONDUCENTE_A_OBTENCION_DE_DEA, CVN_TIPO_PROYECTO.TRABAJO_CONDUCENTE_A_OBTENCION_DE_DEA],
]);
