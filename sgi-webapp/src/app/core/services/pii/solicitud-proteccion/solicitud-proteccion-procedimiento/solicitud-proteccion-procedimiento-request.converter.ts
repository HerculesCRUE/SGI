import { IProcedimiento } from '@core/models/pii/procedimiento';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { ITipoProcedimiento } from '@core/models/pii/tipo-procedimiento';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProcedimientoRequest } from './solicitud-proteccion-procedimiento-request';

export class SolicitudProteccionProcedimientoRequestConverter extends SgiBaseConverter<IProcedimientoRequest, IProcedimiento>{

  toTarget(value: IProcedimientoRequest): IProcedimiento {

    return value ? {
      id: undefined,
      accionATomar: value.accionATomar,
      comentarios: value.comentarios,
      fecha: LuxonUtils.fromBackend(value.fecha),
      fechaLimiteAccion: LuxonUtils.fromBackend(value.fechaLimiteAccion),
      generarAviso: value.generarAviso,
      solicitudProteccion: { id: value.solicitudProteccionId } as ISolicitudProteccion,
      tipoProcedimiento: { id: value.tipoProcedimientoId } as ITipoProcedimiento
    } : (value as unknown as IProcedimiento);
  }

  fromTarget(value: IProcedimiento): IProcedimientoRequest {
    return value ? {
      accionATomar: value.accionATomar,
      comentarios: value.comentarios,
      fecha: LuxonUtils.toBackend(value.fecha),
      fechaLimiteAccion: LuxonUtils.toBackend(value.fechaLimiteAccion),
      generarAviso: value.generarAviso,
      solicitudProteccionId: value.solicitudProteccion?.id,
      tipoProcedimientoId: value.tipoProcedimiento?.id
    } as IProcedimientoRequest : value as unknown as IProcedimientoRequest;
  }

}

export const SOLICITUD_PROTECCION_PROCEDIMIENTO_REQUEST_CONVERTER = new SolicitudProteccionProcedimientoRequestConverter();
