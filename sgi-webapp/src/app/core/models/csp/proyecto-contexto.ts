import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IAreaTematica } from './area-tematica';

export interface IProyectoContexto {
  id: number;
  proyectoId: number;
  objetivos: I18nFieldValue[];
  intereses: I18nFieldValue[];
  resultadosPrevistos: I18nFieldValue[];
  propiedadResultados: PropiedadResultados;
  areaTematica: IAreaTematica;
}

export enum PropiedadResultados {
  SIN_RESULTADOS = 'SIN_RESULTADOS',
  UNIVERSIDAD = 'UNIVERSIDAD',
  ENTIDAD_FINANCIADORA = 'ENTIDAD_FINANCIADORA',
  COMPARTIDA = 'COMPARTIDA'
}

export const PROPIEDAD_RESULTADOS_MAP: Map<PropiedadResultados, string> = new Map([
  [PropiedadResultados.SIN_RESULTADOS, marker('csp.proyecto-contexto.propiedad-resultados.SIN_RESULTADOS')],
  [PropiedadResultados.UNIVERSIDAD, marker('csp.proyecto-contexto.propiedad-resultados.UNIVERSIDAD')],
  [PropiedadResultados.ENTIDAD_FINANCIADORA, marker('csp.proyecto-contexto.propiedad-resultados.ENTIDAD_FINANCIADORA')],
  [PropiedadResultados.COMPARTIDA, marker('csp.proyecto-contexto.propiedad-resultados.COMPARTIDA')],
]);
