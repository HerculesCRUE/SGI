import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum FormularioSolicitud {
  PROYECTO = 'PROYECTO',
  GRUPO = 'GRUPO'
}


export const FORMULARIO_SOLICITUD_MAP: Map<FormularioSolicitud, string> = new Map([
  [FormularioSolicitud.PROYECTO, marker(`csp.formulario-solicitud.PROYECTO`)],
  [FormularioSolicitud.GRUPO, marker(`csp.formulario-solicitud.GRUPO`)]
]);
