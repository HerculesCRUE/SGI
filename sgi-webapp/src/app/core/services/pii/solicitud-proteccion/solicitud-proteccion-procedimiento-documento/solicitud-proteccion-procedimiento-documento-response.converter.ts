import { IProcedimiento } from '@core/models/pii/procedimiento';
import { IProcedimientoDocumento } from '@core/models/pii/procedimiento-documento';
import { IDocumento } from '@core/models/sgdoc/documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProcedimientoDocumentoResponse } from './solicitud-proteccion-procedimiento-documento-response';

class SolicitudProteccionProcedimientoDocumentoResponseConverter
  extends SgiBaseConverter<IProcedimientoDocumentoResponse, IProcedimientoDocumento>{
  toTarget(value: IProcedimientoDocumentoResponse): IProcedimientoDocumento {
    if (!value) {
      return value as unknown as IProcedimientoDocumento;
    }
    return {
      id: value.id,
      documento: { documentoRef: value.documentoRef } as IDocumento,
      nombre: value.nombre,
      procedimiento: { id: value.id } as IProcedimiento
    };
  }

  fromTarget(value: IProcedimientoDocumento): IProcedimientoDocumentoResponse {
    if (!value) {
      return value as unknown as IProcedimientoDocumentoResponse;
    }
    return {
      id: value.id,
      documentoRef: value.documento?.documentoRef,
      nombre: value.nombre,
      procedimientoId: value.procedimiento?.id
    };
  }
}

export const SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_RESPONSE_CONVERTER =
  new SolicitudProteccionProcedimientoDocumentoResponseConverter();
