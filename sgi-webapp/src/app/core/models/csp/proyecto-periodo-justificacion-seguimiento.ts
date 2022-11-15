import { DateTime } from 'luxon';
import { IProyectoAnualidad } from './proyecto-anualidad';
import { IProyectoPeriodoJustificacion } from './proyecto-periodo-justificacion';

export interface IProyectoPeriodoJustificacionSeguimiento {
  id: number;
  proyectoPeriodoJustificacion: IProyectoPeriodoJustificacion;
  proyectoAnualidad: IProyectoAnualidad;
  importeJustificado: number;
  importeJustificadoCD: number;
  importeJustificadoCI: number;
  importeAceptado: number;
  importeAceptadoCD: number;
  importeAceptadoCI: number;
  importeRechazado: number;
  importeRechazadoCD: number;
  importeRechazadoCI: number;
  importeAlegado: number;
  importeAlegadoCD: number;
  importeAlegadoCI: number;
  importeReintegrar: number;
  importeReintegrarCD: number;
  importeReintegrarCI: number;
  importeReintegrado: number;
  importeReintegradoCD: number;
  importeReintegradoCI: number;
  interesesReintegrados: number;
  interesesReintegrar: number;
  fechaReintegro: DateTime;
  justificanteReintegro: string;
  importeNoEjecutado: number;
  importeNoEjecutadoCD: number;
  importeNoEjecutadoCI: number;
}
