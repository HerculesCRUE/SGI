import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IDatoEconomico } from '../sgepii/dato-economico';
import { IInvencion } from './invencion';

export interface IInvencionIngreso {
  id: number;
  invencion: IInvencion;
  ingreso: IDatoEconomico;
  importePendienteRepartir: number;
  estado: Estado;
}

export enum Estado {
  NO_REPARTIDO = 'NO_REPARTIDO',
  REPARTIDO_PARCIALMENTE = 'REPARTIDO_PARCIALMENTE',
  REPARTIDO = 'REPARTIDO'
}

export const ESTADO_MAP: Map<Estado, string> = new Map([
  [Estado.NO_REPARTIDO, marker('pii.invencion-ingreso.estado.NO_REPARTIDO')],
  [Estado.REPARTIDO_PARCIALMENTE, marker('pii.invencion-ingreso.estado.REPARTIDO_PARCIALMENTE')],
  [Estado.REPARTIDO, marker('pii.invencion-ingreso.estado.REPARTIDO')]
]);