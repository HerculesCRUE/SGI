import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IDatoEconomico } from '../sgepii/dato-economico';
import { IInvencion } from './invencion';
import { ISolicitudProteccion } from './solicitud-proteccion';

export interface IInvencionGasto {
  id: number;
  invencion: IInvencion;
  solicitudProteccion: ISolicitudProteccion;
  gasto: IDatoEconomico;
  importePendienteDeducir: number;
  estado: Estado;
}

export enum Estado {
  NO_DEDUCIDO = 'NO_DEDUCIDO',
  DEDUCIDO_PARCIALMENTE = 'DEDUCIDO_PARCIALMENTE',
  DEDUCIDO = 'DEDUCIDO'
}

export const ESTADO_MAP: Map<Estado, string> = new Map([
  [Estado.NO_DEDUCIDO, marker('pii.invencion-gasto.estado.NO_DEDUCIDO')],
  [Estado.DEDUCIDO_PARCIALMENTE, marker('pii.invencion-gasto.estado.DEDUCIDO_PARCIALMENTE')],
  [Estado.DEDUCIDO, marker('pii.invencion-gasto.estado.DEDUCIDO')]
]);
