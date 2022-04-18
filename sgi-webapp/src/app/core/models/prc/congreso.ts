import { DateTime } from 'luxon';
import { CVN_TIPO_EVENTO } from 'src/app/module/prc/shared/cvn/literales-cvn';
import { IProduccionCientifica } from './produccion-cientifica';

export interface ICongreso extends IProduccionCientifica {
  fechaCelebracion: DateTime;
  tipoEvento: TipoEvento;
  tituloTrabajo: string;
}

export enum TipoEvento {
  CONGRESO = '008',
  JORNADA = '031',
  SEMINARIO = '063',
  OTROS = 'OTHERS',
}

export const TIPO_EVENTO_MAP: Map<TipoEvento, string> = new Map([
  [TipoEvento.CONGRESO, CVN_TIPO_EVENTO.CONGRESO],
  [TipoEvento.JORNADA, CVN_TIPO_EVENTO.JORNADA],
  [TipoEvento.SEMINARIO, CVN_TIPO_EVENTO.SEMINARIO],
  [TipoEvento.OTROS, CVN_TIPO_EVENTO.OTROS],
]);
