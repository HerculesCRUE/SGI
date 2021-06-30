import { ClasificacionCVN } from '@core/enums/clasificacion-cvn';
import { TipoSeguimiento } from '@core/enums/tipo-seguimiento';
import { CausaExencion, TipoHorasAnuales } from '../proyecto';
import { ITipoAmbitoGeografico } from '../tipo-ambito-geografico';
import { IModeloEjecucion, ITipoFinalidad } from '../tipos-configuracion';
import { IEstadoProyectoBackend } from './estado-proyecto-backend';
import { IProyectoIVABackend } from './proyecto-iva-backend';

export interface IProyectoBackend {
  /** Id */
  id: number;
  /** EstadoProyecto */
  estado: IEstadoProyectoBackend;
  /** Titulo */
  titulo: string;
  /** Acronimo */
  acronimo: string;
  /** codigoExterno */
  codigoExterno: string;
  /** Fecha Inicio */
  fechaInicio: string;
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
  /** colaborativo */
  colaborativo: boolean;
  /** coordinadorExterno */
  coordinadorExterno: boolean;
  /** timesheet */
  timesheet: boolean;
  /** permitePaquetesTrabajo */
  permitePaquetesTrabajo: boolean;
  /** costeHora */
  costeHora: boolean;
  /** tipoHorasAnuales */
  tipoHorasAnuales: TipoHorasAnuales;
  /** iva */
  iva: IProyectoIVABackend;
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
  /** Fecha Base */
  fechaBase: string;
  /** Importe presupuesto */
  importePresupuesto: number;
  /** Importe concedido */
  importeConcedido: number;
  /** Importe presupuesto socios */
  importePresupuestoSocios: number;
  /** Importe concedido socios */
  importeConcedidoSocios: number;
  /** total Importe presupuesto */
  totalImportePresupuesto: number;
  /** total Importe concedido */
  totalImporteConcedido: number;
}
