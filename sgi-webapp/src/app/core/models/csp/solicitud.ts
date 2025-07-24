
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { IPersona } from '../sgp/persona';
import { IUnidadGestion } from '../usr/unidad-gestion';
import { IEstadoSolicitud } from './estado-solicitud';
import { IModeloEjecucion, ITipoFinalidad } from './tipos-configuracion';
import { I18nFieldValue } from '@core/i18n/i18n-field';

export enum TipoSolicitudGrupo {
  CONSTITUCION = 'CONSTITUCION',
  MODIFICACION = 'MODIFICACION'
}

export const TIPO_SOLICITUD_GRUPO_MAP: Map<TipoSolicitudGrupo, string> = new Map([
  [TipoSolicitudGrupo.CONSTITUCION, marker(`csp.tipo-solicitud-grupo.CONSTITUCION`)],
  [TipoSolicitudGrupo.MODIFICACION, marker(`csp.tipo-solicitud-grupo.MODIFICACION`)]
]);

export enum OrigenSolicitud {
  CONVOCATORIA_SGI = 'CONVOCATORIA_SGI',
  CONVOCATORIA_NO_SGI = 'CONVOCATORIA_NO_SGI',
  SIN_CONVOCATORIA = 'SIN_CONVOCATORIA'
}

export const ORIGEN_SOLICITUD_MAP: Map<OrigenSolicitud, string> = new Map([
  [OrigenSolicitud.CONVOCATORIA_SGI, marker(`csp.origen-solicitud.CONVOCATORIA_SGI`)],
  [OrigenSolicitud.CONVOCATORIA_NO_SGI, marker(`csp.origen-solicitud.CONVOCATORIA_NO_SGI`)],
  [OrigenSolicitud.SIN_CONVOCATORIA, marker(`csp.origen-solicitud.SIN_CONVOCATORIA`)]
]);

export interface ISolicitud {

  /** Id */
  id: number;
  /** Título */
  titulo: I18nFieldValue[];
  /** Activo */
  activo: boolean;
  /** Codigo externo */
  codigoExterno: string;
  /** Codigo registro interno */
  codigoRegistroInterno: string;
  /** Estado solicitud */
  estado: IEstadoSolicitud;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Convocatoria externa */
  convocatoriaExterna: string;
  /** Creador */
  creador: IPersona;
  /** Solicitante */
  solicitante: IPersona;
  /** Tipo formulario solicitud */
  formularioSolicitud: FormularioSolicitud;
  /** Tipo formulario solicitud */
  tipoSolicitudGrupo: TipoSolicitudGrupo;
  /** Unidad gestion */
  unidadGestion: IUnidadGestion;
  /** Observaciones */
  observaciones: I18nFieldValue[];
  /** Año */
  anio: number;
  modeloEjecucion: IModeloEjecucion;
  origenSolicitud: OrigenSolicitud;
  tipoFinalidad: ITipoFinalidad;
}
