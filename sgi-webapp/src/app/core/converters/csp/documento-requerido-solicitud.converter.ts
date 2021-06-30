import { IDocumentoRequeridoSolicitudBackend } from '@core/models/csp/backend/documento-requerido-solicitud-backend';
import { IDocumentoRequeridoSolicitud } from '@core/models/csp/documento-requerido-solicitud';
import { SgiBaseConverter } from '@sgi/framework/core';

class DocumentoRequeridoSolicitudConverter extends
  SgiBaseConverter<IDocumentoRequeridoSolicitudBackend, IDocumentoRequeridoSolicitud> {

  toTarget(value: IDocumentoRequeridoSolicitudBackend): IDocumentoRequeridoSolicitud {
    if (!value) {
      return value as unknown as IDocumentoRequeridoSolicitud;
    }
    return {
      id: value.id,
      configuracionSolicitudId: value.configuracionSolicitudId,
      tipoDocumento: value.tipoDocumento,
      observaciones: value.observaciones
    };
  }

  fromTarget(value: IDocumentoRequeridoSolicitud): IDocumentoRequeridoSolicitudBackend {
    if (!value) {
      return value as unknown as IDocumentoRequeridoSolicitudBackend;
    }
    return {
      id: value.id,
      configuracionSolicitudId: value.configuracionSolicitudId,
      tipoDocumento: value.tipoDocumento,
      observaciones: value.observaciones
    };
  }
}

export const DOCUMENTO_REQUERIDO_SOLICITUD_CONVERTER = new DocumentoRequeridoSolicitudConverter();
