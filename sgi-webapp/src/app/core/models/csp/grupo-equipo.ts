import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { IGrupo } from './grupo';
import { IRolProyecto, Orden } from './rol-proyecto';

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

export const PARTICIPACION_MAX = 100;

export function sortGrupoEquipoByRolProyectoOrden(miembroEquipoA: IGrupoEquipo, miembroEquipoB: IGrupoEquipo): number {
  if (miembroEquipoA.rol.orden === Orden.PRIMARIO && miembroEquipoB.rol.orden === Orden.SECUNDARIO) {
    return -1;
  }

  if (miembroEquipoA.rol.orden === Orden.SECUNDARIO && miembroEquipoB.rol.orden === Orden.PRIMARIO) {
    return 1;
  }

  return 0;
}

export function sortGrupoEquipoByPersonaNombre(miembroEquipoA: IGrupoEquipo, miembroEquipoB: IGrupoEquipo): number {
  const nombreConceptoGastoItemA = miembroEquipoA?.persona?.nombre ?? '';
  const nombreConceptoGastoItemB = miembroEquipoB?.persona?.nombre ?? '';
  return nombreConceptoGastoItemA.localeCompare(nombreConceptoGastoItemB);
}
