import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProcedimiento } from '@core/models/pii/procedimiento';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_PROCEDIMIENTO_RESPONSE_CONVERTER } from '../../tipo-procedimiento/tipo-procedimiento-response.converter';
import { IProcedimientoResponse } from './solicitud-proteccion-procedimiento-response';

class SolicitudProteccionProcedimientoResponseConverter extends SgiBaseConverter<IProcedimientoResponse, IProcedimiento> {
  toTarget(value: IProcedimientoResponse): IProcedimiento {
    if (!value) {
      return value as unknown as IProcedimiento;
    }
    return {
      id: value.id,
      accionATomar: value.accionATomar ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.accionATomar) : [],
      comentarios: value.comentarios ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentarios) : [],
      fecha: LuxonUtils.fromBackend(value.fecha),
      fechaLimiteAccion: LuxonUtils.fromBackend(value.fechaLimiteAccion),
      generarAviso: value.generarAviso,
      solicitudProteccion: { id: value.solicitudProteccionId } as ISolicitudProteccion,
      tipoProcedimiento: value.tipoProcedimiento ? TIPO_PROCEDIMIENTO_RESPONSE_CONVERTER.toTarget(value.tipoProcedimiento) : null
    };
  }

  fromTarget(value: IProcedimiento): IProcedimientoResponse {
    if (!value) {
      return value as unknown as IProcedimientoResponse;
    }
    return {
      id: value.id,
      accionATomar: value.accionATomar ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.accionATomar) : [],
      comentarios: value.comentarios ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentarios) : [],
      fecha: LuxonUtils.toBackend(value.fecha),
      fechaLimiteAccion: LuxonUtils.toBackend(value.fechaLimiteAccion),
      generarAviso: value.generarAviso,
      solicitudProteccionId: value.solicitudProteccion?.id,
      tipoProcedimiento: value.tipoProcedimiento ? TIPO_PROCEDIMIENTO_RESPONSE_CONVERTER.fromTarget(value.tipoProcedimiento) : null
    };
  }
}

export const SOLICITUD_PROTECCION_PROCEDIMIENTO_RESPONSE_CONVERTER = new SolicitudProteccionProcedimientoResponseConverter();
