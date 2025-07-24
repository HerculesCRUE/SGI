import { DateTime } from 'luxon';
import { ISolicitudProteccion } from './solicitud-proteccion';
import { ITipoProcedimiento } from './tipo-procedimiento';
import { I18nFieldValue } from '@core/i18n/i18n-field';

export interface IProcedimiento {

  id: number;
  fecha: DateTime;
  tipoProcedimiento: ITipoProcedimiento;
  solicitudProteccion: ISolicitudProteccion;
  accionATomar: I18nFieldValue[];
  fechaLimiteAccion: DateTime;
  generarAviso: boolean;
  comentarios: I18nFieldValue[];

}
