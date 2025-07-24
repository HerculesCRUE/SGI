import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ISolicitudDocumento } from '@core/models/csp/solicitud-documento';
import { ISolicitudDocumentoResponse } from '@core/services/csp/solicitud-documento/solicitud-documento-response';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_DOCUMENTO_RESPONSE_CONVERTER } from '../tipo-documento/tipo-documento-response.converter';
import { TIPO_FASE_RESPONSE_CONVERTER } from '../tipo-fase/tipo-fase-response.converter';

class SolicitudDocumentoResponseConverter extends SgiBaseConverter<ISolicitudDocumentoResponse, ISolicitudDocumento> {

  toTarget(value: ISolicitudDocumentoResponse): ISolicitudDocumento {
    if (!value) {
      return value as unknown as ISolicitudDocumento;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentario) : [],
      documentoRef: value.documentoRef,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.toTarget(value.tipoDocumento) : null,
      tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.toTarget(value.tipoFase) : null
    };
  }

  fromTarget(value: ISolicitudDocumento): ISolicitudDocumentoResponse {
    if (!value) {
      return value as unknown as ISolicitudDocumentoResponse;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentario) : [],
      documentoRef: value.documentoRef,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.fromTarget(value.tipoDocumento) : null,
      tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.fromTarget(value.tipoFase) : null
    };
  }
}

export const SOLICITUD_DOCUMENTO_RESPONSE_CONVERTER = new SolicitudDocumentoResponseConverter();
