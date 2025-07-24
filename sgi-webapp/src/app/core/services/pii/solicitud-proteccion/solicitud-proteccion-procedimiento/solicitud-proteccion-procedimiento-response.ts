import { ITipoProcedimiento } from '@core/models/pii/tipo-procedimiento';
import { ITipoProcedimientoResponse } from '../../tipo-procedimiento/tipo-procedimiento-response';
import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';

export interface IProcedimientoResponse {

  id: number;
  fecha: string;
  tipoProcedimiento: ITipoProcedimientoResponse;
  solicitudProteccionId: number;
  accionATomar: I18nFieldValueResponse[];
  fechaLimiteAccion: string;
  generarAviso: boolean;
  comentarios: I18nFieldValueResponse[];

}
