import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConfiguracionBaremo } from './configuracion-baremo';
import { IConvocatoriaBaremacion } from './convocatoria-baremacion';

export interface IBaremo {
  id: number;
  peso: number;
  puntos: number;
  cuantia: number;
  tipoCuantia: TipoCuantia;
  configuracionBaremo: IConfiguracionBaremo;
  convocatoriaBaremacion: IConvocatoriaBaremacion;
}

export enum TipoCuantia {
  PUNTOS = 'PUNTOS',
  RANGO = 'RANGO'
}

export const TIPO_CUANTIA_MAP: Map<TipoCuantia, string> = new Map([
  [TipoCuantia.PUNTOS, marker('prc.baremo.tipo-cuantia.PUNTOS')],
  [TipoCuantia.RANGO, marker('prc.baremo.tipo-cuantia.RANGO')],
]);

export const BAREMO_PESO_MAX = 100;
export const BAREMO_PESO_MIN = 1;
