import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum ClasificacionCVN {
  AYUDAS = 'AYUDAS',
  COMPETITIVOS = 'COMPETITIVOS',
  NO_COMPETITIVOS = 'NO_COMPETITIVOS',
  INSTITUCIONAL_AYUDAS = 'INSTITUCIONAL_AYUDAS',
  INSTITUCIONAL_COMPETITIVOS = 'INSTITUCIONAL_COMPETITIVOS',
  INSTITUCIONAL_NO_COMPETITIVOS = 'INSTITUCIONAL_NO_COMPETITIVOS'
}

export const CLASIFICACION_CVN_MAP: Map<ClasificacionCVN, string> = new Map([
  [ClasificacionCVN.AYUDAS, marker(`csp.clasificacion-cvn.AYUDAS`)],
  [ClasificacionCVN.COMPETITIVOS, marker(`csp.clasificacion-cvn.COMPETITIVOS`)],
  [ClasificacionCVN.NO_COMPETITIVOS, marker(`csp.clasificacion-cvn.NO_COMPETITIVOS`)],
  [ClasificacionCVN.INSTITUCIONAL_AYUDAS, marker(`csp.clasificacion-cvn.INSTITUCIONAL_AYUDAS`)],
  [ClasificacionCVN.INSTITUCIONAL_COMPETITIVOS, marker(`csp.clasificacion-cvn.INSTITUCIONAL_COMPETITIVOS`)],
  [ClasificacionCVN.INSTITUCIONAL_NO_COMPETITIVOS, marker(`csp.clasificacion-cvn.INSTITUCIONAL_NO_COMPETITIVOS`)],
]);