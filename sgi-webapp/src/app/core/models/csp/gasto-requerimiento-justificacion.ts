import { IGastoJustificado } from '../sge/gasto-justificado';
import { IRequerimientoJustificacion } from './requerimiento-justificacion';

export interface IGastoRequerimientoJustificacion {
  id: number;
  gasto: IGastoJustificado;
  importeAceptado: number;
  importeRechazado: number;
  importeAlegado: number;
  aceptado: boolean;
  incidencia: string;
  alegacion: string;
  identificadorJustificacion: string;
  requerimientoJustificacion: IRequerimientoJustificacion;
}
