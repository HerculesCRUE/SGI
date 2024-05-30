import { IMiembroEquipoProyecto } from './miembro-equipo-proyecto';
import { Orden } from './rol-proyecto';

export interface IProyectoEquipo extends IMiembroEquipoProyecto {
  proyectoId: number;
}

export function sortProyectoEquipoByRolProyectoOrden(miembroEquipoA: IProyectoEquipo, miembroEquipoB: IProyectoEquipo): number {
  if (miembroEquipoA.rolProyecto.orden === Orden.PRIMARIO && miembroEquipoB.rolProyecto.orden === Orden.SECUNDARIO) {
    return -1;
  }

  if (miembroEquipoA.rolProyecto.orden === Orden.SECUNDARIO && miembroEquipoB.rolProyecto.orden === Orden.PRIMARIO) {
    return 1;
  }

  return 0;
}

export function sortProyectoEquipoByPersonaNombre(miembroEquipoA: IProyectoEquipo, miembroEquipoB: IProyectoEquipo): number {
  const nombreConceptoGastoItemA = miembroEquipoA?.persona?.nombre ?? '';
  const nombreConceptoGastoItemB = miembroEquipoB?.persona?.nombre ?? '';
  return nombreConceptoGastoItemA.localeCompare(nombreConceptoGastoItemB);
}
