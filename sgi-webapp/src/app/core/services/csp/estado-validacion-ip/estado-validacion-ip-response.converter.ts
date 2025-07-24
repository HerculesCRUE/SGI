import { I18N_FIELD_RESPONSE_CONVERTER } from "@core/i18n/i18n-field.converter";
import { IEstadoValidacionIP } from "@core/models/csp/estado-validacion-ip";
import { LuxonUtils } from "@core/utils/luxon-utils";
import { SgiBaseConverter } from "@sgi/framework/core";
import { IEstadoValidacionIPResponse } from "./estado-validacion-ip.response";

class EstadoValidacionIPResponseConverter extends SgiBaseConverter<IEstadoValidacionIPResponse, IEstadoValidacionIP> {

  toTarget(value: IEstadoValidacionIPResponse): IEstadoValidacionIP {
    return value ? {
      id: value.id,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentario) : [],
      proyectoFacturacionId: value.proyectoFacturacionId,
      fecha: LuxonUtils.fromBackend(value.fecha),
      estado: value.estado
    } : value as unknown as IEstadoValidacionIP;
  }

  fromTarget(value: IEstadoValidacionIP): IEstadoValidacionIPResponse {
    return value ? {
      id: value.id,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentario) : [],
      proyectoFacturacionId: value.proyectoFacturacionId,
      fecha: LuxonUtils.toBackend(value.fecha),
      estado: value.estado
    } : value as unknown as IEstadoValidacionIPResponse;
  }

}

export const ESTADO_VALIDACION_IP_RESPONSE_CONVERTER = new EstadoValidacionIPResponseConverter();
