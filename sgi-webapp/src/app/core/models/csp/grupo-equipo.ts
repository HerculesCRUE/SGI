import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { IGrupo } from './grupo';
import { IRolProyecto } from './rol-proyecto';

export interface IGrupoEquipo {
  id: number;
  persona: IPersona;
  grupo: IGrupo;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  rol: IRolProyecto;
  dedicacion: Dedicacion;
  participacion: number;
}

export enum Dedicacion {
  /** Completa */
  COMPLETA = 'COMPLETA',
  /** Parcial */
  PARCIAL = 'PARCIAL'
}

export const DEDICACION_MAP: Map<Dedicacion, string> = new Map([
  [Dedicacion.COMPLETA, marker(`csp.grupo-equipo.dedicacion.COMPLETA`)],
  [Dedicacion.PARCIAL, marker(`csp.grupo-equipo.dedicacion.PARCIAL`)]
]);
