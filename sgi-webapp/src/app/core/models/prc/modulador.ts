import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IAreaConocimiento } from '../sgo/area-conocimiento';
import { IConvocatoriaBaremacion } from './convocatoria-baremacion';

export interface IModulador {
  id: number;
  area: IAreaConocimiento;
  tipo: Tipo;
  valor1: number;
  valor2: number;
  valor3: number;
  valor4: number;
  valor5: number;
  convocatoriaBaremacion: IConvocatoriaBaremacion;
}

export enum Tipo {
  AREAS = 'AREAS',
  NUMERO_AUTORES = 'NUMERO_AUTORES'
}

export const TIPO: Map<Tipo, string> = new Map([
  [Tipo.AREAS, marker('prc.modulador.tipo.AREAS')],
  [Tipo.NUMERO_AUTORES, marker('prc.modulador.tipo.NUMERO_AUTORES')]
]);
