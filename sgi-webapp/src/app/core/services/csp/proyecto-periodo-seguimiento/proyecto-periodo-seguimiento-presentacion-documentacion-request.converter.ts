import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoPeriodoSeguimientoPresentacionDocumentacionRequest } from './proyecto-periodo-seguimiento-presentacion-documentacion-request';

class ProyectoPeriodoSeguimientoPresentacionDocumentacionRequestConverter
  extends SgiBaseConverter<IProyectoPeriodoSeguimientoPresentacionDocumentacionRequest, IProyectoPeriodoSeguimiento> {

  toTarget(value: IProyectoPeriodoSeguimientoPresentacionDocumentacionRequest): IProyectoPeriodoSeguimiento {
    throw new Error('Method not implemented.');
  }

  fromTarget(value: IProyectoPeriodoSeguimiento): IProyectoPeriodoSeguimientoPresentacionDocumentacionRequest {
    if (!value) {
      return value as unknown as IProyectoPeriodoSeguimientoPresentacionDocumentacionRequest;
    }
    return {
      fechaPresentacionDocumentacion: LuxonUtils.toBackend(value.fechaPresentacionDocumentacion),
    };
  }
}

export const PROYECTO_PERIODO_SEGUIMIENTO_PRESENTACION_DOCUMENTACION_REQUEST_CONVERTER =
  new ProyectoPeriodoSeguimientoPresentacionDocumentacionRequestConverter();
