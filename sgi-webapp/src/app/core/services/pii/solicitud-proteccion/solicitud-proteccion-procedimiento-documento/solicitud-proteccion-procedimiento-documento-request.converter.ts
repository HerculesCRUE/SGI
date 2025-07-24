import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProcedimiento } from '@core/models/pii/procedimiento';
import { IProcedimientoDocumento } from '@core/models/pii/procedimiento-documento';
import { IDocumento } from '@core/models/sgdoc/documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProcedimientoDocumentoRequest } from './solicitud-proteccion-procedimiento-documento-request';

export class SolicitudProteccionProcedimientoDocumentoRequestConverter
  extends SgiBaseConverter<IProcedimientoDocumentoRequest, IProcedimientoDocumento> {

  toTarget(value: IProcedimientoDocumentoRequest): IProcedimientoDocumento {

    return value ? {
      id: undefined,
      documento: {
        documentoRef: value.documentoRef
      } as IDocumento,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      procedimiento: {
        id: value.procedimientoId
      } as IProcedimiento
    } : (value as unknown as IProcedimientoDocumento);
  }

  fromTarget(value: IProcedimientoDocumento): IProcedimientoDocumentoRequest {
    return value ? {
      documentoRef: value.documento?.documentoRef,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
      procedimientoId: value.procedimiento?.id
    } as IProcedimientoDocumentoRequest : value as unknown as IProcedimientoDocumentoRequest;
  }

}

export const SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_REQUEST_CONVERTER
  = new SolicitudProteccionProcedimientoDocumentoRequestConverter();
