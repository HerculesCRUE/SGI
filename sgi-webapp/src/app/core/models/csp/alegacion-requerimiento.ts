import { DateTime } from 'luxon';
import { IRequerimientoJustificacion } from './requerimiento-justificacion';

export interface IAlegacionRequerimiento {
  id: number;
  requerimientoJustificacion: IRequerimientoJustificacion;
  fechaAlegacion: DateTime;
  importeAlegado: number;
  importeAlegadoCd: number;
  importeAlegadoCi: number;
  importeReintegrado: number;
  importeReintegradoCd: number;
  importeReintegradoCi: number;
  interesesReintegrados: number;
  fechaReintegro: DateTime;
  justificanteReintegro: string;
  observaciones: string;
}
