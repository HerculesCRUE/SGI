import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ClasificacionCVN } from '@core/enums/clasificacion-cvn';
import { DateTime } from 'luxon';
import { IUnidadGestion } from '../usr/unidad-gestion';
import { ITipoAmbitoGeografico } from './tipo-ambito-geografico';
import { ITipoRegimenConcurrencia } from './tipo-regimen-concurrencia';
import { IModeloEjecucion, ITipoFinalidad } from './tipos-configuracion';

export interface IConvocatoria {
  id: number;
  unidadGestion: IUnidadGestion;
  modeloEjecucion: IModeloEjecucion;
  codigo: string;
  fechaPublicacion: DateTime;
  fechaProvisional: DateTime;
  fechaConcesion: DateTime;
  titulo: string;
  objeto: string;
  observaciones: string;
  finalidad: ITipoFinalidad;
  regimenConcurrencia: ITipoRegimenConcurrencia;
  estado: Estado;
  duracion: number;
  abiertoPlazoPresentacionSolicitud: boolean;
  ambitoGeografico: ITipoAmbitoGeografico;
  clasificacionCVN: ClasificacionCVN;
  activo: boolean;
}

export enum Estado {
  BORRADOR = 'BORRADOR',
  REGISTRADA = 'REGISTRADA'
}

export const ESTADO_MAP: Map<Estado, string> = new Map([
  [Estado.BORRADOR, marker(`csp.convocatoria.estado.BORRADOR`)],
  [Estado.REGISTRADA, marker(`csp.convocatoria.estado.REGISTRADA`)]
]);
