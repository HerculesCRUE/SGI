import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IAreaTematica } from './area-tematica';

export interface ISolicitudProyecto {
  id: number;
  titulo: string;
  acronimo: string;
  codExterno: string;
  duracion: number;
  colaborativo: boolean;
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
  importeSolicitadoSocios: number;
  importePresupuestadoSocios: number;
  totalImporteSolicitado: number;
  totalImportePresupuestado: number;
}

export enum TipoPresupuesto {
  GLOBAL = 'GLOBAL',
  MIXTO = 'MIXTO',
  INDIVIDUAL = 'INDIVIDUAL'
}

export const TIPO_PRESUPUESTO_MAP: Map<TipoPresupuesto, string> = new Map([
  [TipoPresupuesto.GLOBAL, marker(`csp.solicitud-proyecto.tipo-presupuesto.GLOBAL`)],
  [TipoPresupuesto.MIXTO, marker(`csp.solicitud-proyecto.tipo-presupuesto.MIXTO`)],
  [TipoPresupuesto.INDIVIDUAL, marker(`csp.solicitud-proyecto.tipo-presupuesto.INDIVIDUAL`)]
]);
