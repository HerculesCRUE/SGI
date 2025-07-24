import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PROGRAMA_RESPONSE_CONVERTER } from '../programa/programa-response.converter';
import { ISolicitudModalidadResponse } from './solicitud-modalidad-response';

class SolicitudModalidadResponseConverter extends SgiBaseConverter<ISolicitudModalidadResponse, ISolicitudModalidad> {

  toTarget(value: ISolicitudModalidadResponse): ISolicitudModalidad {
    if (!value) {
      return value as unknown as ISolicitudModalidad;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      entidad: { id: value.entidadRef } as IEmpresa,
      programa: PROGRAMA_RESPONSE_CONVERTER.toTarget(value.programa),
      programaConvocatoriaId: value.programaConvocatoriaId
    };
  }

  fromTarget(value: ISolicitudModalidad): ISolicitudModalidadResponse {
    if (!value) {
      return value as unknown as ISolicitudModalidadResponse;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      entidadRef: value.entidad?.id,
      programa: PROGRAMA_RESPONSE_CONVERTER.fromTarget(value.programa),
      programaConvocatoriaId: value.programaConvocatoriaId
    };
  }
}

export const SOLICITUD_MODALIDAD_RESPONSE_CONVERTER = new SolicitudModalidadResponseConverter();
