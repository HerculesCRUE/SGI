import { ISolicitud } from '@core/models/csp/solicitud';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudResumenResponse } from './solicitud-resumen-response';

class SolicitudResumenResponseConverter
  extends SgiBaseConverter<ISolicitudResumenResponse, ISolicitud> {
  toTarget(value: ISolicitudResumenResponse): ISolicitud {
    if (!value) {
      return value as unknown as ISolicitud;
    }
    return {
      id: value.id,
      titulo: undefined,
      activo: undefined,
      codigoExterno: undefined,
      codigoRegistroInterno: value.codigoRegistroInterno,
      estado: undefined,
      convocatoriaId: undefined,
      convocatoriaExterna: undefined,
      creador: undefined,
      solicitante: undefined,
      formularioSolicitud: undefined,
      tipoSolicitudGrupo: undefined,
      unidadGestion: undefined,
      observaciones: undefined
    };
  }

  fromTarget(value: ISolicitud): ISolicitudResumenResponse {
    if (!value) {
      return value as unknown as ISolicitudResumenResponse;
    }
    return {
      id: value.id,
      codigoRegistroInterno: value.codigoRegistroInterno
    };
  }
}

export const SOLICITUD_RESUMEN_RESPONSE_CONVERTER = new SolicitudResumenResponseConverter();
