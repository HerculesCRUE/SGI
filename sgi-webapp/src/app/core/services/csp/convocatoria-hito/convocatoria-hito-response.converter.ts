import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IGenericEmailText } from '@core/models/com/generic-email-text';
import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { ISendEmailTask } from '@core/models/tp/send-email-task';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoHitoResponse } from '../tipo-hito/tipo-hito-response';
import { TIPO_HITO_RESPONSE_CONVERTER } from '../tipo-hito/tipo-hito-response.converter';
import { IConvocatoriaHitoResponse } from './convocatoria-hito-response';

class ConvocatoriaHitoResponseConverter extends SgiBaseConverter<IConvocatoriaHitoResponse, IConvocatoriaHito> {

  toTarget(value: IConvocatoriaHitoResponse): IConvocatoriaHito {
    if (!value) {
      return value as unknown as IConvocatoriaHito;
    }
    return {
      id: value.id,
      fecha: LuxonUtils.fromBackend(value.fecha),
      tipoHito: value.tipoHito ? TIPO_HITO_RESPONSE_CONVERTER.toTarget(value.tipoHito) : null,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentario) : [],
      convocatoriaId: value.convocatoriaId,
      aviso: value.aviso ? {
        email: {
          id: Number(value.aviso.comunicadoRef)
        } as IGenericEmailText,
        task: {
          id: Number(value.aviso.tareaProgramadaRef)
        } as ISendEmailTask,
        incluirIpsSolicitud: value.aviso.incluirIpsSolicitud,
        incluirIpsProyecto: value.aviso.incluirIpsProyecto
      } : null
    };
  }

  fromTarget(value: IConvocatoriaHito): IConvocatoriaHitoResponse {
    if (!value) {
      return value as unknown as IConvocatoriaHitoResponse;
    }
    return {
      id: value.id,
      fecha: LuxonUtils.toBackend(value.fecha),
      tipoHito: {
        id: value.tipoHito.id
      } as ITipoHitoResponse,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentario) : [],
      convocatoriaId: value.convocatoriaId,
      aviso: value.aviso ? {
        comunicadoRef: value.aviso.email.id.toString(),
        tareaProgramadaRef: value.aviso.task.id.toString(),
        incluirIpsSolicitud: value.aviso.incluirIpsSolicitud,
        incluirIpsProyecto: value.aviso.incluirIpsProyecto
      } : null
    };
  }
}

export const CONVOCATORIA_HITO_RESPONSE_CONVERTER = new ConvocatoriaHitoResponseConverter();
