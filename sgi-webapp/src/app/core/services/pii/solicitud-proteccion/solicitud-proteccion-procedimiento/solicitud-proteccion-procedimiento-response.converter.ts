import { IProcedimiento } from '@core/models/pii/procedimiento';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProcedimientoResponse } from './solicitud-proteccion-procedimiento-response';

class SolicitudProteccionProcedimientoResponseConverter extends SgiBaseConverter<IProcedimientoResponse, IProcedimiento>{
  toTarget(value: IProcedimientoResponse): IProcedimiento {
    if (!value) {
      return value as unknown as IProcedimiento;
    }
    return {
      id: value.id,
      accionATomar: value.accionATomar,
      comentarios: value.comentarios,
      fecha: LuxonUtils.fromBackend(value.fecha),
      fechaLimiteAccion: LuxonUtils.fromBackend(value.fechaLimiteAccion),
      generarAviso: value.generarAviso,
      solicitudProteccion: { id: value.solicitudProteccionId } as ISolicitudProteccion,
      tipoProcedimiento: value.tipoProcedimiento
    };
  }

  fromTarget(value: IProcedimiento): IProcedimientoResponse {
    if (!value) {
      return value as unknown as IProcedimientoResponse;
    }
    return {
      id: value.id,
      accionATomar: value.accionATomar,
      comentarios: value.comentarios,
      fecha: LuxonUtils.toBackend(value.fecha),
      fechaLimiteAccion: LuxonUtils.toBackend(value.fechaLimiteAccion),
      generarAviso: value.generarAviso,
      solicitudProteccionId: value.solicitudProteccion?.id,
      tipoProcedimiento: value.tipoProcedimiento
    };
  }
}

export const SOLICITUD_PROTECCION_PROCEDIMIENTO_RESPONSE_CONVERTER = new SolicitudProteccionProcedimientoResponseConverter();
