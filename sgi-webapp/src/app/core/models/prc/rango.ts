import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoriaBaremacion } from './convocatoria-baremacion';

export interface IRango {
  id: number;
  tipoRango: TipoRango;
  tipoTemporalidad: TipoTemporalidad;
  desde: number;
  hasta: number;
  puntos: number;
  convocatoriaBaremacion: IConvocatoriaBaremacion;
}

export enum TipoRango {
  CUANTIA_CONTRATOS = 'CUANTIA_CONTRATOS',
  CUANTIA_COSTES_INDIRECTOS = 'CUANTIA_COSTES_INDIRECTOS',
  LICENCIA = 'LICENCIA'
}

export enum TipoTemporalidad {
  INICIAL = 'INICIAL',
  INTERMEDIO = 'INTERMEDIO',
  FINAL = 'FINAL'
}

export const TIPO_TEMPORALIDAD_MAP: Map<TipoTemporalidad, string> = new Map([
  [TipoTemporalidad.INICIAL, marker('prc.tipo-rango.INICIAL')],
  [TipoTemporalidad.INTERMEDIO, marker('prc.tipo-rango.INTERMEDIO')],
  [TipoTemporalidad.FINAL, marker('prc.tipo-rango.FINAL')]
]);
