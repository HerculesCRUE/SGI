import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';
import { IInvencion } from './invencion';
import { IRepartoGasto } from './reparto-gasto';
import { IRepartoIngreso } from './reparto-ingreso';

export interface IReparto {
  id: number;
  invencion: IInvencion;
  fecha: DateTime;
  importeUniversidad: number;
  importeEquipoInventor: number;
  estado: Estado;
}

export interface IRepartoCreate {
  invencion: IInvencion;
  gastos: IRepartoGasto[];
  ingresos: IRepartoIngreso[];
}

export enum Estado {
  PENDIENTE_EJECUTAR = 'PENDIENTE_EJECUTAR',
  EJECUTADO = 'EJECUTADO'
}

export const ESTADO_MAP: Map<Estado, string> = new Map([
  [Estado.PENDIENTE_EJECUTAR, marker('pii.invencion-reparto.estado.PENDIENTE_EJECUTAR')],
  [Estado.EJECUTADO, marker('pii.invencion-reparto.estado.EJECUTADO')],
]);
