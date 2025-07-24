import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoDocumentoResponse } from './tipo-documento-response';

class TipoDocumentoResponseConverter extends SgiBaseConverter<ITipoDocumentoResponse, ITipoDocumento> {
  toTarget(value: ITipoDocumentoResponse): ITipoDocumento {
    if (!value) {
      return value as unknown as ITipoDocumento;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
  fromTarget(value: ITipoDocumento): ITipoDocumentoResponse {
    if (!value) {
      return value as unknown as ITipoDocumentoResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
}

export const TIPO_DOCUMENTO_RESPONSE_CONVERTER = new TipoDocumentoResponseConverter();
