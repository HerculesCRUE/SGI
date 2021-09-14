import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IAreaTematica } from './area-tematica';

export interface ISolicitudProyecto {
  id: number;
  acronimo: string;
  codExterno: string;
  duracion: number;
  colaborativo: boolean;
  coordinado: boolean;
  coordinadorExterno: boolean;
  objetivos: string;
  intereses: string;
  resultadosPrevistos: string;
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
