import { IGenericEmailText } from '@core/models/com/generic-email-text';
import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { ISendEmailTask } from '@core/models/tp/send-email-task';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IConvocatoriaHitoResponse } from './convocatoria-hito-response';

class ConvocatoriaHitoResponseConverter extends SgiBaseConverter<IConvocatoriaHitoResponse, IConvocatoriaHito> {

  toTarget(value: IConvocatoriaHitoResponse): IConvocatoriaHito {
    if (!value) {
      return value as unknown as IConvocatoriaHito;
    }
    return {
      id: value.id,
      fecha: LuxonUtils.fromBackend(value.fecha),
      tipoHito: value.tipoHito,
      comentario: value.comentario,
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
      } as ITipoHito,
      comentario: value.comentario,
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
