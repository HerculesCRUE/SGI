
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { IPersona } from '../sgp/persona';
import { IUnidadGestion } from '../usr/unidad-gestion';
import { IEstadoSolicitud } from './estado-solicitud';

export enum TipoSolicitudGrupo {
  CONSTITUCION = 'CONSTITUCION',
  MODIFICACION = 'MODIFICACION'
}

export const TIPO_SOLICITUD_GRUPO_MAP: Map<TipoSolicitudGrupo, string> = new Map([
  [TipoSolicitudGrupo.CONSTITUCION, marker(`csp.tipo-solicitud-grupo.CONSTITUCION`)],
  [TipoSolicitudGrupo.MODIFICACION, marker(`csp.tipo-solicitud-grupo.MODIFICACION`)]
]);
export interface ISolicitud {

  /** Id */
  id: number;
  /** Título */
  titulo: string;
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
  observaciones: string;
}
