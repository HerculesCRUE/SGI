import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum TipoPropiedad {
  INTELECTUAL = 'INTELECTUAL',
  INDUSTRIAL = 'INDUSTRIAL',
}

export const TIPO_PROPIEDAD_MAP: Map<TipoPropiedad, string> = new Map([
  [TipoPropiedad.INTELECTUAL, marker('pii.tipo-propiedad.INTELECTUAL')],
  [TipoPropiedad.INDUSTRIAL, marker('pii.tipo-propiedad.INDUSTRIAL')],
]);
