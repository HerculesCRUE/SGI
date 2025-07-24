import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoriaDocumento } from '@core/models/csp/convocatoria-documento';
import { IConvocatoriaDocumentoResponse } from '@core/services/csp/convocatoria-documento/convocatoria-documento-response';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_DOCUMENTO_RESPONSE_CONVERTER } from '../tipo-documento/tipo-documento-response.converter';
import { TIPO_FASE_RESPONSE_CONVERTER } from '../tipo-fase/tipo-fase-response.converter';

class ConvocatoriaDocumentoResponseConverter extends SgiBaseConverter<IConvocatoriaDocumentoResponse, IConvocatoriaDocumento> {

  toTarget(value: IConvocatoriaDocumentoResponse): IConvocatoriaDocumento {
    if (!value) {
      return value as unknown as IConvocatoriaDocumento;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      documentoRef: value.documentoRef,
      tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.toTarget(value.tipoFase) : null,
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.toTarget(value.tipoDocumento) : null,
      publico: value.publico,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : []
    };
  }

  fromTarget(value: IConvocatoriaDocumento): IConvocatoriaDocumentoResponse {
    if (!value) {
      return value as unknown as IConvocatoriaDocumentoResponse;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      documentoRef: value.documentoRef,
      tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.fromTarget(value.tipoFase) : null,
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.fromTarget(value.tipoDocumento) : null,
      publico: value.publico,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : []
    };
  }
}

export const CONVOCATORIA_DOCUMENTO_RESPONSE_CONVERTER = new ConvocatoriaDocumentoResponseConverter();
