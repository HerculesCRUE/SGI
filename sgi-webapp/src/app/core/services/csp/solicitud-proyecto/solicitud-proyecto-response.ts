import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { IAreaTematicaResponse } from '@core/services/csp/area-tematica/area-tematica-response';
import { TipoPresupuesto } from '../../../models/csp/solicitud-proyecto';

export interface ISolicitudProyectoResponse {
  id: number;
  acronimo: string;
  codExterno: string;
  duracion: number;
  colaborativo: boolean;
  coordinado: boolean;
  rolUniversidadId: number;
  objetivos: I18nFieldValueResponse[];
  intereses: I18nFieldValueResponse[];
  resultadosPrevistos: I18nFieldValueResponse[];
  areaTematica: IAreaTematicaResponse;
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
