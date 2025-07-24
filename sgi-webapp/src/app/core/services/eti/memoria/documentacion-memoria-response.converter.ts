import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { IDocumento } from '@core/models/sgdoc/documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_DOCUMENTO_RESPONSE_CONVERTER } from '../tipo-documento/tipo-documento-response.converter';
import { IDocumentacionMemoriaResponse } from './documentacion-memoria-response';
import { MEMORIA_RESPONSE_CONVERTER } from './memoria-response.converter';

class DocumentacionMemoriaResponseConverter extends SgiBaseConverter<IDocumentacionMemoriaResponse, IDocumentacionMemoria> {
  toTarget(value: IDocumentacionMemoriaResponse): IDocumentacionMemoria {
    if (!value) {
      return value as unknown as IDocumentacionMemoria;
    }
    return {
      id: value.id,
      memoria: MEMORIA_RESPONSE_CONVERTER.toTarget(value.memoria),
      tipoDocumento: TIPO_DOCUMENTO_RESPONSE_CONVERTER.toTarget(value.tipoDocumento),
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      documento: { documentoRef: value.documentoRef } as IDocumento,
    };
  }

  fromTarget(value: IDocumentacionMemoria): IDocumentacionMemoriaResponse {
    if (!value) {
      return value as unknown as IDocumentacionMemoriaResponse;
    }
    return {
      id: value.id,
      memoria: MEMORIA_RESPONSE_CONVERTER.fromTarget(value.memoria),
      tipoDocumento: TIPO_DOCUMENTO_RESPONSE_CONVERTER.fromTarget(value.tipoDocumento),
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      documentoRef: value.documento.documentoRef,
    };
  }
}

export const DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER = new DocumentacionMemoriaResponseConverter();
