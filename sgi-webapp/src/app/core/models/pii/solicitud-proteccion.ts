import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { IEmpresa } from '../sgemp/empresa';
import { IPais } from '../sgo/pais';
import { IInvencion } from './invencion';
import { ITipoCaducidad } from './tipo-caducidad';
import { IViaProteccion } from './via-proteccion';

export interface ISolicitudProteccion {
  id: number;
  invencion: IInvencion;
  titulo: I18nFieldValue[];
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
  comentarios: I18nFieldValue[];
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
