import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ClasificacionCVN } from '@core/enums/clasificacion-cvn';
import { TipoSeguimiento } from '@core/enums/tipo-seguimiento';
import { DateTime } from 'luxon';
import { IUnidadGestion } from '../usr/unidad-gestion';
import { IEstadoProyecto } from './estado-proyecto';
import { IProyectoIVA } from './proyecto-iva';
import { IRolSocio } from './rol-socio';
import { IModeloEjecucion, ITipoAmbitoGeografico, ITipoFinalidad } from './tipos-configuracion';

export interface IProyecto {
  /** Id */
  id: number;
  /** EstadoProyecto */
  estado: IEstadoProyecto;
  /** Titulo */
  titulo: string;
  /** Acronimo */
  acronimo: string;
  /** codigoExterno */
  codigoExterno: string;
  /** codigoInterno */
  codigoInterno: string;
  /** Fecha Inicio */
  fechaInicio: DateTime;
  /** Fecha inicio informada en algun momento */
  fechaInicioStarted: boolean;
  /** Fecha Fin */
  fechaFin: DateTime;
  /** Fecha Fin Definitiva */
  fechaFinDefinitiva: DateTime;
  /** Comentario */
  comentario: string;
  /** Unidad gestion */
  unidadGestion: IUnidadGestion;
  /** modelo ejecucion */
  modeloEjecucion: IModeloEjecucion;
  /** convocatoriaExterna */
  convocatoriaExterna: string;
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
  /** coordinado */
  coordinado: boolean;
  /** excelencia */
  excelencia: boolean;
  /** colaborativo */
  colaborativo: boolean;
  /** Rol universidad */
  rolUniversidad: IRolSocio;
  /** permitePaquetesTrabajo */
  permitePaquetesTrabajo: boolean;
  /** iva */
  iva: IProyectoIVA;
  /** IVA deducible */
  ivaDeducible: boolean;
  /** causaExencion */
  causaExencion: CausaExencion;
  /** observaciones */
  observaciones: string;
  /** anualidades */
  anualidades: boolean;
  /** activo  */
  activo: boolean;
  /** Tipo de Seguimiento */
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

export enum CausaExencion {
  SUJETO_EXENTO = 'SUJETO_EXENTO',
  NO_SUJETO = 'NO_SUJETO',
  NO_SUJETO_SIN_DEDUCCION = 'NO_SUJETO_SIN_DEDUCCION',
  NO_SUJETO_CON_DEDUCCION = 'NO_SUJETO_CON_DEDUCCION'
}

export const CAUSA_EXENCION_MAP: Map<CausaExencion, string> = new Map([
  [CausaExencion.SUJETO_EXENTO, marker('csp.proyecto.causa-exencion.SUJETO_EXENTO')],
  [CausaExencion.NO_SUJETO, marker('csp.proyecto.causa-exencion.NO_SUJETO')],
  [CausaExencion.NO_SUJETO_SIN_DEDUCCION, marker('csp.proyecto.causa-exencion.NO_SUJETO_SIN_DEDUCCION')],
  [CausaExencion.NO_SUJETO_CON_DEDUCCION, marker('csp.proyecto.causa-exencion.NO_SUJETO_CON_DEDUCCION')]
]);
