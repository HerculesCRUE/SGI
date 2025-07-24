import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IAreaTematica } from './area-tematica';
import { IRolSocio } from './rol-socio';

export interface ISolicitudProyecto {
  id: number;
  acronimo: string;
  codExterno: string;
  duracion: number;
  colaborativo: boolean;
  coordinado: boolean;
  rolUniversidad: IRolSocio;
  objetivos: I18nFieldValue[];
  intereses: I18nFieldValue[];
  resultadosPrevistos: I18nFieldValue[];
  areaTematica: IAreaTematica;
  checklistRef: string;
  peticionEvaluacionRef: string;
  tipoPresupuesto: TipoPresupuesto;
  importeSolicitado: number;
  importePresupuestado: number;
  importePresupuestadoCostesIndirectos: number;
  importeSolicitadoCostesIndirectos: number;
  importeSolicitadoSocios: number;
  importePresupuestadoSocios: number;
  totalImporteSolicitado: number;
  totalImportePresupuestado: number;
}

export enum TipoPresupuesto {
  GLOBAL = 'GLOBAL',
  MIXTO = 'MIXTO',
  POR_ENTIDAD = 'POR_ENTIDAD',
}

export const TIPO_PRESUPUESTO_MAP: Map<TipoPresupuesto, string> = new Map([
  [TipoPresupuesto.GLOBAL, marker(`csp.solicitud-proyecto.tipo-presupuesto.GLOBAL`)],
  [TipoPresupuesto.MIXTO, marker(`csp.solicitud-proyecto.tipo-presupuesto.MIXTO`)],
  [TipoPresupuesto.POR_ENTIDAD, marker(`csp.solicitud-proyecto.tipo-presupuesto.POR_ENTIDAD`)],
]);
