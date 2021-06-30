import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum FormularioSolicitud {
  ESTANDAR = 'ESTANDAR',
  RRHH = 'RRHH',
  AYUDAS_GRUPOS = 'AYUDAS_GRUPOS'
}


export const FORMULARIO_SOLICITUD_MAP: Map<FormularioSolicitud, string> = new Map([
  [FormularioSolicitud.ESTANDAR, marker(`csp.formulario-solicitud.ESTANDAR`)],
  [FormularioSolicitud.RRHH, marker(`csp.formulario-solicitud.RRHH`)],
  [FormularioSolicitud.AYUDAS_GRUPOS, marker(`csp.formulario-solicitud.AYUDAS_GRUPOS`)]
]);
