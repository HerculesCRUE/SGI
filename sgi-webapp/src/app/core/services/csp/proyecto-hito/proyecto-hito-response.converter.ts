import { IGenericEmailText } from '@core/models/com/generic-email-text';
import { IProyectoHito } from '@core/models/csp/proyecto-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { ISendEmailTask } from '@core/models/tp/send-email-task';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoHitoResponse } from './proyecto-hito-response';

class ProyectoHitoResponseConverter extends SgiBaseConverter<IProyectoHitoResponse, IProyectoHito> {

  toTarget(value: IProyectoHitoResponse): IProyectoHito {
    if (!value) {
      return value as unknown as IProyectoHito;
    }
    return {
      id: value.id,
      fecha: LuxonUtils.fromBackend(value.fecha),
      tipoHito: value.tipoHito,
      comentario: value.comentario,
      proyectoId: value.proyectoId,
      aviso: value.proyectoHitoAviso ? {
        email: {
          id: Number(value.proyectoHitoAviso.comunicadoRef)
        } as IGenericEmailText,
        task: {
          id: Number(value.proyectoHitoAviso.tareaProgramadaRef)
        } as ISendEmailTask,
        incluirIpsProyecto: value.proyectoHitoAviso.incluirIpsProyecto,
      } : null
    };
  }

  fromTarget(value: IProyectoHito): IProyectoHitoResponse {
    if (!value) {
      return value as unknown as IProyectoHitoResponse;
    }
    return {
      id: value.id,
      fecha: LuxonUtils.toBackend(value.fecha),
      tipoHito: {
        id: value.tipoHito.id
      } as ITipoHito,
      comentario: value.comentario,
      proyectoId: value.proyectoId,
      proyectoHitoAviso: value.aviso ? {
        comunicadoRef: value.aviso.email.id.toString(),
        tareaProgramadaRef: value.aviso.task.id.toString(),
        incluirIpsProyecto: value.aviso.incluirIpsProyecto,
      } : null
    };
  }
}

export const PROYECTO_HITO_RESPONSE_CONVERTER = new ProyectoHitoResponseConverter();
