import { INotificacionCVNEntidadFinanciadora } from '@core/models/csp/notificacion-cvn-entidad-financiadora';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SgiBaseConverter } from '@sgi/framework/core';
import { INotificacionCVNEntidadFinanciadoraResponse } from './notificacion-cvn-entidad-financiadora-response';

class INotificacionCVNEntidadFinanciadoraResponseConverter
  extends SgiBaseConverter<INotificacionCVNEntidadFinanciadoraResponse, INotificacionCVNEntidadFinanciadora> {
  toTarget(value: INotificacionCVNEntidadFinanciadoraResponse): INotificacionCVNEntidadFinanciadora {
    if (!value) {
      return value as unknown as INotificacionCVNEntidadFinanciadora;
    }
    return {
      id: value.id,
      datosEntidadFinanciadora: value.datosEntidadFinanciadora,
      entidadFinanciadora: { id: value.entidadFinanciadoraRef } as IEmpresa,
      notificacionProyecto: { id: value.notificacionProyectoId } as INotificacionProyectoExternoCVN,

    };
  }

  fromTarget(value: INotificacionCVNEntidadFinanciadora): INotificacionCVNEntidadFinanciadoraResponse {
    if (!value) {
      return value as unknown as INotificacionCVNEntidadFinanciadoraResponse;
    }
    return {
      id: value.id,
      datosEntidadFinanciadora: value.datosEntidadFinanciadora,
      entidadFinanciadoraRef: value.entidadFinanciadora?.id,
      notificacionProyectoId: value.notificacionProyecto?.id
    };
  }
}

export const NOTIFICACION_CVN_ENTIDAD_FINANCIADORA_RESPONSE_CONVERTER = new INotificacionCVNEntidadFinanciadoraResponseConverter();
