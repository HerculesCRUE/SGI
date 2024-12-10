import { ClasificacionCVN } from '@core/enums/clasificacion-cvn';
import { TipoSeguimiento } from '@core/enums/tipo-seguimiento';
import { IEstadoProyectoResponse } from '../../../services/csp/estado-proyecto/estado-proyecto-response';
import { CausaExencion } from '../proyecto';
import { IModeloEjecucion, ITipoAmbitoGeografico, ITipoFinalidad } from '../tipos-configuracion';
import { IProyectoIVABackend } from './proyecto-iva-backend';

export interface IProyectoBackend {
  /** Id */
  id: number;
  /** EstadoProyecto */
  estado: IEstadoProyectoResponse;
  /** Titulo */
  titulo: string;
  /** Acronimo */
  acronimo: string;
  /** codigoInterno */
  codigoInterno: string;
  /** codigoExterno */
  codigoExterno: string;
  /** Fecha Inicio */
  fechaInicio: string;
  /** Fecha inicio informada en algun momento */
  fechaInicioStarted: boolean;
  /** Fecha Fin */
  fechaFin: string;
  /** Fecha Fin Definitiva */
  fechaFinDefinitiva: string;
  /** modelo ejecucion */
  modeloEjecucion: IModeloEjecucion;
  /** finalidad */
  finalidad: ITipoFinalidad;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Id de Solicitud */
  solicitudId: number;
  /** ambitoGeografico */
  ambitoGeografico: ITipoAmbitoGeografico;
  /** confidencial */
  confidencial: boolean;
  /** clasificacionCVN */
  clasificacionCVN: ClasificacionCVN;
  /** convocatoriaExterna */
  convocatoriaExterna: string;
  /** coordinado */
  coordinado: boolean;
  /** colaborativo */
  colaborativo: boolean;
  /** excelencia */
  excelencia: boolean;
  /** Id de RolSocio de la Universidad */
  rolUniversidadId: number;
  /** permitePaquetesTrabajo */
  permitePaquetesTrabajo: boolean;
  /** iva */
  iva: IProyectoIVABackend;
  /** IVA deducible */
  ivaDeducible: boolean;
  /** causaExencion */
  causaExencion: CausaExencion;
  /** observaciones */
  observaciones: string;
  /** unidadGestionRef */
  unidadGestionRef: string;
  /** anualidades */
  anualidades: boolean;
  /** activo  */
  activo: boolean;
  /** Tipo de seguimiento */
  tipoSeguimiento: TipoSeguimiento;
  /** Importe presupuesto */
  importePresupuesto: number;
  /** Importe presupuesto Costes Indirectos */
  importePresupuestoCostesIndirectos: number;
  /** Importe concedido */
  importeConcedido: number;
  /** Importe concedido Costes Indirectos */
  importeConcedidoCostesIndirectos: number;
  /** Importe presupuesto socios */
  importePresupuestoSocios: number;
  /** Importe concedido socios */
  importeConcedidoSocios: number;
  /** total Importe presupuesto */
  totalImportePresupuesto: number;
  /** total Importe concedido */
  totalImporteConcedido: number;
}
