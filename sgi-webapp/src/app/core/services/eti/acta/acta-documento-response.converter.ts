import { Language } from '@core/i18n/language';
import { IActaDocumento } from '@core/models/eti/acta-documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IActaDocumentoResponse } from './acta-documento-response';

class ActaDocumentoResponseConverter
  extends SgiBaseConverter<IActaDocumentoResponse, IActaDocumento> {
  toTarget(value: IActaDocumentoResponse): IActaDocumento {
    if (!value) {
      return value as unknown as IActaDocumento;
    }
    return {
      actaId: value.actaId,
      documentoRef: value.documentoRef,
      lang: Language.fromCode(value.lang),
      transaccionRef: value.transaccionRef
    };
  }

  fromTarget(value: IActaDocumento): IActaDocumentoResponse {
    if (!value) {
      return value as unknown as IActaDocumentoResponse;
    }
    return {
      actaId: value.actaId,
      documentoRef: value.documentoRef,
      lang: value.lang.code.toUpperCase(),
      transaccionRef: value.transaccionRef
    };
  }
}

export const ACTA_DOCUMENTO_RESPONSE_CONVERTER = new ActaDocumentoResponseConverter();
