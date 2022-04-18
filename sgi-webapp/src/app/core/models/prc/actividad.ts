import { DateTime } from 'luxon';
import { CVN_MODO_PARTICIPACION } from '../../../module/prc/shared/cvn/literales-cvn';
import { IProduccionCientifica } from './produccion-cientifica';

export interface IActividad extends IProduccionCientifica {
  fechaInicio: DateTime;
  tituloActividad: string;
}

export enum ModoParticipacion {
  COMISARIO_DE_EXPOSICION = '230',
  ORGANIZADOR = '650',
  ORGANIZATIVO_COMITE = 'ORGANIZATIVO_COMITE',
  ORGANIZATIVO_OTROS = 'ORGANIZATIVO_OTROS',
  ORGANIZATIVO_PRESIDENTE_COMITE = 'ORGANIZATIVO_PRESIDENTE_COMITE',
  OTHERS = 'OTHERS',
  PRESIDENTE = '740',
  SECRETARIO = '830'
}

export const MODO_PARTICIPACION_MAP: Map<ModoParticipacion, string> = new Map([
  [ModoParticipacion.COMISARIO_DE_EXPOSICION, CVN_MODO_PARTICIPACION.COMISARIO_DE_EXPOSICION],
  [ModoParticipacion.ORGANIZADOR, CVN_MODO_PARTICIPACION.ORGANIZADOR],
  [ModoParticipacion.ORGANIZATIVO_COMITE, CVN_MODO_PARTICIPACION.ORGANIZATIVO_COMITE_CIENTIFICO_Y_ORGANIZADOR],
  [ModoParticipacion.ORGANIZATIVO_OTROS, CVN_MODO_PARTICIPACION.ORGANIZATIVO_OTROS],
  [ModoParticipacion.ORGANIZATIVO_PRESIDENTE_COMITE, CVN_MODO_PARTICIPACION.ORGANIZATIVO_PRESIDENTE_COMITE],
  [ModoParticipacion.OTHERS, CVN_MODO_PARTICIPACION.OTROS],
  [ModoParticipacion.PRESIDENTE, CVN_MODO_PARTICIPACION.PRESIDENTE],
  [ModoParticipacion.SECRETARIO, CVN_MODO_PARTICIPACION.SECRETARIO],
]);
