import { IAreaTematica } from '../area-tematica';
import { TipoPresupuesto } from '../solicitud-proyecto';

export interface ISolicitudProyectoBackend {
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
