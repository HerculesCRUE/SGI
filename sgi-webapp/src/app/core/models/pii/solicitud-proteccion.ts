import { DateTime } from 'luxon';
import { IInvencion } from './invencion';
import { IViaProteccion } from './via-proteccion';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ITipoCaducidad } from './tipo-caducidad';
import { IPais } from '../sgo/pais';
import { IEmpresa } from '../sgemp/empresa';

export interface ISolicitudProteccion {
  id: number;
  invencion: IInvencion;
  titulo: string;
  fechaPrioridadSolicitud: DateTime;
  fechaFinPriorPresFasNacRec: DateTime;
  fechaPublicacion: DateTime;
  fechaConcesion: DateTime;
  fechaCaducidad: DateTime;
  viaProteccion: IViaProteccion;
  numeroSolicitud: string;
  numeroPublicacion: string;
  numeroConcesion: string;
  numeroRegistro: string;
  estado: Estado;
  tipoCaducidad: ITipoCaducidad;
  agentePropiedad: IEmpresa;
  paisProteccion: IPais;
  comentarios: string;
  activo: boolean;
}

export enum Estado {

  SOLICITADA = 'SOLICITADA',
  PUBLICADA = 'PUBLICADA',
  CONCEDIDA = 'CONCEDIDA',
  CADUCADA = 'CADUCADA'
}

export const ESTADO_MAP: Map<Estado, string> = new Map([
  [Estado.SOLICITADA, marker('pii.solicitud-proteccion.estado.SOLICITADA')],
  [Estado.PUBLICADA, marker('pii.solicitud-proteccion.estado.PUBLICADA')],
  [Estado.CONCEDIDA, marker('pii.solicitud-proteccion.estado.CONCEDIDA')],
  [Estado.CADUCADA, marker('pii.solicitud-proteccion.estado.CADUCADA')]
]);
