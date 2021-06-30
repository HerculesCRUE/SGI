import { ISolicitudDocumentoBackend } from '@core/models/csp/backend/solicitud-documento-backend';
import { ISolicitudDocumento } from '@core/models/csp/solicitud-documento';
import { SgiBaseConverter } from '@sgi/framework/core';

class SolicitudDocumentoConverter extends SgiBaseConverter<ISolicitudDocumentoBackend, ISolicitudDocumento> {

  toTarget(value: ISolicitudDocumentoBackend): ISolicitudDocumento {
    if (!value) {
      return value as unknown as ISolicitudDocumento;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      comentario: value.comentario,
      documentoRef: value.documentoRef,
      nombre: value.nombre,
      tipoDocumento: value.tipoDocumento
    };
  }

  fromTarget(value: ISolicitudDocumento): ISolicitudDocumentoBackend {
    if (!value) {
      return value as unknown as ISolicitudDocumentoBackend;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      comentario: value.comentario,
      documentoRef: value.documentoRef,
      nombre: value.nombre,
      tipoDocumento: value.tipoDocumento
    };
  }
}

export const SOLICITUD_DOCUMENTO_CONVERTER = new SolicitudDocumentoConverter();
