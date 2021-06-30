import { marker } from '@biesbjerg/ngx-translate-extract-marker';


export interface IRolProyecto {
  id: number;
  abreviatura: string;
  nombre: string;
  descripcion: string;
  rolPrincipal: boolean;
  orden: Orden;
  equipo: Equipo;
  activo: boolean;
}


export enum Equipo {
  INVESTIGACION = 'INVESTIGACION',
  TRABAJO = 'TRABAJO'
}

export const EQUIPO_MAP: Map<Equipo, string> = new Map([
  [Equipo.INVESTIGACION, marker('csp.rol-proyecto.equipo.INVESTIGACION')],
  [Equipo.TRABAJO, marker('csp.rol-proyecto.equipo.TRABAJO')],
]);

export enum Orden {
  PRIMARIO = 'PRIMARIO',
  SECUNDARIO = 'SECUNDARIO'
}

export const ORDEN_MAP: Map<Orden, string> = new Map([
  [Orden.PRIMARIO, marker('csp.rol-proyecto.orden.PRIMARIO')],
  [Orden.SECUNDARIO, marker('csp.rol-proyecto.orden.SECUNDARIO')],
]);
