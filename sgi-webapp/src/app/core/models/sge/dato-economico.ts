import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';

export interface IDatoEconomico {
  id: string;
  proyectoId: string;
  partidaPresupuestaria: string;
  codigoEconomico: any;
  anualidad: string;
  tipo: TipoDatoEconomico;
  fechaDevengo: DateTime;
  clasificacionSGE: {
    id: string,
    nombre: string
  };
  columnas: {
    [name: string]: string | number;
  };
}

export enum TipoDatoEconomico {
  GASTO = 'Gasto',
  INGRESO = 'Ingreso'
}

export const TIPO_DATO_ECONOMICO_MAP: Map<TipoDatoEconomico, string> = new Map([
  [TipoDatoEconomico.GASTO, marker('csp.tipo-dato-economico.GASTO')],
  [TipoDatoEconomico.INGRESO, marker('csp.tipo-dato-economico.INGRESO')]
]);