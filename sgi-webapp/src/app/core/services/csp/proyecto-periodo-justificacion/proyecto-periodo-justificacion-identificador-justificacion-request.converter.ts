import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoPeriodoJustificacionIdentificadorJustificacionRequest } from './proyecto-periodo-justificacion-identificador-justificacion-request';

class ProyectoPeriodoJustificacionIdentificadorJustificacionRequestConverter
  extends SgiBaseConverter<IProyectoPeriodoJustificacionIdentificadorJustificacionRequest, IProyectoPeriodoJustificacion> {

  toTarget(value: IProyectoPeriodoJustificacionIdentificadorJustificacionRequest): IProyectoPeriodoJustificacion {
    throw new Error('Method not implemented.');
  }

  fromTarget(value: IProyectoPeriodoJustificacion): IProyectoPeriodoJustificacionIdentificadorJustificacionRequest {
    if (!value) {
      return value as unknown as IProyectoPeriodoJustificacionIdentificadorJustificacionRequest;
    }
    return {
      fechaPresentacionJustificacion: LuxonUtils.toBackend(value.fechaPresentacionJustificacion),
      identificadorJustificacion: value.identificadorJustificacion
    };
  }
}

export const PROYECTO_PERIODO_JUSTIFICACION_IDENTIFICADOR_JUSTIFICACION_REQUEST_CONVERTER =
  new ProyectoPeriodoJustificacionIdentificadorJustificacionRequestConverter();
