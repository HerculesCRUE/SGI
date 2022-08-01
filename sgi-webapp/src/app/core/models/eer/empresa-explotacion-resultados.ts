import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';
import { IEmpresa } from '../sgemp/empresa';
import { IPersona } from '../sgp/persona';

export enum TipoEmpresa {
  EBT = 'EBT',
  EINCNT = 'EINCNT'
}

export const TIPO_EMPRESA_EXPLOTACION_RESULTADOS_MAP: Map<TipoEmpresa, string> = new Map([
  [TipoEmpresa.EBT, marker('eer.tipo-empresa.EBT')],
  [TipoEmpresa.EINCNT, marker('eer.tipo-empresa.EINCNT')]
]);

export enum EstadoEmpresa {
  EN_TRAMITACION = 'EN_TRAMITACION',
  NO_APROBADA = 'NO_APROBADA',
  ACTIVA = 'ACTIVA',
  SIN_ACTIVIDAD = 'SIN_ACTIVIDAD',
  DISUELTA = 'DISUELTA'
}

export const ESTADO_EMPRESA_EXPLOTACION_RESULTADOS_MAP: Map<EstadoEmpresa, string> = new Map([
  [EstadoEmpresa.EN_TRAMITACION, marker('eer.estado-empresa.EN_TRAMITACION')],
  [EstadoEmpresa.NO_APROBADA, marker('eer.estado-empresa.NO_APROBADA')],
  [EstadoEmpresa.ACTIVA, marker('eer.estado-empresa.ACTIVA')],
  [EstadoEmpresa.SIN_ACTIVIDAD, marker('eer.estado-empresa.SIN_ACTIVIDAD')],
  [EstadoEmpresa.DISUELTA, marker('eer.estado-empresa.DISUELTA')]
]);


export interface IEmpresaExplotacionResultados {
  id: number;
  fechaSolicitud: DateTime;
  tipoEmpresa: TipoEmpresa;
  solicitante: IPersona;
  nombreRazonSocial: string;
  entidad: IEmpresa;
  objetoSocial: string;
  conocimientoTecnologia: string;
  numeroProtocolo: string;
  notario: string;
  fechaConstitucion: DateTime;
  fechaAprobacionCG: DateTime;
  fechaIncorporacion: DateTime;
  fechaDesvinculacion: DateTime;
  fechaCese: DateTime;
  observaciones: string;
  estado: EstadoEmpresa;
  activo: boolean;
}
