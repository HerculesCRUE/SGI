import { INotificacionCVNEntidadFinanciadora } from '@core/models/csp/notificacion-cvn-entidad-financiadora';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SgiBaseConverter } from '@sgi/framework/core';
import { INotificacionCVNEntidadFinanciadoraRequest } from './notificacion-cvn-entidad-financiadora-request';

class INotificacionCVNEntidadFinanciadoraRequestConverter
  extends SgiBaseConverter<INotificacionCVNEntidadFinanciadoraRequest, INotificacionCVNEntidadFinanciadora> {
  toTarget(value: INotificacionCVNEntidadFinanciadoraRequest): INotificacionCVNEntidadFinanciadora {
    if (!value) {
      return value as unknown as INotificacionCVNEntidadFinanciadora;
    }
    return {
      id: undefined,
      datosEntidadFinanciadora: value.datosEntidadFinanciadora,
      entidadFinanciadora: { id: value.entidadFinanciadoraRef } as IEmpresa,
      notificacionProyecto: { id: value.notificacionProyectoId } as INotificacionProyectoExternoCVN,

    };
  }

  fromTarget(value: INotificacionCVNEntidadFinanciadora): INotificacionCVNEntidadFinanciadoraRequest {
    if (!value) {
      return value as unknown as INotificacionCVNEntidadFinanciadoraRequest;
    }
    return {
      datosEntidadFinanciadora: value.datosEntidadFinanciadora,
      entidadFinanciadoraRef: value.entidadFinanciadora?.id,
      notificacionProyectoId: value.notificacionProyecto?.id
    };
  }
}

export const NOTIFICACION_CVN_ENTIDAD_FINANCIADORA_REQUEST_CONVERTER = new INotificacionCVNEntidadFinanciadoraRequestConverter();
