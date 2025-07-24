import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IGenericEmailText } from '@core/models/com/generic-email-text';
import { ISolicitudHito } from '@core/models/csp/solicitud-hito';
import { ISendEmailTask } from '@core/models/tp/send-email-task';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoHitoResponse } from '../tipo-hito/tipo-hito-response';
import { TIPO_HITO_RESPONSE_CONVERTER } from '../tipo-hito/tipo-hito-response.converter';
import { ISolicitudHitoResponse } from './solicitud-hito-response';

class SolicitudHitoResponseConverter extends SgiBaseConverter<ISolicitudHitoResponse, ISolicitudHito> {

  toTarget(value: ISolicitudHitoResponse): ISolicitudHito {
    if (!value) {
      return value as unknown as ISolicitudHito;
    }
    return {
      id: value.id,
      fecha: LuxonUtils.fromBackend(value.fecha),
      tipoHito: value.tipoHito ? TIPO_HITO_RESPONSE_CONVERTER.toTarget(value.tipoHito) : null,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentario) : [],
      solicitudId: value.solicitudId,
      createdBy: value.createdBy,
      aviso: value.aviso ? {
        email: {
          id: Number(value.aviso.comunicadoRef)
        } as IGenericEmailText,
        task: {
          id: Number(value.aviso.tareaProgramadaRef)
        } as ISendEmailTask,
        incluirIpsSolicitud: value.aviso.incluirIpsSolicitud,
      } : null
    };
  }

  fromTarget(value: ISolicitudHito): ISolicitudHitoResponse {
    if (!value) {
      return value as unknown as ISolicitudHitoResponse;
    }
    return {
      id: value.id,
      fecha: LuxonUtils.toBackend(value.fecha),
      tipoHito: {
        id: value.tipoHito.id
      } as ITipoHitoResponse,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentario) : [],
      solicitudId: value.solicitudId,
      createdBy: value.createdBy,
      aviso: value.aviso ? {
        comunicadoRef: value.aviso.email.id.toString(),
        tareaProgramadaRef: value.aviso.task.id.toString(),
        incluirIpsSolicitud: value.aviso.incluirIpsSolicitud,
      } : null
    };
  }
}

export const SOLICITUD_HITO_RESPONSE_CONVERTER = new SolicitudHitoResponseConverter();
