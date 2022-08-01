import { DateTime } from 'luxon';
import { IProyectoPeriodoJustificacion } from './proyecto-periodo-justificacion';
import { IProyectoProyectoSge } from './proyecto-proyecto-sge';
import { ITipoRequerimiento } from './tipo-requerimiento';

export interface IRequerimientoJustificacion {
  id: number;
  anticipoJustificado: number;
  defectoAnticipo: number;
  defectoSubvencion: number;
  fechaFinAlegacion: DateTime;
  fechaNotificacion: DateTime;
  importeAceptado: number;
  importeAceptadoCd: number;
  importeAceptadoCi: number;
  importeRechazado: number;
  importeRechazadoCd: number;
  importeRechazadoCi: number;
  importeReintegrar: number;
  importeReintegrarCd: number;
  importeReintegrarCi: number;
  interesesReintegrar: number;
  numRequerimiento: number;
  observaciones: string;
  proyectoPeriodoJustificacion: IProyectoPeriodoJustificacion;
  proyectoProyectoSge: IProyectoProyectoSge;
  recursoEstimado: boolean;
  requerimientoPrevio: IRequerimientoJustificacion;
  subvencionJustificada: number;
  tipoRequerimiento: ITipoRequerimiento;
}
