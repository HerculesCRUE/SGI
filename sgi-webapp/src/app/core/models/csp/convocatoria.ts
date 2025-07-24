import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ClasificacionCVN } from '@core/enums/clasificacion-cvn';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { IUnidadGestion } from '../usr/unidad-gestion';
import { IModeloEjecucion, ITipoAmbitoGeografico, ITipoFinalidad, ITipoRegimenConcurrencia } from './tipos-configuracion';

export interface IConvocatoria {
  id: number;
  unidadGestion: IUnidadGestion;
  modeloEjecucion: IModeloEjecucion;
  codigo: string;
  fechaPublicacion: DateTime;
  fechaProvisional: DateTime;
  fechaConcesion: DateTime;
  titulo: I18nFieldValue[];
  objeto: I18nFieldValue[];
  observaciones: I18nFieldValue[];
  finalidad: ITipoFinalidad;
  regimenConcurrencia: ITipoRegimenConcurrencia;
  formularioSolicitud: FormularioSolicitud;
  estado: Estado;
  duracion: number;
  abiertoPlazoPresentacionSolicitud: boolean;
  ambitoGeografico: ITipoAmbitoGeografico;
  clasificacionCVN: ClasificacionCVN;
  activo: boolean;
  excelencia: boolean;
  anio: number;
  codigoInterno: string;
}

export enum Estado {
  BORRADOR = 'BORRADOR',
  REGISTRADA = 'REGISTRADA'
}

export const ESTADO_MAP: Map<Estado, string> = new Map([
  [Estado.BORRADOR, marker(`csp.convocatoria.estado.BORRADOR`)],
  [Estado.REGISTRADA, marker(`csp.convocatoria.estado.REGISTRADA`)]
]);
