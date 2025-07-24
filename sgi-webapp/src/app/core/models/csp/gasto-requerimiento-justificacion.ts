import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IGastoJustificado } from '../sge/gasto-justificado';
import { IRequerimientoJustificacion } from './requerimiento-justificacion';

export interface IGastoRequerimientoJustificacion {
  id: number;
  gasto: IGastoJustificado;
  importeAceptado: number;
  importeRechazado: number;
  importeAlegado: number;
  aceptado: boolean;
  incidencia: I18nFieldValue[];
  alegacion: I18nFieldValue[];
  identificadorJustificacion: string;
  requerimientoJustificacion: IRequerimientoJustificacion;
}
